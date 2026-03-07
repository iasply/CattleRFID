package com.cattlerfid.service;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.OutputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SerialService {

    private SerialPort activePort;
    private OutputStream outputStream;
    private final List<Consumer<String>> messageListeners = new ArrayList<>();
    private final StringBuilder messageBuffer = new StringBuilder(); // Buffer para as mensagens seriais

    // Logs
    private final List<String> logHistory = new ArrayList<>();
    private final List<Consumer<String>> logListeners = new ArrayList<>();

    private void appendLog(String origin, String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String entry = String.format("[%s] %-5s %s", time, origin, message);
        logHistory.add(entry);
        for (Consumer<String> listener : logListeners) {
            listener.accept(entry);
        }
    }

    public List<String> getLogHistory() {
        return new ArrayList<>(logHistory);
    }

    public void addLogListener(Consumer<String> listener) {
        if (!logListeners.contains(listener)) {
            logListeners.add(listener);
        }
    }

    public void removeLogListener(Consumer<String> listener) {
        logListeners.remove(listener);
    }

    // Inicia a porta Serial. Retorna true se conectou.
    public boolean connect(String portName) {
        activePort = SerialPort.getCommPort(portName);
        activePort.setComPortParameters(9600, 8, 1, 0); // 9600 baud rate, 8 bits de dados, 1 bit de parada, sem
                                                        // paridade
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

        if (activePort.openPort()) {
            outputStream = activePort.getOutputStream();
            setupListener();
            return true;
        }
        return false;
    }

    public void disconnect() {
        if (activePort != null && activePort.isOpen()) {
            activePort.removeDataListener();
            activePort.closePort();
        }
    }

    public boolean isOpen() {
        return activePort != null && activePort.isOpen();
    }

    public void addMessageListener(Consumer<String> listener) {
        if (!messageListeners.contains(listener)) {
            messageListeners.add(listener);
        }
    }

    public void removeMessageListener(Consumer<String> listener) {
        messageListeners.remove(listener);
    }

    // Envia o comando de leitura pro Arduino: <READ>\n
    public void requestRead() {
        sendCommand("<READ>\n");
    }

    // Envia o comando de gravacao pro Arduino (ate 16 chars): <WRITE:Texto>\n
    public void requestWrite(String data) {
        if (data.length() > 16) {
            data = data.substring(0, 16);
        }
        sendCommand("<WRITE:" + data + ">\n");
    }

    // Funcao generica para mandar Bytes pra porta OUt
    public void sendCommand(String command) {
        if (isOpen()) {
            try {
                appendLog("OUT", command.trim());
                outputStream.write(command.getBytes());
                outputStream.flush();
            } catch (Exception e) {
                appendLog("ERROR", "Falha ao enviar: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            appendLog("ERROR", "Porta fechada. Tentou enviar: " + command.trim());
            System.err.println("Porta Serial não esta aberta para enviar comando: " + command);
        }
    }

    // Escuta assincronamente a porta Serial usando a Scanner
    private void setupListener() {
        activePort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                try {
                    byte[] newData = new byte[activePort.bytesAvailable()];
                    int numRead = activePort.readBytes(newData, newData.length);
                    for (int i = 0; i < numRead; i++) {
                        char c = (char) newData[i];

                        // Ignora quebra de linhas malucas no meio do payload enviadas pelo println do
                        // Arduino
                        if (c == '\r' || c == '\n') {
                            continue;
                        }

                        messageBuffer.append(c);

                        // O Arduino envia < no começo e > no fim. Vamos ler ate fechar o >.
                        if (c == '>') {
                            String message = messageBuffer.toString().trim();
                            messageBuffer.setLength(0); // Limpa o buffer para a proxima

                            if (!message.isEmpty()) {
                                appendLog("IN", message);

                                // Valida se é o nosso pacote esperado
                                if (message.startsWith("<") && message.endsWith(">")) {
                                    message = message.substring(1, message.length() - 1); // Remove os <>
                                    for (Consumer<String> listener : messageListeners) {
                                        listener.accept(message);
                                    }
                                } else {
                                    appendLog("WARN", "Pacote incompleto ignorado: " + message);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Usado pra testar localmente listando portas disponiveis
    public static String[] getAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] portNames = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            portNames[i] = ports[i].getSystemPortName();
        }
        return portNames;
    }
}
