package com.cattlerfid.view;

import com.cattlerfid.model.Cattle;
import com.cattlerfid.service.CattleApiService;

import com.cattlerfid.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CattleListPanel extends JPanel {

    private final CattleApiService apiService;
    private final com.cattlerfid.controller.CattleController controller;
    private final User loggedUser;
    private final NavigationManager navManager;
    private final MainPanel parentMainPanel;

    private DefaultTableModel tableModel;
    private JTable table;

    public CattleListPanel(CattleApiService apiService, com.cattlerfid.controller.CattleController controller,
            User loggedUser, NavigationManager navManager, MainPanel parentMainPanel) {
        this.apiService = apiService;
        this.controller = controller;
        this.loggedUser = loggedUser;
        this.navManager = navManager;
        this.parentMainPanel = parentMainPanel;

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Abaixo estao listados os animais mockados pela API remota:"));
        add(topPanel, BorderLayout.NORTH);

        // Configuração da Tabela
        String[] columnNames = { "Tag RFID", "Nome/Apelido", "Peso (kg)", "Data Registro", "Vacinas Aplicadas" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Apenas leitura
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Botoes extra
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton editButton = new JButton("Editar Animal Selecionado");
        editButton.addActionListener(e -> openEditDialog());
        bottomPanel.add(editButton);

        JButton logButton = new JButton("Logs Serial");
        logButton.addActionListener(e -> {
            SerialLogFrame logFrame = new SerialLogFrame(controller.getSerialService());
            logFrame.setVisible(true);
        });
        bottomPanel.add(logButton);

        JButton closeBtn = new JButton("< Voltar para Menu");
        closeBtn.addActionListener(e -> {
            navManager.showPanel("Main", parentMainPanel);
        });
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Limpa tabela
        List<Cattle> allCattle = apiService.getAllCattle();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Cattle c : allCattle) {
            String dateStr = c.getRegistrationDate() != null ? c.getRegistrationDate().format(formatter) : "N/A";
            int countVaccines = apiService.getVaccinesByCattle(c.getRfidTag()).size();

            Object[] row = {
                    c.getRfidTag(),
                    c.getName(),
                    c.getWeight(),
                    dateStr,
                    countVaccines
            };
            tableModel.addRow(row);
        }
    }

    private void openEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um animal na lista para editar.",
                    "Nenhum animal selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tagId = (String) tableModel.getValueAt(selectedRow, 0);
        Optional<Cattle> targetOpt = apiService.getCattleByTag(tagId);

        if (targetOpt.isPresent()) {
            Cattle target = targetOpt.get();
            // Abre o formulario como isNew=false, isManual=true (permitir gravação RFID e
            // salvar no DB)
            CattleFormPanel form = new CattleFormPanel(target, false, true, controller, loggedUser, navManager,
                    parentMainPanel);

            // Informa ao MainPanel (ouvinte master da porta serial) que esta é a tela
            // aguardando a gravação
            if (parentMainPanel != null) {
                parentMainPanel.setActiveCattleForm(form);
            }

            // Transitamos para o form de edição. Quando voltar ele vai passar por um novo
            // state,
            // mas nós podemos forçar o refresh ao chamar a lista novamente caso seja
            // necessário.
            navManager.showPanel("EditCattle", form);
        } else {
            JOptionPane.showMessageDialog(this, "Erro: Animal não encontrado na base de dados.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
