 /*******************************************************************************
  Leitura e gravacao de dados usando o Kit RFID MFRC522 (v2.0)

  Codigo de exemplo para leitura e gravacao de dados em uma tag

  Copyright 2026 RoboCore.
  Escrito por Carlos Daniel (09/02/2026).

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version (<https://www.gnu.org/licenses/>).
*******************************************************************************/

// Adicao de bibliotecas
#include <SPI.h>

// RFID_MFRC522v2
#include <MFRC522v2.h>
#include <MFRC522DriverSPI.h>
#include <MFRC522DriverPinSimple.h>
#include <MFRC522Debug.h>

const int PINO_SDA = 10;  // SS / SDA (Chip Select) do RC522

// Cria o driver para o pino de selecao (SS/CS)
// e outro driver para a comunicacao SPI e entao cria o objeto MFRC522
MFRC522DriverPinSimple ssPin(PINO_SDA);
MFRC522DriverSPI driver(ssPin, SPI);
MFRC522 mfrc522(driver);

// Estados globais da aplicacao
enum EstadoOperacao {
  MODO_ESPERA,
  MODO_LEITURA,
  MODO_GRAVACAO
};
EstadoOperacao estadoAtual = MODO_ESPERA; // Inicia aguardando comando

// Cria uma instancia de chave que sera usada para acessar a tag
MFRC522::MIFARE_Key chave;

// Criacao das variaveis do tipo Byte
byte bloco = 4;     // Define qual bloco da tag vai ser utilizado para a leitura ou gravacao
byte buffer[18];    // Armazena temporariamente no programa o conteudo lido da tag
byte tamanho = 18;  // Define o total de caracteres a serem lidos ou gravados

byte texto[16];  // Buffer responsavel por armazenar o texto que vai ser gravado na tag (recebido do Java)

// Autenticacao Key A (MIFARE Classic)
const byte AUTH_KEY_A = 0x60;

// Variaveis para repetirmos a deteccao/leitura para uma melhor precisao
const byte MAX_TENTATIVAS = 25;
const unsigned long TIMEOUT_MS = 2500; // 2.5s de timeout e suficiente para aguardar a tag
const unsigned long PAUSA_MS = 15;

// Estados das operacoes
bool tagSelecionada = false;
bool autenticado = false;
bool leituraOK = false;
bool gravacaoOK = false;

void setup() {
  Serial.begin(9600);  // Inicializacao do monitor serial
  SPI.begin();         // Inicializacao da comunicacao SPI
  mfrc522.PCD_Init();  // Inicializacao do leitor RFID

  // Chave padrao (FF FF FF FF FF FF)
  for (byte i = 0; i < 6; i++) {
    chave.keyByte[i] = 0xFF;
  }
}

void loop() {
  if (Serial.available() > 0) {
    // Le a string ate encontrar a quebra de linha (\n)
    String req = Serial.readStringUntil('\n');
    req.trim(); // Remove espacos e \r

    if (req.startsWith("<") && req.endsWith(">")) {
      String cmdBody = req.substring(1, req.length() - 1); // Extrai o miolo: READ ou WRITE:Payload

      if (cmdBody == "READ") {
        estadoAtual = MODO_LEITURA;
        leitura(); // Executa e manda a resposta diretamente
        estadoAtual = MODO_ESPERA;
      }
      else if (cmdBody.startsWith("WRITE:")) {
        estadoAtual = MODO_GRAVACAO;
        // Pega a string depois de 'WRITE:'
        String payload = cmdBody.substring(6);

        // Limpa o buffer de gravacao e copia o payload (ate 16 bytes)
        memset(texto, ' ', 16); // Preenche com espacos se for menor que 16
        for(int i = 0; i < payload.length() && i < 16; i++) {
          texto[i] = payload[i];
        }

        gravacao(); // Executa e manda a resposta diretamente
        estadoAtual = MODO_ESPERA;
      }
      else {
        responder("ERR:INVALID_CMD");
      }
    }
  }
}

// Funcao auxiliar para sempre responder no formato correto e enviar a versao do firmware junto
void responder(String mensagem) {
  // Ex: <RES:OK:WROTE:FW:92>
  Serial.print("<RES:");
  Serial.print(mensagem);
  Serial.print(":FW:");
  byte v = driver.PCD_ReadRegister(MFRC522Constants::VersionReg);
  Serial.print(v, HEX);
  Serial.println(">");
}

// Funcao para detectar e selecionar a TAG, autenticar o bloco e ler os 16 bytes do bloco
void leitura() {
  esperarTagESelecionar();
  if (!tagSelecionada) {
    responder("ERR:NO_TAG");
    return;
  }

  autenticarBloco();
  if (!autenticado) {
    responder("ERR:AUTH");
    finalizaOperacao();
    return;
  }

  byte dados[16];
  lerBloco16(dados);

  if (!leituraOK) {
    responder("ERR:READ_FAILED");
    finalizaOperacao();
    return;
  }

  // Monta a string de dados lidos
  String payload = "OK:";
  
  // Mostra os bytes lidos sem espacos extras
  for (byte i = 0; i < 16; i++) {
    // Filtra caracteres nao imprimiveis se quiser, ou apenas os imprime
    if (dados[i] >= 32 && dados[i] <= 126) {
      payload += (char)dados[i];
    } else {
      payload += " "; // Troca nulo por espaco de forma segura pro Java ler
    }
  }

  responder(payload);
  finalizaOperacao();
}

// Funcao para detectar e selecionar a TAG, gravar o texto no bloco da TAG e confirmar se foi gravado com sucesso
void gravacao() {
  esperarTagESelecionar();
  if (!tagSelecionada) {
    responder("ERR:NO_TAG");
    return;
  }

  // Para confirmar a gravacao, lemos o mesmo bloco novamente e comparamos byte a byte.
  gravarBloco16Verificado(texto);

  if (gravacaoOK) {
    responder("OK:WROTE");
  } else {
    responder("ERR:WRITE_FAILED");
  }

  finalizaOperacao();
}

void limpaEstado() {
  mfrc522.PCD_StopCrypto1();
  delay(5);
}

void finalizaOperacao() {
  mfrc522.PCD_StopCrypto1();
  mfrc522.PICC_HaltA();
  delay(5);
}

// Confirmar se o que foi lido e igual ao que foi gravado.
bool igual16(const byte a[16], const byte b[16]) {
  for (byte i = 0; i < 16; i++) {
    if (a[i] != b[i]) {
      return false;
    }
  }
  return true;
}

// Detecao + selecao de TAG
void esperarTagESelecionar() {
  tagSelecionada = false;
  unsigned long inicio = millis();

  while (millis() - inicio < TIMEOUT_MS) {
    for (byte t = 0; t < MAX_TENTATIVAS; t++) {

      byte atqa[2];
      byte atqaSize = sizeof(atqa);

      bool detectou =
        mfrc522.PICC_IsNewCardPresent() || (mfrc522.PICC_RequestA(atqa, &atqaSize) == 0) || (mfrc522.PICC_WakeupA(atqa, &atqaSize) == 0);

      // Se detectou, tenta selecionar (ler UID)
      if (detectou && mfrc522.PICC_ReadCardSerial()) {
        tagSelecionada = true;
        return;
      }

      limpaEstado();
      delay(PAUSA_MS);
    }
    delay(20);
  }
}

// Autenticacao do bloco da TAG (com repeticoes)
void autenticarBloco() {
  autenticado = false;

  for (byte t = 0; t < MAX_TENTATIVAS; t++) {

    // Retorno 0 significa sucesso (OK)
    byte status = mfrc522.PCD_Authenticate(AUTH_KEY_A, bloco, &chave, &(mfrc522.uid));
    if (status == 0) {
      autenticado = true;
      return;
    }

    limpaEstado();
    delay(PAUSA_MS);

    esperarTagESelecionar();
    if (!tagSelecionada) {
      return;
    }
  }
}

// Leitura de 16 bytes do bloco
void lerBloco16(byte saida[16]) {
  leituraOK = false;

  for (byte t = 0; t < MAX_TENTATIVAS; t++) {

    tamanho = 18;
    byte status = mfrc522.MIFARE_Read(bloco, buffer, &tamanho);

    // Retorno 0 significa sucesso (OK)
    if (status == 0) {
      for (byte i = 0; i < 16; i++) {
        saida[i] = buffer[i];
      }
      leituraOK = true;
      return;
    }

    limpaEstado();
    delay(PAUSA_MS);

    esperarTagESelecionar();
    if (!tagSelecionada) {
      return;
    }

    autenticarBloco();
    if (!autenticado) {
      return;
    }
  }
}

// Grava e depois le o mesmo bloco e compara 16 bytes
void gravarBloco16Verificado(const byte entrada[16]) {
  gravacaoOK = false;

  for (byte t = 0; t < MAX_TENTATIVAS; t++) {

    autenticarBloco();
    if (!autenticado) {
      limpaEstado();
      delay(PAUSA_MS);

      esperarTagESelecionar();
      if (!tagSelecionada) {
        return;
      }

      continue;
    }

    // Grava 16 bytes
    mfrc522.MIFARE_Write(bloco, (byte*)entrada, 16);

    // Confirma lendo e comparando byte a byte
    byte ver[16];
    lerBloco16(ver);

    if (leituraOK && igual16(ver, entrada)) {
      gravacaoOK = true;
      return;
    }

    limpaEstado();
    delay(PAUSA_MS);

    esperarTagESelecionar();
    if (!tagSelecionada) {
      return;
    }
  }
}
    