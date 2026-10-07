package ui;

import model.PlayerState;
import model.User;
import service.AuthService;
import ui.components.Theme;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Date;

/**
 * FitSo AAA Gaming Login & Registration Frame
 */
public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    // Login Fields
    private JTextField loginUserField;
    private JPasswordField loginPassField;

    // Register Fields
    private JTextField regUserField;
    private JTextField regEmailField;
    private JPasswordField regPassField;
    private JTextField regNameField;
    private JComboBox<String> regGenderCombo;
    private JTextField regHeightField;

    // Status Label
    private JLabel statusLabel;

    public LoginFrame() {
        Theme.applyGlobalLaf();
        setTitle("FitSo — Cyber Fitness Gaming Platform");
        setSize(960, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Futuristic Main Background Panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Deep Space Cyber Background
                GradientPaint bgGp = new GradientPaint(0, 0, Theme.BG_DARK, getWidth(), getHeight(), new Color(14, 20, 34));
                g2.setPaint(bgGp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Ambient Neon Glow Blobs
                g2.setColor(new Color(0, 242, 254, 18));
                g2.fillOval(-80, -80, 420, 420);

                g2.setColor(new Color(124, 77, 255, 18));
                g2.fillOval(getWidth() - 320, getHeight() - 320, 450, 450);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Header Panel (Logo & Subtitle)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Glass Card
        JPanel cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setOpaque(false);

        JPanel glassCard = createGlassCard();
        cardContainer.add(glassCard);

        mainPanel.add(cardContainer, BorderLayout.CENTER);

        // Bottom Error / Status Notification
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_BOLD);
        statusLabel.setForeground(Theme.CYAN_NEON);
        statusLabel.setBorder(new EmptyBorder(8, 10, 16, 10));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(30, 10, 10, 10));

        JLabel title = new JLabel("⚡ FITSO", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(Theme.FONT_HERO);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("LEVEL UP YOUR FITNESS • GAMIFY YOUR LIFE", SwingConstants.CENTER);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(Theme.FONT_SMALL);
        subtitle.setForeground(Theme.CYAN_NEON);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        return header;
    }

    private JPanel createGlassCard() {
        JPanel card = Theme.createGlassCard(24);
        card.setPreferredSize(new Dimension(540, 430));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 22, 22, 22));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_BOLD);
        tabbedPane.setBackground(Theme.PANEL_BG);
        tabbedPane.setForeground(Theme.TEXT_PRIMARY);

        // Tab 1: Sign In
        JPanel loginTab = createLoginTab();
        tabbedPane.addTab("🔑  SIGN IN", loginTab);

        // Tab 2: Create Account
        JPanel registerTab = createRegisterTab();
        tabbedPane.addTab("✨  CREATE ACCOUNT", registerTab);

        card.add(tabbedPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createLoginTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; panel.add(createLabel("Username"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        loginUserField = Theme.createStyledTextField("alex_fit");
        panel.add(loginUserField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; panel.add(createLabel("Password"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        loginPassField = Theme.createStyledPasswordField("Password123!");
        panel.add(loginPassField, gbc);

        // Buttons Panel
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(24, 15, 10, 15);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        btnPanel.setOpaque(false);

        JButton loginBtn = Theme.createGradientButton("SIGN IN", Theme.GREEN_EMERALD, new Color(0, 180, 80));
        loginBtn.addActionListener(e -> performLogin());

        JButton demoBtn = Theme.createGradientButton("⚡ DEMO LOGIN", Theme.CYAN_NEON, Theme.BLUE_ELECTRIC);
        demoBtn.addActionListener(e -> performDemoLogin());

        btnPanel.add(loginBtn);
        btnPanel.add(demoBtn);

        panel.add(btnPanel, gbc);

        return panel;
    }

    private JPanel createRegisterTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; panel.add(createLabel("Username"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        regUserField = Theme.createStyledTextField(""); panel.add(regUserField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; panel.add(createLabel("Email"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        regEmailField = Theme.createStyledTextField(""); panel.add(regEmailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; panel.add(createLabel("Password"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        regPassField = Theme.createStyledPasswordField(""); panel.add(regPassField, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; panel.add(createLabel("Full Name"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        regNameField = Theme.createStyledTextField(""); panel.add(regNameField, gbc);

        // Gender & Height
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; panel.add(createLabel("Gender / Height"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;

        JPanel ghPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        ghPanel.setOpaque(false);
        regGenderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        regGenderCombo.setBackground(Theme.BG_DARK);
        regGenderCombo.setForeground(Theme.TEXT_PRIMARY);

        regHeightField = Theme.createStyledTextField("175.0");
        ghPanel.add(regGenderCombo);
        ghPanel.add(regHeightField);
        panel.add(ghPanel, gbc);

        // Submit Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 10, 4, 10);

        JButton regBtn = Theme.createGradientButton("CREATE ACCOUNT", Theme.GREEN_EMERALD, new Color(0, 180, 80));
        regBtn.addActionListener(e -> performRegistration());
        panel.add(regBtn, gbc);

        return panel;
    }

    private void performLogin() {
        String username = loginUserField.getText().trim();
        String password = new String(loginPassField.getPassword());

        try {
            User user = authService.login(username, password);
            PlayerState state = authService.getPlayerState(user.getUserId());
            Session.getInstance().startSession(user, state);
            launchMainApp();
        } catch (Exception e) {
            showStatus(e.getMessage());
        }
    }

    private void performDemoLogin() {
        loginUserField.setText("alex_fit");
        loginPassField.setText("Password123!");
        performLogin();
    }

    private void performRegistration() {
        try {
            String username = regUserField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = new String(regPassField.getPassword());
            String fullName = regNameField.getText().trim();
            String gender = (String) regGenderCombo.getSelectedItem();
            double height = Double.parseDouble(regHeightField.getText().trim());

            User user = authService.register(username, email, password, fullName, gender, new Date(), height);
            PlayerState state = authService.getPlayerState(user.getUserId());
            Session.getInstance().startSession(user, state);

            launchMainApp();
        } catch (Exception e) {
            showStatus(e.getMessage());
        }
    }

    private void launchMainApp() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

    private void showStatus(String msg) {
        statusLabel.setText("⚠️  " + msg);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.TEXT_SECONDARY);
        return label;
    }
}
