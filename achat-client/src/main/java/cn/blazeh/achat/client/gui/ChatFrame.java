package cn.blazeh.achat.client.gui;

import cn.blazeh.achat.client.manager.MessageManager;
import cn.blazeh.achat.client.service.ChatService;
import cn.blazeh.achat.client.service.MessageService;
import cn.blazeh.achat.common.model.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatFrame extends JFrame {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Color PRIMARY_COLOR = new Color(64, 128, 255);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(40, 167, 69);
    private static final Color HOVER_COLOR = new Color(230, 240, 255);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);

    private final String userId;
    private final DefaultListModel<String> contactListModel = new DefaultListModel<>();
    private final JList<String> contactList = new JList<>(contactListModel);
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField();
    private final JLabel statusLabel = new JLabel("请选择联系人开始聊天");

    private String currentContact = null;
    private int currentOffset = 0;
    private static final int MESSAGE_LIMIT = 20;
    private final AtomicBoolean isLoading = new AtomicBoolean(false);
    private JScrollPane chatScroll;

    // 添加消息ID追踪，确保不重复显示消息
    private long lastDisplayedMessageId = 0;

    public ChatFrame(String userId) {
        this.userId = userId;

        setTitle("AChat - 欢迎 " + userId);
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        initContacts();
        initUI();
        applyModernStyling();
    }

    private void initContacts() {
        MessageManager.INSTANCE.getContacts().forEach(contact -> {
            if (!contact.equals(userId)) {
                contactListModel.addElement(contact);
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // 左侧面板
        JPanel leftPanel = createLeftPanel();

        // 右侧聊天面板
        JPanel rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        if (!contactListModel.isEmpty()) {
            contactList.setSelectedIndex(0);
        }
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(SECONDARY_COLOR);
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));

        // 顶部：用户信息和添加按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(SECONDARY_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel userLabel = new JLabel("当前用户: " + userId);
        userLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        userLabel.setForeground(TEXT_COLOR);

        JButton addContactBtn = createStyledButton("+ 添加联系人", ACCENT_COLOR);
        addContactBtn.setPreferredSize(new Dimension(120, 30));
        addContactBtn.addActionListener(this::showAddContactDialog);

        topPanel.add(userLabel, BorderLayout.CENTER);
        topPanel.add(addContactBtn, BorderLayout.EAST);

        // 联系人列表
        setupContactList();
        JScrollPane contactScroll = new JScrollPane(contactList);
        contactScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "联系人列表",
                0, 0,
                new Font("Microsoft YaHei UI", Font.BOLD, 12),
                TEXT_COLOR
        ));
        contactScroll.setBackground(Color.WHITE);
        contactScroll.getViewport().setBackground(Color.WHITE);

        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(contactScroll, BorderLayout.CENTER);

        return leftPanel;
    }

    private void setupContactList() {
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        contactList.setFixedCellHeight(40);
        contactList.setBackground(Color.WHITE);
        contactList.setSelectionBackground(HOVER_COLOR);
        contactList.setSelectionForeground(TEXT_COLOR);
        contactList.setBorder(new EmptyBorder(5, 10, 5, 10));

        // 自定义渲染器
        contactList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                setText("👤 " + value.toString());
                setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(8, 15, 8, 15));

                if (isSelected) {
                    setBackground(HOVER_COLOR);
                    setForeground(TEXT_COLOR);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(TEXT_COLOR);
                }

                return this;
            }
        });

        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = contactList.getSelectedValue();
                if (selected != null) {
                    showChat(selected);
                    statusLabel.setText("正在与 " + selected + " 聊天");
                }
            }
        });

        // 添加鼠标悬停效果
        contactList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = contactList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    contactList.repaint(contactList.getCellBounds(index, index));
                }
            }
        });
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(10, 5, 10, 10));

        // 顶部状态栏
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        statusLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_COLOR);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // 聊天区域
        setupChatArea();
        chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "聊天记录",
                0, 0,
                new Font("Microsoft YaHei UI", Font.BOLD, 12),
                TEXT_COLOR
        ));
        chatScroll.setBackground(Color.WHITE);
        chatScroll.getViewport().setBackground(Color.WHITE);

        // 添加滚动监听器
        chatScroll.getVerticalScrollBar().addAdjustmentListener(e -> SwingUtilities.invokeLater(() -> {
            JScrollBar scrollBar = (JScrollBar) e.getSource();
            if (scrollBar.getValue() == 0 && !e.getValueIsAdjusting() && !isLoading.get()) {
                loadMoreMessages();
            }
        }));

        // 输入区域
        JPanel inputPanel = createInputPanel();

        rightPanel.add(statusPanel, BorderLayout.NORTH);
        rightPanel.add(chatScroll, BorderLayout.CENTER);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void setupChatArea() {
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(249, 249, 249));
        chatArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatArea.setForeground(TEXT_COLOR);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // 输入框
        inputField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        inputField.setPreferredSize(new Dimension(0, 35));
        inputField.addActionListener(this::sendMessage);

        // 发送按钮
        JButton sendButton = createStyledButton("发送消息", PRIMARY_COLOR);
        sendButton.setPreferredSize(new Dimension(100, 35));
        sendButton.addActionListener(this::sendMessage);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        return inputPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(bgColor, 1, true));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        return button;
    }

    private void applyModernStyling() {
        // 设置整体字体
        Font defaultFont = new Font("Microsoft YaHei UI", Font.PLAIN, 12);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
    }

    private void showAddContactDialog(ActionEvent e) {
        JDialog dialog = new JDialog(this, "添加联系人", true);
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 标题
        JLabel titleLabel = new JLabel("请输入联系人姓名:");
        titleLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(titleLabel, gbc);

        // 输入框
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        nameField.setPreferredSize(new Dimension(0, 35));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(nameField, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelBtn = createStyledButton("取消", new Color(108, 117, 125));
        cancelBtn.setPreferredSize(new Dimension(80, 32));
        cancelBtn.addActionListener(evt -> dialog.dispose());

        JButton confirmBtn = createStyledButton("确定", ACCENT_COLOR);
        confirmBtn.setPreferredSize(new Dimension(80, 32));
        confirmBtn.addActionListener(evt -> {
            String contactName = nameField.getText().trim();
            if (contactName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "联系人姓名不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (contactName.equals(userId)) {
                JOptionPane.showMessageDialog(dialog, "不能添加自己为联系人！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (int i = 0; i < contactListModel.getSize(); i++) {
                if (contactListModel.getElementAt(i).equals(contactName)) {
                    JOptionPane.showMessageDialog(dialog, "联系人已存在！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            contactListModel.addElement(contactName);

            JOptionPane.showMessageDialog(dialog, "联系人添加成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(confirmBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        nameField.addActionListener(evt -> confirmBtn.doClick());

        dialog.setVisible(true);
        nameField.requestFocus();
    }

    private String formatMessage(Message message) {
        boolean isMyMessage = message.getSender().equals(userId);
        String timeStr = DATE_FORMAT.format(new Date(message.getTimestamp()));
        String senderName = isMyMessage ? "我" : message.getSender();

        return String.format("[%s] %s:\n%s\n", timeStr, senderName, message.getContent());
    }

    private void showChat(String contact) {
        isLoading.set(true);
        try {
            currentContact = contact;
            currentOffset = 0;
            lastDisplayedMessageId = 0;

            chatArea.setText("");

            List<Message> messages = MessageManager.INSTANCE.getConversationMessages(
                    userId, contact, MESSAGE_LIMIT, currentOffset);
            if (messages.isEmpty())
                return;
            messages.sort(Comparator.comparing(Message::getTimestamp)
                    .thenComparing(Message::getMessageId));

            currentOffset = messages.size();
            lastDisplayedMessageId = messages.get(messages.size() - 1).getMessageId();

            StringBuilder sb = new StringBuilder();
            for (Message msg : messages)
                sb.append(formatMessage(msg)).append("\n");
            chatArea.setText(sb.toString());

            SwingUtilities.invokeLater(() -> {
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
                LOGGER.info("已显示与{}的聊天，滚动条：{}", contact, chatScroll.getVerticalScrollBar().getValue());
            });
        } finally {
            isLoading.set(false);
        }
    }

    private void loadMoreMessages() {
        if (currentContact == null || isLoading.get()) {
            return;
        }
        LOGGER.info("开始加载更多聊天记录，滚动条：{}", chatScroll.getVerticalScrollBar().getValue());
        isLoading.set(true);
        try {
            // 获取更早的历史消息
            List<Message> moreMessages = MessageManager.INSTANCE.getConversationMessages(
                    userId, currentContact, MESSAGE_LIMIT, currentOffset);

            if (moreMessages.isEmpty()) {
                return;
            }

            // 确保历史消息按时间戳正序排列
            moreMessages.sort(Comparator.comparing(Message::getTimestamp)
                    .thenComparing(Message::getMessageId));

            // 过滤掉已经显示的消息（防止重复）
            moreMessages.removeIf(msg -> msg.getMessageId() >= lastDisplayedMessageId);

            if (moreMessages.isEmpty()) {
                return;
            }

            // 记录当前滚动位置
            int scrollPos = chatScroll.getVerticalScrollBar().getValue();

            // 构建要插入的历史消息文本
            StringBuilder sb = new StringBuilder();
            for (Message msg : moreMessages) {
                sb.append(formatMessage(msg)).append("\n");
            }
            String newMessages = sb.toString();

            // 将历史消息插入到聊天区域的开头
            chatArea.insert(newMessages, 0);

            // 更新偏移量
            currentOffset += moreMessages.size();

            // 计算新增内容的高度并调整滚动位置
            FontMetrics fm = chatArea.getFontMetrics(chatArea.getFont());
            int lineCount = countLines(newMessages);
            int addedHeight = lineCount * fm.getHeight();

            // 保持用户当前查看位置不变
            SwingUtilities.invokeLater(() -> {
                chatScroll.getVerticalScrollBar().setValue(scrollPos + addedHeight);
            });

        } finally {
            isLoading.set(false);
        }
    }

    private int countLines(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int lines = 1;
        for (char c : str.toCharArray()) {
            if (c == '\n') {
                lines++;
            }
        }
        return lines;
    }

    private void sendMessage(ActionEvent e) {
        String receiver = contactList.getSelectedValue();
        if (receiver == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个联系人", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String content = inputField.getText().trim();
        if (content.isEmpty()) {
            return;
        }

        Message message = MessageService.getInstance().newTempMessage(receiver, content);
        ChatService.getInstance().sendMessage(message);
        inputField.setText("");

        // 发送的新消息添加到聊天区域底部
        chatArea.append(formatMessage(message) + "\n");

        // 更新最后显示的消息ID（如果message有ID的话）
        if (message.getMessageId() > lastDisplayedMessageId) {
            lastDisplayedMessageId = message.getMessageId();
        }

        // 滚动到底部显示新消息
        SwingUtilities.invokeLater(() -> {
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public void receiveMessage(Message message) {
        // 只有当前聊天对象的消息才显示
        if ((message.getSender().equals(currentContact) || message.getReceiver().equals(currentContact))) {

            chatArea.append(formatMessage(message) + "\n");

            // 更新最后显示的消息ID
            if (message.getMessageId() > lastDisplayedMessageId) {
                lastDisplayedMessageId = message.getMessageId();
            }

            // 滚动到底部显示新消息
            SwingUtilities.invokeLater(() -> {
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            });
        }
    }
}