# Arduino RFID v2 - Protocolo de Comunicação Espelho (Java <-> Arduino)

Este documento explica como o componente Java deve se comunicar com o Arduino usando o protocolo Serial.

## Configuração da Porta
- **Baud Rate:** `9600`
- **Pinos (Arduino Nano / Uno):**
  - SDA (SS): 10
  - SCK: 13
  - MOSI: 11
  - MISO: 12
  - RST: 9 (ou diretamente no 3.3V)
- **Importante:** Quando a porta serial é aberta no Java, o Arduino irá reiniciar. Aguarde sempre pelo menos **2000 milissegundos (2s)** antes de enviar o primeiro comando após o `port.openPort()`.

---

## Formato das Requisições (Java para Arduino)
As mensagens DEVEM começar com o marcador de início `<` e terminar com o marcador de encerramento `>\n`. (onde `\n` é o caractere de nova linha/LF).

### 1. Comando de Leitura
Pede ao Arduino para aguardar uma Tag (timeout de 2,5 segundos) e devolver a string contida nela.
- **Payload Literal:** `<READ>\n`

### 2. Comando de Gravação
Pede ao Arduino para aguardar a aproximação de uma Tag e gravar até 16 caracteres de dados nela.
- **Formato Exigido (16 Bytes):** O sistema Java exige formato estrito: `[Prefixo_1_Byte][7_Espacos][ID_8_Bytes]`.
  - **Exemplo Veterinário:** `<WRITE:V       12345678>\n`
  - **Exemplo Animal:** `<WRITE:C       87654321>\n`
- *Nota: Se a string for menor que 16 caracteres, o Arduino preencherá o resto da memória do bloco automaticamente com espaços em branco, mas o Java rejeitará a leitura futuramente.*

---

## Formato das Respostas (Arduino para Java)
Para facilitar o processamento em Java, o Arduino sempre responderá usando um prefixo base `<RES:`. 
Todas as respostas incluem o sufixo `:FW:Versão_Do_Chip_RC522>` no final, permitindo que a aplicação Java saiba imediatamente qual é a versão de firmware da placa física e detecte cabos desconectados (quando o firmware for lido como algo do tipo 0x00).

### Respostas de Sucesso
*   `<RES:OK:C       12345678:FW:92>` (Resposta à operação `READ`)
*   `<RES:OK:WROTE:FW:92>` (Resposta à operação `WRITE:...`)

### Respostas de Erro (Tratamento Essencial no Java)
*   `<RES:ERR:NO_TAG:FW:...>`
    *   **Significado:** Cerca de 2,5 segundos se passaram e nenhuma tag foi detectada nas proximidades da antena.
*   `<RES:ERR:AUTH:FW:...>`
    *   **Significado:** A tag foi detectada, mas a senha padrão de fábrica (`0xFF`) falhou para acessar os blocos do cartão. É possível que o cartão esteja bloqueado ou vazio.
*   `<RES:ERR:READ_FAILED:FW:...>`
    *   **Significado:** A tag estava no alcance, se autenticou, mas aconteceu um erro físico na hora de extrair os 16 bytes. Evite mover a tag muito rápido.
*   `<RES:ERR:WRITE_FAILED:FW:...>`
    *   **Significado:** A operação de gravação parecia ter funcionado, mas na rotina de "Gravar -> Ler de volta para conferir", os dados divergiram.
*   `<RES:ERR:INVALID_CMD:FW:...>`
    *   **Significado:** O Java enviou um comando formatado incorretamente.

---

### Exemplo de Parser Sintético (Código Java Genérico)
```java
String linha = serialPort.readStringUntil('\n');

if (linha.startsWith("<RES:") && linha.endsWith(">")) {
    linha = linha.substring(1, linha.length() - 1); // Remove os delimitadores <>
    String[] parts = linha.split(":");
    
    // parts[0] = "RES"
    // parts[1] = "OK" ou "ERR"
    
    if (parts[1].equals("OK")) {
        // Se a resposta for "WROTE", foi uma escrita bem-sucedida
        if (parts.length > 2 && parts[2].equals("WROTE")) {
           System.out.println("Gravado com sucesso na memoria!");
        } 
        // Caso contrario, é uma resposta de leitura com sucesso contendo o dado
        else if (parts.length > 2) {
           String dadosTag = parts[2].trim();
           System.out.println("Lido com sucesso: " + dadosTag);
        }
    } else {
        String codigoErro = parts[2];
        System.out.println("Erro reportado do Arduino: " + codigoErro);
    }
}
```
