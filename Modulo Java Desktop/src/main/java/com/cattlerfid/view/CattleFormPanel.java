package com.cattlerfid.view;

import com.cattlerfid.controller.CattleController;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CattleFormPanel extends JPanel {

    private final Cattle cattle;
    private final boolean isNew;
    private final boolean isManual;
    private final CattleController controller;
    private final User loggedUser;

    private final NavigationManager navManager;
    private final MainPanel parentMainPanel;

    private JTextField nameField;
    private JTextField weightField;
    private JTextField dateField;
    private JButton writeTagButton;
    private JButton saveDbButton;

    public CattleFormPanel(Cattle cattle, boolean isNew, boolean isManual, CattleController controller,
            User loggedUser, NavigationManager navManager, MainPanel parentMainPanel) {
        this.cattle = cattle;
        this.isNew = isNew;
        this.isManual = isManual;
        this.controller = controller;
        this.loggedUser = loggedUser;
        this.navManager = navManager;
        this.parentMainPanel = parentMainPanel;

        setupUI();
        populateFields();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(isNew ? "Novo Cadastro de Animal" : "Editando Animal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("< Voltar");
        backButton.addActionListener(e -> {
            // Aborta edição e volta
            if (parentMainPanel != null) {
                parentMainPanel.setActiveCattleForm(null); // Clear ref
            }
            navManager.showPanel("Main", parentMainPanel);
        });
        headerPanel.add(backButton, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

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

        formPanel.add(new JLabel("Data de Cadastro (DD/MM/AAAA):"));
        dateField = new JTextField();
        dateField.setEditable(false);
        dateField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(dateField);

        add(formPanel, BorderLayout.CENTER);

        // Botoes Pannel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        writeTagButton = new JButton("1. Gravar Identidade na Tag Física");
        writeTagButton.addActionListener(e -> writeTagAction());
        // Apenas habilita gravação física se for manual
        writeTagButton.setVisible(isManual);
        buttonPanel.add(writeTagButton);

        saveDbButton = new JButton("2. Salvar no Banco de Dados");
        saveDbButton.addActionListener(e -> saveDbAction());
        saveDbButton.setEnabled(!isManual || !isNew); // Habilita direto se for edição ou se não for manual
        buttonPanel.add(saveDbButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (!isNew) {
            nameField.setText(cattle.getName() != null ? cattle.getName() : "");
            weightField.setText(cattle.getWeight() > 0 ? String.valueOf(cattle.getWeight()) : "");
            if (cattle.getRegistrationDate() != null) {
                dateField.setText(cattle.getRegistrationDate().format(formatter));
            } else {
                dateField.setText(LocalDate.now().format(formatter));
            }
        } else {
            dateField.setText(LocalDate.now().format(formatter));
            cattle.setRegistrationDate(LocalDate.now());
        }
    }

    private void writeTagAction() {
        try {
            double weight = 0.0;
            if (!weightField.getText().trim().isEmpty()) {
                weight = Double.parseDouble(weightField.getText().replace(",", "."));
            }

            cattle.setName(nameField.getText().trim());
            cattle.setWeight(weight);

            if (isManual) {
                writeTagButton.setEnabled(false);
                writeTagButton.setText("Gravando na Tag...");
                controller.requestWriteTag(cattle.getRfidTag());
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Peso inválido. Use formato número decimal.", "Erro de Digitação",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDbAction() {
        try {
            double weight = 0.0;
            if (!weightField.getText().trim().isEmpty()) {
                weight = Double.parseDouble(weightField.getText().replace(",", "."));
            }

            cattle.setName(nameField.getText().trim());
            cattle.setWeight(weight);

            controller.saveCattleData(cattle);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Peso inválido. Use formato número decimal.", "Erro de Digitação",
                    JOptionPane.ERROR_MESSAGE);
            saveDbButton.setEnabled(true);
        }
    }

    // Callback para quando a gravação física der erro
    public void resetSubmitButton() {
        writeTagButton.setEnabled(true);
        writeTagButton.setText("Tentar Gravar Tag Novamente");
    }

    // Callback para sucesso físico
    public void onTagWriteSuccess() {
        writeTagButton.setEnabled(false);
        writeTagButton.setText("Tag Gravada!");
        writeTagButton.setBackground(new Color(144, 238, 144)); // Verde claro

        saveDbButton.setEnabled(true);
        saveDbAction(); // Salva automaticamente ao dar sucesso

        // Transição de sucesso
        if (parentMainPanel != null) {
            parentMainPanel.setActiveCattleForm(null);
        }
        navManager.showPanel("Main", parentMainPanel);
    }

    // Getter para os dados montados
    public Cattle getPendingCattle() {
        return cattle;
    }
}
