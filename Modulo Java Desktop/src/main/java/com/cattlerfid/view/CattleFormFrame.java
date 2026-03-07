package com.cattlerfid.view;

import com.cattlerfid.controller.CattleController;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CattleFormFrame extends JFrame {

    private final Cattle cattle;
    private final boolean isNew;
    private final CattleController controller;
    private final User loggedUser;

    private JTextField nameField;
    private JTextField weightField;
    private JTextField dateField;
    private JButton submitButton;

    public CattleFormFrame(Cattle cattle, boolean isNew, CattleController controller, User loggedUser) {
        this.cattle = cattle;
        this.isNew = isNew;
        this.controller = controller;
        this.loggedUser = loggedUser;

        setupUI();
        populateFields();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupUI() {
        setTitle(isNew ? "Novo Cadastro de Animal" : "Editando Animal");
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(400, 350));

        // Form Pannel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        formPanel.add(new JLabel("RFID Tag (Apenas Leitura):"));
        JTextField tagField = new JTextField(cattle.getRfidTag());
        tagField.setEditable(false);
        tagField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(tagField);

        formPanel.add(new JLabel("Responsável (Apenas Leitura):"));
        JTextField userField = new JTextField(loggedUser.getFullName());
        userField.setEditable(false);
        userField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(userField);

        formPanel.add(new JLabel("Nome / Apelido:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Peso (kg):"));
        weightField = new JTextField();
        formPanel.add(weightField);

        formPanel.add(new JLabel("Última Vacinação (DD/MM/AAAA):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        add(formPanel, BorderLayout.CENTER);

        // Botoes Pannel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton writeChipButton = new JButton("Gravar Nome No Chip");
        writeChipButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha um nome primeiro para gravar no chip.", "Alerta",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Aproxime A MESMA tag na antena agora. O nome será gravado nela.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            controller.requestWriteTag(nameField.getText().trim());
        });
        buttonPanel.add(writeChipButton);

        JButton logButton = new JButton("Logs");
        logButton.addActionListener(e -> {
            SerialLogFrame logFrame = new SerialLogFrame(controller.getSerialService());
            logFrame.setVisible(true);
        });
        buttonPanel.add(logButton);

        submitButton = new JButton("Salvar no Banco");
        submitButton.addActionListener(e -> saveAction());
        buttonPanel.add(submitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        if (!isNew) {
            nameField.setText(cattle.getName() != null ? cattle.getName() : "");
            weightField.setText(cattle.getWeight() > 0 ? String.valueOf(cattle.getWeight()) : "");
            if (cattle.getLastVaccinationDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dateField.setText(cattle.getLastVaccinationDate().format(formatter));
            }
        }
    }

    private void saveAction() {
        try {
            double weight = 0.0;
            if (!weightField.getText().trim().isEmpty()) {
                weight = Double.parseDouble(weightField.getText().replace(",", "."));
            }

            LocalDate date = null;
            if (!dateField.getText().trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                date = LocalDate.parse(dateField.getText().trim(), formatter);
            }

            cattle.setName(nameField.getText().trim());
            cattle.setWeight(weight);
            cattle.setLastVaccinationDate(date);
            cattle.setVaccinatorUser(loggedUser.getUsername());

            controller.saveCattleData(cattle);
            JOptionPane.showMessageDialog(this, "Dados do animal salvos com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Peso inválido. Use formato número decimal.", "Erro de Digitação",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use formato DD/MM/AAAA.", "Erro de Digitação",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
