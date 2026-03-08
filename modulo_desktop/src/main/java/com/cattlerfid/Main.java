package com.cattlerfid;

import com.cattlerfid.controller.ConnectionController;
import com.cattlerfid.service.AuthenticationService;
import com.cattlerfid.service.SerialService;
import com.cattlerfid.view.ApplicationFrame;
import com.cattlerfid.view.ConnectionPanel;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Look & Feel moderno (FlatLaf)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {

            // 1. Instanciar Serviços de Configuração e Infraestrutura
            com.cattlerfid.config.ApiConfig apiConfig = new com.cattlerfid.config.ApiConfig();
            AuthenticationService authService = new AuthenticationService(apiConfig);
            SerialService serialService = new SerialService();

            // 2. Instanciar Controladores Raiz
            ConnectionController connectionController = new ConnectionController(serialService);

            // 3. Inicializar a View Primária (ApplicationFrame e ConnectionPanel)
            ApplicationFrame appFrame = new ApplicationFrame();
            ConnectionPanel connectionPanel = new ConnectionPanel(connectionController, authService, apiConfig,
                    appFrame);

            // 4. Mostrar Aplicação Java
            appFrame.setVisible(true);
            appFrame.showPanel("Connection", connectionPanel);

            System.out.println("Sistema Modulo Desktop iniciado (Single-Window Mode).");
        });
    }
}
