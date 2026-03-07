package com.cattlerfid.view;

import com.cattlerfid.controller.CattleController;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.controller.LoginController;
import com.cattlerfid.service.AuthenticationService;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel implements CattleController.CattleViewListener {

    private final User loggedUser;
    private final CattleController cattleController;
    private final NavigationManager navManager;

    private JLabel statusLabel;
    private JButton scanCattleButton;

    // Referencia da ultima tela ativa para callbacks
    private CattleFormPanel activeCattleForm;

    public MainPanel(User loggedUser, CattleController cattleController, NavigationManager navManager) {
        this.loggedUser = loggedUser;
        this.cattleController = cattleController;
        this.navManager = navManager;
        this.cattleController.setViewListener(this); // Assume controle dos callbacks

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        // Topo / Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.DARK_GRAY);

        JLabel welcomeLabel = new JLabel("  Usuário: " + loggedUser.getFullName() + " (VET)");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Botão de Logout para Retornar ao LoginPanel
        JButton logoutButton = new JButton("Sair (Logout)");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deslogar do sistema?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                cattleController.detachSerial();

                // Reconstroi serviço e abre a tela pura de Login pendurada na mesma serial viva
                AuthenticationService authService = new AuthenticationService();
                LoginController loginController = new LoginController(authService, cattleController.getSerialService());
                LoginPanel loginPanel = new LoginPanel(loginController, navManager);
                navManager.showPanel("Login", loginPanel);
                loginController.attachToActiveSerial();
            }
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Centro (Botoes Gigantes)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 80));

        scanCattleButton = new JButton("<html><center>IDENTIFICAR / VACINAR<br>(Aproximar Leitor)</center></html>");
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
            CattleListPanel listPanel = new CattleListPanel(cattleController.getApiService(), cattleController,
                    loggedUser, navManager, this);
            navManager.showPanel("List", listPanel);
        });

        JButton manualRegisterButton = new JButton(
                "<html><center>CADASTRAR ANIMAL<br>(Sem Uso do Crachá)</center></html>");
        manualRegisterButton.setPreferredSize(new Dimension(200, 100));
        manualRegisterButton.setFont(new Font("Arial", Font.BOLD, 14));
        manualRegisterButton.addActionListener(e -> {
            statusLabel.setText("Preparando formulário manual...");

            // Gera uma TAG automática garantindo até 16 bytes: "C" + Epoch timestamp
            String generatedTag = "C" + System.currentTimeMillis();
            if (generatedTag.length() > 16) {
                generatedTag = generatedTag.substring(0, 16);
            }

            Cattle newCattle = new Cattle();
            newCattle.setRfidTag(generatedTag);

            System.out.println("-> Abrindo Formulário Manual do Gado para: " + generatedTag);
            activeCattleForm = new CattleFormPanel(newCattle, true, true, cattleController, loggedUser, navManager,
                    this);
            navManager.showPanel("ManualRegister", activeCattleForm);
        });

        centerPanel.add(scanCattleButton);
        centerPanel.add(manualRegisterButton);
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
            statusLabel.setText("Animal Encontrado (" + cattle.getRfidTag() + ")");
            System.out.println("-> Abrindo Formulário de Vacina para: " + cattle.getRfidTag());
            VaccineFormPanel form = new VaccineFormPanel(cattle, cattleController, loggedUser, navManager, this);
            navManager.showPanel("Vaccine", form);
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
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Tag gravada com sucesso!");
            if (activeCattleForm != null && activeCattleForm.isVisible()) {
                activeCattleForm.onTagWriteSuccess();
            }
        });
    }

    @Override
    public void onRfidWriteError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Erro de Escrita: " + message);
            JOptionPane.showMessageDialog(this,
                    message + "\nPosicione a TAG sob o leitor corretamente e tente novamente.", "Erro ao Gravar RFID",
                    JOptionPane.ERROR_MESSAGE);
            if (activeCattleForm != null) {
                activeCattleForm.resetSubmitButton();
            }
        });
    }

    @Override
    public void onApiSaveSuccess() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Dados salvos no mock db com sucesso.");
            JOptionPane.showMessageDialog(this, "Registro concluído e salvo no banco de dados!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            if (activeCattleForm != null) {
                activeCattleForm = null;
                // Transition will be handled by the form closing itself or auto-saving
                // mechanism.
            }
        });
    }

    @Override
    public void onApiSaveError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Falha no Banco: " + message);
            JOptionPane.showMessageDialog(this, message, "Erro Base de Dados", JOptionPane.ERROR_MESSAGE);
            if (activeCattleForm != null) {
                activeCattleForm.resetSubmitButton();
            }
        });
    }

    public void setActiveCattleForm(CattleFormPanel activeCattleForm) {
        this.activeCattleForm = activeCattleForm;
    }
}
