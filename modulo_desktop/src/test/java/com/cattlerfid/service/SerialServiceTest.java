package com.cattlerfid.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerialServiceTest {

    // Como o jSerialComm aciona diretamente drivers USB de Hardware (JNI) e estamos
    // no TDD,
    // nao da pra testar uma porta COM1 com facilidade sem "Mockar" toda a JNI
    // framework native.
    // Porem podemos testar os manipuladores criados dentro de uma subclasse mock em
    // memoria.

    @Test
    void testRequestReadCommandFormat() {
        MockSerialService mockService = new MockSerialService();
        mockService.requestRead();
        assertEquals("<READ>\n", mockService.getLastSentCommand());
    }

    @Test
    void testRequestWriteCommandFormat() {
        MockSerialService mockService = new MockSerialService();
        mockService.requestWrite("JoaoSilva123");
        assertEquals("<WRITE:JoaoSilva123>\n", mockService.getLastSentCommand());
    }

    @Test
    void testRequestWriteCommandFormat_TruncatesAt16Chars() {
        MockSerialService mockService = new MockSerialService();
        String hugePayload = "BoiBandido123456789"; // 19 caracteres
        mockService.requestWrite(hugePayload);
        assertEquals("<WRITE:BoiBandido123456>\n", mockService.getLastSentCommand()); // Exatos 16 caracteres cortados
    }

    @Test
    void testIncomingMessageParsing() {
        MockSerialService mockService = new MockSerialService();
        AtomicReference<String> receivedParsedMessage = new AtomicReference<>("");

        // Simula a linha bruta do Arduino: <RES:OK:João :FW:92>
        mockService.simulateArduinoIncomingLine("<RES:OK:João :FW:92>", receivedParsedMessage::set);

        // O servico Serial tem que cortar os <> (brackets) que sao do protocolo
        assertEquals("RES:OK:João :FW:92", receivedParsedMessage.get());
    }

    class MockSerialService extends SerialService {
        private String lastSentCommand = "";

        @Override
        public boolean connect(String portName) {
            return true; // Simula sucesso sempre
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public void sendCommand(String command) {
            this.lastSentCommand = command; // Captura pra validacao
        }

        public String getLastSentCommand() {
            return lastSentCommand;
        }

        // Metodo pra simular entrada fake vinda do Arduino, chamando o callback
        public void simulateArduinoIncomingLine(String line, java.util.function.Consumer<String> callback) {
            if (line.startsWith("<") && line.endsWith(">")) {
                callback.accept(line.substring(1, line.length() - 1));
            }
        }
    }
}
