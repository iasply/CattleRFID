package com.cattlerfid.view;

import com.cattlerfid.model.Cattle;
import com.cattlerfid.service.CattleApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CattleListFrame extends JFrame {

    private final CattleApiService apiService;
    private final com.cattlerfid.controller.CattleController controller;

    public CattleListFrame(CattleApiService apiService, com.cattlerfid.controller.CattleController controller) {
        this.apiService = apiService;
        this.controller = controller;

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
        String[] columnNames = { "Tag RFID", "Nome/Apelido", "Peso (kg)", "Última Vacina", "VET Resp." };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Apenas leitura
            }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Puxa os dados da API Mock
        List<Cattle> allCattle = apiService.getAllCattle();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Cattle c : allCattle) {
            String dateStr = c.getLastVaccinationDate() != null ? c.getLastVaccinationDate().format(formatter)
                    : "Nenhum";
            Object[] row = {
                    c.getRfidTag(),
                    c.getName(),
                    c.getWeight(),
                    dateStr,
                    c.getVaccinatorUser()
            };
            tableModel.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Botoes extra
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

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
}
