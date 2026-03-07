package com.cattlerfid.view;

import com.cattlerfid.controller.CattleController;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.controller.LoginController;
import com.cattlerfid.service.AuthenticationService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements CattleController.CattleViewListener {

    private final User loggedUser;
    private final CattleController cattleController;

    private JLabel statusLabel;
    private JButton scanCattleButton;

    public MainFrame(User loggedUser, CattleController cattleController) {
        this.loggedUser = loggedUser;
        this.cattleController = cattleController;
        this.cattleController.setViewListener(this); // Assume controle dos callbacks

        setupUI();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupUI() {
        setTitle("Sistema de Vacinação - Menu Principal");
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(600, 400));

        // Topo / Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.DARK_GRAY);

        JLabel welcomeLabel = new JLabel("  Usuário: " + loggedUser.getFullName() + " (VET)");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Botão de Logout para Retornar ao LoginFrame
        JButton logoutButton = new JButton("Sair (Logout)");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deslogar do sistema?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.setVisible(false);
                this.dispose();
                cattleController.detachSerial();

                // Reconstroi serviço e abre a tela pura de Login pendurada na mesma serial viva
                AuthenticationService authService = new AuthenticationService();
                LoginController loginController = new LoginController(authService, cattleController.getSerialService());
                LoginFrame loginFrame = new LoginFrame(loginController);
                loginFrame.setVisible(true);
                loginController.attachToActiveSerial();
            }
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Centro (Botoes Gigantes)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 80));

        scanCattleButton = new JButton("<html><center>LER TAG DO ANIMAL<br>(Aproximar Leitor)</center></html>");
        scanCattleButton.setPreferredSize(new Dimension(200, 100));
        scanCattleButton.setFont(new Font("Arial", Font.BOLD, 14));
        scanCattleButton.addActionListener(e -> {
            statusLabel.setText("Aproxime a Tag do Animal...");
            cattleController.requestReadTag();
        });

        JButton listButton = new JButton("<html><center>LISTAR GADO<br>(Mock Database)</center></html>");
        listButton.setPreferredSize(new Dimension(200, 100));
        listButton.setFont(new Font("Arial", Font.BOLD, 14));
        listButton.addActionListener(e -> {
            CattleListFrame listFrame = new CattleListFrame(cattleController.getApiService(), cattleController);
            listFrame.setVisible(true);
        });

        centerPanel.add(scanCattleButton);
        centerPanel.add(listButton);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" Sistema Pronto.", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createBevelBorder(1));

        JButton logButton = new JButton("Ver Logs Serial");
        logButton.setFont(new Font("Arial", Font.BOLD, 12));
        logButton.addActionListener(e -> {
            SerialLogFrame logFrame = new SerialLogFrame(cattleController.getSerialService());
            logFrame.setVisible(true);
        });

        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        bottomPanel.add(logButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- Callbacks do CattleController ---

    @Override
    public void onRfidReadSuccess(Cattle cattle, boolean isNew) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText((isNew ? "Novo Animal " : "Animal Encontrado ") + " (" + cattle.getRfidTag() + ")");
            // Abre o formulário enviando os dados pro preenchimento
            System.out.println("-> Abrindo Formulário do Gado para: " + cattle.getRfidTag());
            CattleFormFrame form = new CattleFormFrame(cattle, isNew, cattleController, loggedUser);
            form.setVisible(true);
        });
    }

    @Override
    public void onRfidReadError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Erro de Leitura: " + message);
            JOptionPane.showMessageDialog(this, message, "Aviso RFID", JOptionPane.WARNING_MESSAGE);
        });
    }

    @Override
    public void onRfidWriteSuccess() {
        // Ignorado no MainMenu, usado no FormView
        SwingUtilities.invokeLater(() -> statusLabel.setText("Tag gravada com sucesso!"));
    }

    @Override
    public void onRfidWriteError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Erro Gravação: " + message);
        });
    }

    @Override
    public void onApiSaveSuccess() {
        SwingUtilities.invokeLater(() -> statusLabel.setText("Dados do Animal salvos na base!"));
    }

    @Override
    public void onApiSaveError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Falha no Banco: " + message);
        });
    }
}
