package cn.blazeh.achat.client.gui;

import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.client.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AuthFrame extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(64, 128, 255);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);

    private final AuthService authService;
    private final Runnable onLoginSuccess;

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JTextField loginUsername = new JTextField(15);
    private final JPasswordField loginPassword = new JPasswordField(15);
    private final JTextField regUsername = new JTextField(15);
    private final JPasswordField regPassword = new JPasswordField(15);
    private final JPasswordField regConfirmPassword = new JPasswordField(15);

    public AuthFrame(AuthService authService, Runnable onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;

        setTitle("AChat - 登录注册");
        setSize(420, 570);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        applyModernStyling();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 顶部标题面板
        JPanel headerPanel = createHeaderPanel();

        // 主内容面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 自定义标签页
        setupTabbedPane();

        // 登录面板
        JPanel loginPanel = createLoginPanel();

        // 注册面板
        JPanel regPanel = createRegisterPanel();

        tabbedPane.addTab("登录", loginPanel);
        tabbedPane.addTab("注册", regPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // 底部版权信息
        JPanel footerPanel = createFooterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("欢迎使用 AChat");
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("连接你我，沟通无界");
        subtitleLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 230, 255));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private void setupTabbedPane() {
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setBorder(null);

        // 自定义标签页样式
        UIManager.put("TabbedPane.selected", LIGHT_GRAY);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(10, 0, 0, 0));
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // 用户名输入
        JPanel usernamePanel = createInputGroup("👤 用户名", loginUsername);

        // 密码输入
        JPanel passwordPanel = createInputGroup("🔒 密码", loginPassword);

        // 登录按钮
        JButton loginBtn = createStyledButton("登录", PRIMARY_COLOR, this::performLogin);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createVerticalStrut(25));
        loginPanel.add(loginBtn);

        // 回车键登录
        loginUsername.addActionListener(e -> loginPassword.requestFocus());
        loginPassword.addActionListener(this::performLogin);

        return loginPanel;
    }

    private JPanel createRegisterPanel() {
        JPanel regPanel = new JPanel();
        regPanel.setLayout(new BoxLayout(regPanel, BoxLayout.Y_AXIS));
        regPanel.setBackground(Color.WHITE);
        regPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // 用户名输入
        JPanel usernamePanel = createInputGroup("👤 用户名", regUsername);

        // 密码输入
        JPanel passwordPanel = createInputGroup("🔒 密码", regPassword);

        // 确认密码输入
        JPanel confirmPanel = createInputGroup("🔐 确认密码", regConfirmPassword);

        // 注册按钮
        JButton regBtn = createStyledButton("注册", ACCENT_COLOR, this::performRegister);
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        regPanel.add(usernamePanel);
        regPanel.add(Box.createVerticalStrut(15));
        regPanel.add(passwordPanel);
        regPanel.add(Box.createVerticalStrut(15));
        regPanel.add(confirmPanel);
        regPanel.add(Box.createVerticalStrut(25));
        regPanel.add(regBtn);

        // 回车键注册
        regUsername.addActionListener(e -> regPassword.requestFocus());
        regPassword.addActionListener(e -> regConfirmPassword.requestFocus());
        regConfirmPassword.addActionListener(this::performRegister);

        return regPanel;
    }

    private JPanel createInputGroup(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);

        // 设置输入框样式
        textField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        textField.setPreferredSize(new Dimension(0, 40));
        textField.setBackground(Color.WHITE);

        // 添加焦点效果
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(9, 14, 9, 14)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(bgColor, 1, true));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 42));
        button.setMaximumSize(new Dimension(120, 42));
        button.addActionListener(listener);

        // 添加悬停效果
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

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(LIGHT_GRAY);
        footerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel copyrightLabel = new JLabel("© 2025 AChat - 安全可靠的即时通讯软件");
        copyrightLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(new Color(108, 117, 125));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        footerPanel.add(copyrightLabel, BorderLayout.CENTER);
        return footerPanel;
    }

    private void applyModernStyling() {
        // 设置整体字体
        Font defaultFont = new Font("Microsoft YaHei UI", Font.PLAIN, 12);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
    }

    private boolean beforeAuth() {
        System.out.println(SessionManager.INSTANCE.getSession().getAuthState());
        switch(SessionManager.INSTANCE.getSession().getAuthState()) {
            case PREPARING -> showErrorMessage("尚未连接服务器，请稍后再试");
            case PENDING -> showErrorMessage("正在等待服务器验证响应，请勿重复发送验证请求");
            case DONE -> showErrorMessage("已登录成功，请勿重复登录");
        }
        return SessionManager.INSTANCE.getSession().getAuthState().equals(Session.AuthState.READY);
    }

    private void performLogin(ActionEvent e) {
        if(!beforeAuth())
            return;

        String username = loginUsername.getText().trim();
        String password = new String(loginPassword.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            showErrorMessage("用户名或密码不能为空");
            return;
        }

        authService.sendAuthRequest(username, password, false);
    }

    private void performRegister(ActionEvent e) {
        if(!beforeAuth())
            return;

        String username = regUsername.getText().trim();
        String password = new String(regPassword.getPassword());
        String confirm = new String(regConfirmPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("用户名或密码不能为空");
            return;
        }

        if (password.length() < 6) {
            showErrorMessage("密码长度不能少于6位");
            return;
        }

        if (!password.equals(confirm)) {
            showErrorMessage("两次输入的密码不一致");
            return;
        }

        authService.sendAuthRequest(username, password, true);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    public void authSuccess(String msg) {
        showSuccessMessage(msg);
        dispose();
        onLoginSuccess.run();
    }

    public void authFailed(String msg) {
        showErrorMessage(msg);
    }
}