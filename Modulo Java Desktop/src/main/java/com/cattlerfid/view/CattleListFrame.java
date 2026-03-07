package com.cattlerfid.view;

import com.cattlerfid.model.Cattle;
import com.cattlerfid.service.CattleApiService;

import com.cattlerfid.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CattleListFrame extends JFrame {

    private final CattleApiService apiService;
    private final com.cattlerfid.controller.CattleController controller;
    private final User loggedUser;
    private final MainFrame mainFrame;

    private DefaultTableModel tableModel;
    private JTable table;

    public CattleListFrame(CattleApiService apiService, com.cattlerfid.controller.CattleController controller,
            User loggedUser, MainFrame mainFrame) {
        this.apiService = apiService;
        this.controller = controller;
        this.loggedUser = loggedUser;
        this.mainFrame = mainFrame;

        setupUI();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupUI() {
        setTitle("Controle de Gado Cadastrado");
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(650, 400));

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

        JButton closeBtn = new JButton("Fechar");
        closeBtn.addActionListener(e -> dispose());
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
            CattleFormFrame form = new CattleFormFrame(target, false, true, controller, loggedUser);

            // Informa ao MainFrame (ouvinte master da porta serial) que esta é a tela
            // aguardando a gravação
            if (mainFrame != null) {
                mainFrame.setActiveCattleForm(form);
            }

            form.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // Limpa a referencia segura na main se ela tiver falhado/encerrado
                    if (mainFrame != null) {
                        mainFrame.setActiveCattleForm(null);
                    }
                    refreshTable(); // Atualiza a tabela quando o usuario fechar a tela de edição
                }
            });
            form.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Erro: Animal não encontrado na base de dados.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
