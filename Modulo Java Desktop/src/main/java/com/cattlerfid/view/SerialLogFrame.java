package com.cattlerfid.view;

import com.cattlerfid.service.SerialService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SerialLogFrame extends JFrame {

    private final SerialService serialService;
    private JTextArea logArea;

    public SerialLogFrame(SerialService serialService) {
        this.serialService = serialService;

        setupUI();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        loadHistoryAndSubscribe();
    }

    private void setupUI() {
        setTitle("Log de Comunicação Serial (Arduino <-> Java)");
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(500, 350));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(new Color(0, 255, 0)); // Estilo terminal

        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearButton = new JButton("Limpar Tela");
        clearButton.addActionListener(e -> logArea.setText(""));

        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> dispose());

        bottomPanel.add(clearButton);
        bottomPanel.add(closeButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadHistoryAndSubscribe() {
        // Puxa o que ja passou
        List<String> history = serialService.getLogHistory();
        for (String line : history) {
            logArea.append(line + "\n");
        }

        // Se inscreve para receber novos logs em tempo real
        serialService.setOnLogAppended(newLine -> {
            SwingUtilities.invokeLater(() -> {
                logArea.append(newLine + "\n");
                // Faz auto-scroll pra ultima linha
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        });
    }

    @Override
    public void dispose() {
        // Remove listener de tela limpa ao fechar para evitar leaks de memoria,
        // caso o servico Serial sobreviva mais tempo do que a janela.
        serialService.setOnLogAppended(null);
        super.dispose();
    }
}
