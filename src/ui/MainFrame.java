package ui;

import model.User;
import ui.components.Theme;
import ui.panels.DashboardPanel;
import ui.panels.LogPanel;
import ui.panels.ProfilePanel;
import ui.panels.ReportsPanel;
import ui.panels.ShopPanel;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FitSo AAA Gaming Container
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentArea;

    private DashboardPanel dashboardPanel;
    private LogPanel logPanel;
    private ReportsPanel reportsPanel;
    private ShopPanel shopPanel;
    private ProfilePanel profilePanel;

    private JPanel navDashboard;
    private JPanel navLog;
    private JPanel navReports;
    private JPanel navShop;
    private JPanel navProfile;

    private String currentActiveCard = "DASHBOARD";

    public MainFrame() {
        Theme.applyGlobalLaf();
        setTitle("FitSo — Cyber Fitness Gaming Platform");
        setSize(1140, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Theme.BG_DARK);

        JPanel sidebar = createSidebar();
        mainContainer.add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setOpaque(false);

        dashboardPanel = new DashboardPanel(() -> showCard("LOG"));
        logPanel = new LogPanel(() -> {
            dashboardPanel.refreshDashboardData();
            reportsPanel.loadReportsData();
            shopPanel.loadShopItems();
            showCard("DASHBOARD");
        });
        reportsPanel = new ReportsPanel();
        shopPanel = new ShopPanel(() -> {
            dashboardPanel.refreshDashboardData();
        });
        profilePanel = new ProfilePanel();

        contentArea.add(dashboardPanel, "DASHBOARD");
        contentArea.add(logPanel, "LOG");
        contentArea.add(reportsPanel, "REPORTS");
        contentArea.add(shopPanel, "SHOP");
        contentArea.add(profilePanel, "PROFILE");

        mainContainer.add(contentArea, BorderLayout.CENTER);
        setContentPane(mainContainer);

        showCard("DASHBOARD");
    }

    public void showCard(String cardName) {
        if ("DASHBOARD".equals(cardName)) {
            dashboardPanel.refreshDashboardData();
        } else if ("REPORTS".equals(cardName)) {
            reportsPanel.loadReportsData();
        } else if ("SHOP".equals(cardName)) {
            shopPanel.loadShopItems();
        } else if ("PROFILE".equals(cardName)) {
            profilePanel.loadProfileData();
        }
        cardLayout.show(contentArea, cardName);
        updateNavSelection(cardName);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, 740));
        sidebar.setBackground(Theme.PANEL_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.CARD_BORDER));

        // Top Brand Logo
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 24));
        brandPanel.setOpaque(false);
        JLabel brandLabel = new JLabel("⚡ FITSO");
        brandLabel.setFont(Theme.FONT_TITLE);
        brandLabel.setForeground(Theme.CYAN_NEON);
        brandPanel.add(brandLabel);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Items List
        JPanel navList = new JPanel();
        navList.setOpaque(false);
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));

        navDashboard = createNavItem("🏠   Dashboard", "DASHBOARD");
        navLog = createNavItem("📝   Log Fitness", "LOG");
        navReports = createNavItem("📊   Reports", "REPORTS");
        navShop = createNavItem("🛍️   Shop & Items", "SHOP");
        navProfile = createNavItem("👤   Profile & Goals", "PROFILE");

        navList.add(navDashboard);
        navList.add(Box.createVerticalStrut(6));
        navList.add(navLog);
        navList.add(Box.createVerticalStrut(6));
        navList.add(navReports);
        navList.add(Box.createVerticalStrut(6));
        navList.add(navShop);
        navList.add(Box.createVerticalStrut(6));
        navList.add(navProfile);

        sidebar.add(navList, BorderLayout.CENTER);

        JPanel bottomUser = createBottomUserPanel();
        sidebar.add(bottomUser, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createNavItem(String labelText, String cardKey) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(210, 44));
        item.setPreferredSize(new Dimension(210, 44));
        item.setBorder(new EmptyBorder(10, 18, 10, 18));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.TEXT_SECONDARY);
        item.add(label, BorderLayout.WEST);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCard(cardKey);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardKey.equalsIgnoreCase(currentActiveCard)) {
                    item.setBackground(Theme.PANEL_BG_LIGHT);
                    item.setOpaque(true);
                    item.repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardKey.equalsIgnoreCase(currentActiveCard)) {
                    item.setOpaque(false);
                    item.repaint();
                }
            }
        });

        return item;
    }

    private void updateNavSelection(String cardKey) {
        this.currentActiveCard = cardKey;
        resetNavItem(navDashboard, "DASHBOARD".equals(cardKey));
        resetNavItem(navLog, "LOG".equals(cardKey));
        resetNavItem(navReports, "REPORTS".equals(cardKey));
        resetNavItem(navShop, "SHOP".equals(cardKey));
        resetNavItem(navProfile, "PROFILE".equals(cardKey));
    }

    private void resetNavItem(JPanel item, boolean active) {
        if (item == null) return;
        JLabel l = (JLabel) item.getComponent(0);
        if (active) {
            item.setBackground(Theme.PANEL_BG_LIGHT);
            item.setOpaque(true);
            l.setForeground(Theme.CYAN_NEON);
        } else {
            item.setOpaque(false);
            l.setForeground(Theme.TEXT_SECONDARY);
        }
        item.repaint();
    }

    private JPanel createBottomUserPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 18, 18));

        User user = Session.getInstance().getCurrentUser();
        String uName = user != null ? user.getUsername() : "alex_fit";

        JLabel uLabel = new JLabel("👤 " + uName);
        uLabel.setFont(Theme.FONT_BOLD);
        uLabel.setForeground(Theme.TEXT_PRIMARY);

        JButton logoutBtn = Theme.createGradientButton("🚪", Theme.RED_CORAL, new Color(220, 50, 50));
        logoutBtn.setToolTipText("Sign Out");
        logoutBtn.setPreferredSize(new Dimension(36, 34));
        logoutBtn.addActionListener(e -> {
            Session.getInstance().clearSession();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        p.add(uLabel, BorderLayout.WEST);
        p.add(logoutBtn, BorderLayout.EAST);

        return p;
    }
}
