package cn.blazeh.achat.client.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class BaseFrame extends JFrame {

    protected static final Color PRIMARY_COLOR = new Color(64, 128, 255);
    protected static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    protected static final Color ACCENT_COLOR = new Color(40, 167, 69);
    protected static final Color TEXT_COLOR = new Color(51, 51, 51);
    protected static final Color DANGER_COLOR = new Color(220, 53, 69);
    protected static final Color HOVER_COLOR = new Color(230, 240, 255);
    protected static final Color LIGHT_GRAY = new Color(248, 249, 250);

    public BaseFrame(Runnable onClose) {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose.run();
            }
        });
    }

    protected JButton createStyledButton(String text, Color bgColor, Dimension size, ActionListener listener) {
        return createStyledButton(text, 11, bgColor, size, listener);
    }

    protected JButton createStyledButton(String text, int textSize, Color bgColor, Dimension buttonSize, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei UI", Font.BOLD, textSize));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(bgColor, 1, true));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(buttonSize);
        button.addActionListener(listener);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
                button.setBorder(new LineBorder(bgColor.darker(), 1, true));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(new LineBorder(bgColor, 1, true));
            }
        });
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        return button;
    }

    protected void applyModernStyling() {
        Font defaultFont = new Font("Microsoft YaHei UI", Font.PLAIN, 12);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
    }

    protected void showWarnMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.WARNING_MESSAGE);
    }

    protected void showInfoMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

}
