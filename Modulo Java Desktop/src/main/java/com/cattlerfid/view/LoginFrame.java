package com.cattlerfid.view;

import com.cattlerfid.controller.LoginController;
import com.cattlerfid.controller.ConnectionController;
import com.cattlerfid.service.AuthenticationService;
import com.cattlerfid.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame implements LoginController.LoginViewListener {

    private final LoginController controller;

    private JLabel statusLabel;
    private JButton readCardButton;

    public LoginFrame(LoginController controller) {
        this.controller = controller;
        this.controller.setViewListener(this);

        setupUI();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupUI() {
        setTitle("Sistema de Vacinação - RFID Login");
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(450, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Acesso via Crachá RFID", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("< Voltar");
        backButton.setFont(new Font("Arial", Font.PLAIN, 10));
        backButton.addActionListener(e -> {
            setVisible(false);
            dispose();
            controller.detachSerial();

            // Desliga a porta para liberá-la antes de voltar pro scanner raw de hardware
            controller.getSerialService().disconnect();

            AuthenticationService authService = new AuthenticationService();
            ConnectionController connController = new ConnectionController(controller.getSerialService());
            ConnectionFrame connFrame = new ConnectionFrame(connController, authService);
            connFrame.setVisible(true);
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JButton logButton = new JButton("Ver Logs Serial");
        logButton.setFont(new Font("Arial", Font.PLAIN, 10));
        logButton.addActionListener(e -> {
            SerialLogFrame logFrame = new SerialLogFrame(controller.getSerialService());
            logFrame.setVisible(true);
        });
        headerPanel.add(logButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Status e Leitura)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusLabel = new JLabel("Aguardando conexão...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.DARK_GRAY);
        centerPanel.add(statusLabel);

        readCardButton = new JButton("Aproximar Crachá (READ)");
        readCardButton.setFont(new Font("Arial", Font.BOLD, 14));
        readCardButton.setEnabled(false);
        readCardButton.addActionListener(e -> controller.requestCardLogin());
        centerPanel.add(readCardButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    // Callbacks do Controller
    @Override
    public void onLoginSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Bem-vindo(a), " + user.getFullName() + "!",
                    "Acesso Liberado", JOptionPane.INFORMATION_MESSAGE);

            // Sucesso! Esconde esta tela e abre a MainFrame
            this.setVisible(false);
            controller.detachSerial();

            // Instancia o repositorio e controller global do sistema
            com.cattlerfid.service.CattleApiService apiService = new com.cattlerfid.service.CattleApiService();
            com.cattlerfid.controller.CattleController cattleController = new com.cattlerfid.controller.CattleController(
                    apiService, controller.getSerialService());

            MainFrame main = new MainFrame(user, cattleController);
            main.setVisible(true);
        });
    }

    @Override
    public void onLoginError(String message) {
        SwingUtilities.invokeLater(() -> {
            readCardButton.setEnabled(true);
            readCardButton.setText("Aproximar Crachá (READ)");
            statusLabel.setText(message);
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, message, "Erro de Login", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void onSerialConnected() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Arduino Conectado! Por favor, leia seu crachá.");
            statusLabel.setForeground(new Color(0, 150, 0)); // Verde escuro
            readCardButton.setEnabled(true);
        });
    }

    @Override
    public void onSerialError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, message, "Erro da Porta", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void onWaitingForCard() {
        SwingUtilities.invokeLater(() -> {
            readCardButton.setText("Lendo... Aproxime o cartão");
            statusLabel.setText("Aguardando leitura do RFID (Timeout 2.5s)...");
            statusLabel.setForeground(Color.BLUE);
        });
    }
}
