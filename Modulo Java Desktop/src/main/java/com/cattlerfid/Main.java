package com.cattlerfid;

import com.cattlerfid.controller.ConnectionController;
import com.cattlerfid.service.AuthenticationService;
import com.cattlerfid.service.SerialService;
import com.cattlerfid.view.ConnectionFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Look & Feel do Sistema Operacional nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {

            // 1. Instanciar Serviços Globais de Infraestrutura
            AuthenticationService authService = new AuthenticationService();
            SerialService serialService = new SerialService();

            // 2. Instanciar Controladores Raiz
            ConnectionController connectionController = new ConnectionController(serialService);

            // 3. Inicializar a View Primária (Injetando Controller)
            ConnectionFrame connectionFrame = new ConnectionFrame(connectionController, authService);

            // 4. Mostrar Aplicação Java
            connectionFrame.setVisible(true);

            System.out.println("Sistema Modulo Desktop iniciado.");
        });
    }
}
