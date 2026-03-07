package com.cattlerfid.view;

import com.cattlerfid.controller.CattleController;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.model.Vaccine;

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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Registro de Vacinação - " + cattle.getRfidTag(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("< Voltar");
        backButton.addActionListener(e -> {
            navManager.showPanel("Main", parentMainPanel);
        });
        headerPanel.add(backButton, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form Pannel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        formPanel.add(new JLabel("Tag RFID:"));
        JTextField tagField = new JTextField(cattle.getRfidTag());
        tagField.setEditable(false);
        tagField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(tagField);

        formPanel.add(new JLabel("Nome do Animal:"));
        nameField = new JTextField(cattle.getName() != null ? cattle.getName() : "");
        nameField.setEditable(false);
        nameField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Veterinário Responsável:"));
        JTextField userField = new JTextField(loggedUser.getFullName());
        userField.setEditable(false);
        userField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(userField);

        formPanel.add(new JLabel("Tipo / Nome da Vacina:"));
        vaccineTypeField = new JTextField();
        formPanel.add(vaccineTypeField);

        formPanel.add(new JLabel("Peso Atual do Animal (kg):"));
        weightField = new JTextField(cattle.getWeight() > 0 ? String.valueOf(cattle.getWeight()) : "");
        formPanel.add(weightField);

        formPanel.add(new JLabel("Data da Aplicação (DD/MM/AAAA):"));
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        formPanel.add(dateField);

        add(formPanel, BorderLayout.CENTER);

        // Botoes Pannel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        submitButton = new JButton("Registrar Vacina");
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
            v.setVaccinatorUser(loggedUser.getUsername());
            v.setVaccineType(vType);
            v.setCurrentWeight(weight);
            v.setVaccinationDate(date);

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
