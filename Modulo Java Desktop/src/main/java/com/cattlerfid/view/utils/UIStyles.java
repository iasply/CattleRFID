package com.cattlerfid.view.utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UIStyles {

    // Color Palette
    public static final Color PRIMARY = new Color(43, 95, 159); // Deep Blue
    public static final Color SECONDARY = new Color(220, 224, 229);
    public static final Color SUCCESS = new Color(46, 125, 50); // Agriculture Green
    public static final Color DANGER = new Color(211, 47, 47); // Red
    public static final Color BACKGROUND = new Color(245, 247, 250); // Off-white
    public static final Color TEXT_DARK = new Color(33, 33, 33);
    public static final Color TEXT_LIGHT = Color.WHITE;

    // Typography
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Creates a styled title label.
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(HEADER_FONT);
        label.setForeground(PRIMARY);
        return label;
    }

    /**
     * Creates a standardized Primary Button (Blue/Green depending on context).
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(SUBHEADER_FONT);
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
        return btn;
    }

    /**
     * Creates a standardized Success Button (e.g., Save/Submit).
     */
    public static JButton createSuccessButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(SUCCESS);
        return btn;
    }

    /**
     * Creates a back/cancel button.
     */
    public static JButton createBackButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BODY_FONT);
        btn.setForeground(DANGER);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Applies a clean shadow/card border to a panel.
     */
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}
