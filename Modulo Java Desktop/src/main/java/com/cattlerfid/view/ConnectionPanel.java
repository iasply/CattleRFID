package com.cattlerfid.view;

import com.cattlerfid.controller.ConnectionController;
import com.cattlerfid.controller.LoginController;
import com.cattlerfid.service.AuthenticationService;
import com.cattlerfid.service.SerialService;

import javax.swing.*;
import java.awt.*;

public class ConnectionPanel extends JPanel implements ConnectionController.ConnectionViewListener {

    private final ConnectionController controller;
    private final AuthenticationService authService;
    private final NavigationManager navManager;

    private JLabel statusLabel;
    private JComboBox<String> portSelector;
    private JButton connectPortButton;
    private JButton disconnectButton;
    private JButton testReadButton;

    public ConnectionPanel(ConnectionController controller, AuthenticationService authService,
            NavigationManager navManager) {
        this.controller = controller;
        this.authService = authService;
        this.navManager = navManager;
        this.controller.setViewListener(this);

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        // Panels do not have setTitle, pack, etc.

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Configuração de Hardware", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logButton = new JButton("Ver Logs Serial");
        logButton.setFont(new Font("Arial", Font.PLAIN, 10));
        logButton.addActionListener(e -> {
            SerialLogFrame logFrame = new SerialLogFrame(controller.getSerialService());
            logFrame.setVisible(true);
        });
        headerPanel.add(logButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Config e Status)
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        portPanel.add(new JLabel("Porta Serial (Arduino):"));
        portSelector = new JComboBox<>(SerialService.getAvailablePorts());
        portPanel.add(portSelector);

        connectPortButton = new JButton("Conectar");
        connectPortButton.addActionListener(e -> connectSerial());
        portPanel.add(connectPortButton);

        disconnectButton = new JButton("Desconectar");
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(e -> controller.disconnectSerial());
        portPanel.add(disconnectButton);

        centerPanel.add(portPanel);

        statusLabel = new JLabel("Aguardando conexão...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.DARK_GRAY);
        centerPanel.add(statusLabel);

        testReadButton = new JButton("Realizar Teste Inicial de Leitura");
        testReadButton.setFont(new Font("Arial", Font.BOLD, 14));
        testReadButton.setEnabled(false);
        testReadButton.addActionListener(e -> controller.requestTestRead());
        centerPanel.add(testReadButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void connectSerial() {
        if (portSelector.getSelectedItem() != null) {
            String port = portSelector.getSelectedItem().toString();
            statusLabel.setText("Conectando na " + port + "...");
            connectPortButton.setEnabled(false);
            portSelector.setEnabled(false);

            // Operação assíncrona para não freezar a Interface Gráfica com a API Serial
            // travando em IO
            new Thread(() -> {
                controller.startSerialConnection(port);
            }).start();
        }
    }

    @Override
    public void onSerialConnected() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Arduino Conectado! Faça um teste de leitura.");
            statusLabel.setForeground(new Color(0, 150, 0)); // Verde escuro
            connectPortButton.setEnabled(false);
            portSelector.setEnabled(false);
            disconnectButton.setEnabled(true);
            testReadButton.setEnabled(true);
        });
    }

    @Override
    public void onSerialDisconnected() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Desconectado. Aguardando conexão...");
            statusLabel.setForeground(Color.DARK_GRAY);
            connectPortButton.setEnabled(true);
            portSelector.setEnabled(true);
            disconnectButton.setEnabled(false);
            testReadButton.setEnabled(false);
            testReadButton.setText("Realizar Teste Inicial de Leitura");
        });
    }

    @Override
    public void onSerialError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(Color.RED);

            if (message.startsWith("Não foi possível conectar")) {
                connectPortButton.setEnabled(true);
                portSelector.setEnabled(true);
                if (disconnectButton != null)
                    disconnectButton.setEnabled(false);
            }

            testReadButton.setEnabled(true);
            testReadButton.setText("Realizar Teste Inicial de Leitura");
            JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void onWaitingForTestTag() {
        SwingUtilities.invokeLater(() -> {
            testReadButton.setText("Lendo... Aproxime qualquer tag");
            statusLabel.setText("Aguardando leitura do RFID (Timeout 2.5s)...");
            statusLabel.setForeground(Color.BLUE);
        });
    }

    @Override
    public void onTestTagReadSuccess(String tagContent) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Teste concluído com sucesso!\nConteúdo Lido: " + tagContent + "\nAvançando para o Login.",
                    "Hardware Validado", JOptionPane.INFORMATION_MESSAGE);

            controller.detachSerial();

            LoginController loginController = new LoginController(authService, controller.getSerialService());
            LoginPanel loginPanel = new LoginPanel(loginController, navManager);

            navManager.showPanel("Login", loginPanel);

            // Religa o canal Serial ouvindo direto pro LoginController
            loginController.attachToActiveSerial();
        });
    }
}
