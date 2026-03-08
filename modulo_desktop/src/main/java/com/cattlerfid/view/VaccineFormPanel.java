package com.cattlerfid.view;

import com.cattlerfid.controller.CattleController;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.model.Vaccine;
import com.cattlerfid.view.utils.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VaccineFormPanel extends JPanel {

    private final Cattle cattle;
    private final CattleController controller;
    private final User loggedUser;
    private final NavigationManager navManager;
    private final MainPanel parentMainPanel;

    private JTextField dateField;
    private JTextField vaccineTypeField;
    private JTextField weightField;
    private JTextField nameField;
    private JButton submitButton;

    public VaccineFormPanel(Cattle cattle, CattleController controller, User loggedUser, NavigationManager navManager,
            MainPanel parentMainPanel) {
        this.cattle = cattle;
        this.controller = controller;
        this.loggedUser = loggedUser;
        this.navManager = navManager;
        this.parentMainPanel = parentMainPanel;

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyles.BACKGROUND);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyles.BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = UIStyles.createTitleLabel("Registro de Vacinação - " + cattle.getRfidTag());
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = UIStyles.createBackButton("< Voltar");
        backButton.setPreferredSize(new Dimension(100, 30));
        backButton.addActionListener(e -> {
            navManager.showPanel("Main", parentMainPanel);
        });
        headerPanel.add(backButton, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form Container with Card Styling
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(UIStyles.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // RFID Tag
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel tagLabel = new JLabel("Tag RFID:");
        tagLabel.setFont(UIStyles.LABEL_FONT);
        cardPanel.add(tagLabel, gbc);

        gbc.gridx = 1;
        JTextField tagField = new JTextField(cattle.getRfidTag());
        tagField.setFont(UIStyles.BODY_FONT);
        tagField.setEditable(false);
        tagField.setBackground(UIStyles.SECONDARY);
        cardPanel.add(tagField, gbc);

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Nome do Animal:");
        nameLabel.setFont(UIStyles.LABEL_FONT);
        cardPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(cattle.getName() != null ? cattle.getName() : "");
        nameField.setFont(UIStyles.BODY_FONT);
        nameField.setEditable(false);
        nameField.setBackground(UIStyles.SECONDARY);
        cardPanel.add(nameField, gbc);

        // Responsável
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel respLabel = new JLabel("Veterinário:");
        respLabel.setFont(UIStyles.LABEL_FONT);
        cardPanel.add(respLabel, gbc);

        gbc.gridx = 1;
        JTextField userField = new JTextField(loggedUser.getName());
        userField.setFont(UIStyles.BODY_FONT);
        userField.setEditable(false);
        userField.setBackground(UIStyles.SECONDARY);
        cardPanel.add(userField, gbc);

        // Vacina
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel vLabel = new JLabel("Tipo da Vacina:");
        vLabel.setFont(UIStyles.LABEL_FONT);
        cardPanel.add(vLabel, gbc);

        gbc.gridx = 1;
        vaccineTypeField = new JTextField();
        vaccineTypeField.setFont(UIStyles.BODY_FONT);
        cardPanel.add(vaccineTypeField, gbc);

        // Peso
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel wLabel = new JLabel("Peso Atual (kg):");
        wLabel.setFont(UIStyles.LABEL_FONT);
        cardPanel.add(wLabel, gbc);

        gbc.gridx = 1;
        weightField = new JTextField(cattle.getWeight() > 0 ? String.valueOf(cattle.getWeight()) : "");
        weightField.setFont(UIStyles.BODY_FONT);
        cardPanel.add(weightField, gbc);

        // Data
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel dLabel = new JLabel("Data Aplicação:");
        dLabel.setFont(UIStyles.LABEL_FONT);
        cardPanel.add(dLabel, gbc);

        gbc.gridx = 1;
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateField.setFont(UIStyles.BODY_FONT);
        cardPanel.add(dateField, gbc);

        // Wrapper for centering
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(UIStyles.BACKGROUND);
        wrapperPanel.add(cardPanel);

        add(wrapperPanel, BorderLayout.CENTER);

        // Botoes Pannel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setBackground(UIStyles.BACKGROUND);

        submitButton = UIStyles.createSuccessButton("Registrar Vacina");
        submitButton.setPreferredSize(new Dimension(250, 45));
        submitButton.setBackground(UIStyles.PRIMARY);
        submitButton.addActionListener(e -> saveAction());
        buttonPanel.add(submitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveAction() {
        try {
            double weight = 0.0;
            if (!weightField.getText().trim().isEmpty()) {
                weight = Double.parseDouble(weightField.getText().replace(",", "."));
            }

            LocalDate date;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            date = LocalDate.parse(dateField.getText().trim(), formatter);

            String vType = vaccineTypeField.getText().trim();
            if (vType.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O tipo/nome da vacina é obrigatório.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vaccine v = new Vaccine();
            v.setRfidTag(cattle.getRfidTag());
            v.setVaccineType(vType);
            v.setCurrentWeight(weight);

            // Convert DD/MM/YYYY (UI) to YYYY-MM-DD (API)
            String[] parts = dateField.getText().trim().split("/");
            if (parts.length == 3) {
                v.setVaccinationDate(parts[2] + "-" + parts[1] + "-" + parts[0]);
            }

            controller.saveVaccineData(v, cattle, weight);
            navManager.showPanel("Main", parentMainPanel);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Peso inválido. Use formato número decimal.", "Erro de Digitação",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use formato DD/MM/AAAA.", "Erro de Digitação",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
