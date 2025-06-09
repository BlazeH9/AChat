package cn.blazeh.achat.client.gui;

import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.client.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * 认证窗口界面，提供用户登录和注册功能，处理认证流程的UI交互
 */
public class AuthFrame extends BaseFrame {

    private static final Logger LOGGER = LogManager.getLogger(AuthFrame.class);

    private final AuthService authService;
    private final Runnable onLoginSuccess;

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JTextField loginUsername = new JTextField(15);
    private final JPasswordField loginPassword = new JPasswordField(15);
    private final JTextField regUsername = new JTextField(15);
    private final JPasswordField regPassword = new JPasswordField(15);
    private final JPasswordField regConfirmPassword = new JPasswordField(15);

    /**
     * 创建认证窗口
     * @param authService 认证服务实例
     * @param onLoginSuccess 登录成功回调函数
     * @param onClose 窗口关闭回调函数
     */
    public AuthFrame(AuthService authService, Runnable onLoginSuccess, Runnable onClose) {
        super(onClose);
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;

        setTitle("AChat - 登录注册");
        setSize(420, 570);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        LOGGER.info("认证页面初始化完成");
        applyModernStyling();
    }

    /**
     * 初始化用户界面，构建认证窗口布局
     */
    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = createHeaderPanel();
        LOGGER.info("顶部标题面板创建完成");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        LOGGER.info("主内容面板创建完成");

        setupTabbedPane();
        LOGGER.info("自定义标签页创建完成");

        JPanel loginPanel = createLoginPanel();
        LOGGER.info("登录面板创建完成");

        JPanel regPanel = createRegisterPanel();
        LOGGER.info("注册面板创建完成");

        tabbedPane.addTab("登录", loginPanel);
        tabbedPane.addTab("注册", regPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        LOGGER.info("页脚信息创建完成");

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建顶部标题面板
     * @return 包含标题的头部面板
     */
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

    /**
     * 配置标签页组件，设置标签页样式
     */
    private void setupTabbedPane() {
        tabbedPane.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setBorder(null);

        UIManager.put("TabbedPane.selected", LIGHT_GRAY);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(10, 0, 0, 0));
    }

    /**
     * 创建登录面板
     * @return 包含登录表单的面板
     */
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel usernamePanel = createInputGroup("👤 用户名", loginUsername);
        JPanel passwordPanel = createInputGroup("🔒 密码", loginPassword);

        Dimension size = new Dimension(120, 42);
        JButton loginBtn = createStyledButton("登录", 14, PRIMARY_COLOR, size, this::performLogin);
        loginBtn.setMaximumSize(size);
        loginBtn.setMinimumSize(size);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createVerticalStrut(25));
        loginPanel.add(loginBtn);

        loginUsername.addActionListener(e -> loginPassword.requestFocus());
        loginPassword.addActionListener(this::performLogin);

        return loginPanel;
    }

    /**
     * 创建注册面板
     * @return 包含注册表单的面板
     */
    private JPanel createRegisterPanel() {
        JPanel regPanel = new JPanel();
        regPanel.setLayout(new BoxLayout(regPanel, BoxLayout.Y_AXIS));
        regPanel.setBackground(Color.WHITE);
        regPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel usernamePanel = createInputGroup("👤 用户名", regUsername);
        JPanel passwordPanel = createInputGroup("🔒 密码", regPassword);
        JPanel confirmPanel = createInputGroup("🔐 确认密码", regConfirmPassword);

        Dimension size = new Dimension(120, 42);
        JButton regBtn = createStyledButton("注册", 14, PRIMARY_COLOR, size, this::performLogin);
        regBtn.setMaximumSize(size);
        regBtn.setMinimumSize(size);
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        regPanel.add(usernamePanel);
        regPanel.add(Box.createVerticalStrut(15));
        regPanel.add(passwordPanel);
        regPanel.add(Box.createVerticalStrut(15));
        regPanel.add(confirmPanel);
        regPanel.add(Box.createVerticalStrut(25));
        regPanel.add(regBtn);

        regUsername.addActionListener(e -> regPassword.requestFocus());
        regPassword.addActionListener(e -> regConfirmPassword.requestFocus());
        regConfirmPassword.addActionListener(this::performRegister);

        return regPanel;
    }

    /**
     * 创建输入字段组
     * @param labelText 字段标签文本
     * @param textField 输入框组件
     * @return 包含标签和输入框的面板
     */
    private JPanel createInputGroup(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);

        textField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        textField.setPreferredSize(new Dimension(0, 40));
        textField.setBackground(Color.WHITE);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(9, 14, 9, 14)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
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

    /**
     * 创建页脚面板
     * @return 包含版权信息的页脚面板
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(LIGHT_GRAY);
        footerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel copyrightLabel = new JLabel("2025 AChat - 一款简单的即时通讯软件");
        copyrightLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(new Color(108, 117, 125));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        footerPanel.add(copyrightLabel, BorderLayout.CENTER);
        return footerPanel;
    }

    /**
     * 认证前置检查，验证当前认证状态是否允许操作
     * @return 是否允许进行认证操作
     */
    private boolean beforeAuth() {
        System.out.println(SessionManager.INSTANCE.getSession().getAuthState());
        switch(SessionManager.INSTANCE.getSession().getAuthState()) {
            case PREPARING -> showWarnMessage("尚未连接服务器，请稍后再试");
            case PENDING -> showWarnMessage("正在等待服务器验证响应，请勿重复发送验证请求");
            case DONE -> showWarnMessage("已登录成功，请勿重复登录");
        }
        return SessionManager.INSTANCE.getSession().getAuthState().equals(Session.AuthState.READY);
    }

    /**
     * 执行登录操作，处理登录表单提交
     * @param e 触发事件
     */
    private void performLogin(ActionEvent e) {
        if(!beforeAuth())
            return;

        String username = loginUsername.getText().trim();
        String password = new String(loginPassword.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            showWarnMessage("用户名或密码不能为空");
            return;
        }

        authService.sendAuthRequest(username, password, false);
    }

    /**
     * 执行注册操作，处理注册表单提交
     * @param e 触发事件
     */
    private void performRegister(ActionEvent e) {
        if(!beforeAuth())
            return;

        String username = regUsername.getText().trim();
        String password = new String(regPassword.getPassword());
        String confirm = new String(regConfirmPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showWarnMessage("用户名或密码不能为空");
            return;
        }

        if (password.length() < 6) {
            showWarnMessage("密码长度不能少于6位");
            return;
        }

        if (!password.equals(confirm)) {
            showWarnMessage("两次输入的密码不一致");
            return;
        }

        authService.sendAuthRequest(username, password, true);
    }

    /**
     * 处理认证成功事件，关闭窗口并执行回调
     * @param msg 成功提示消息
     */
    public void authSuccess(String msg) {
        showInfoMessage(msg, "成功");
        dispose();
        onLoginSuccess.run();
    }

    /**
     * 处理认证失败事件，显示错误提示
     * @param msg 失败提示消息
     */
    public void authFailed(String msg) {
        showWarnMessage(msg);
    }
}