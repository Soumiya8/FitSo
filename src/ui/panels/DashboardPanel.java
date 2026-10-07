package ui.panels;

import dao.MissionDao;
import model.Mission;
import model.PlayerState;
import model.User;
import service.GamificationService;
import service.GamificationService.CharacterState;
import service.GamificationService.PetState;
import service.GamificationService.WorldStage;
import ui.components.CharacterView;
import ui.components.EnergyBar;
import ui.components.Theme;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * FitSo AAA Cyber Gaming Dashboard
 */
public class DashboardPanel extends JPanel {

    private final GamificationService gamificationService = new GamificationService();
    private final MissionDao missionDao = new MissionDao();

    private CharacterView characterView;
    private EnergyBar energyBar;
    private JLabel coinsLabel;
    private JLabel promptLabel;
    private JPanel missionsListContainer;
    private JLabel worldTitleLabel;

    private final Runnable navigateToLogAction;

    public DashboardPanel(Runnable navigateToLogAction) {
        this.navigateToLogAction = navigateToLogAction;
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // 1. Top Header Bar
        JPanel topStrip = createTopStrip();
        add(topStrip, BorderLayout.NORTH);

        // 2. Center Split (Left: Avatar & World Roadmap | Right: Active Missions)
        JPanel centerSplit = new JPanel(new GridLayout(1, 2, 16, 0));
        centerSplit.setOpaque(false);

        // Left Card: World Stage + Avatar + Speech Prompt + Level Roadmap
        JPanel leftCard = Theme.createGlassCard(20);
        leftCard.setLayout(new BorderLayout(12, 12));
        leftCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        worldTitleLabel = Theme.createSubtitleLabel("🏡  BEDROOM STAGE (LEVEL 1)");
        leftCard.add(worldTitleLabel, BorderLayout.NORTH);

        characterView = new CharacterView();
        leftCard.add(characterView, BorderLayout.CENTER);

        promptLabel = new JLabel("Ready for today's workout? Let's level up!", SwingConstants.CENTER);
        promptLabel.setFont(Theme.FONT_BOLD);
        promptLabel.setForeground(Theme.TEXT_PRIMARY);
        promptLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.CYAN_NEON, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JPanel leftBottom = new JPanel(new BorderLayout(0, 12));
        leftBottom.setOpaque(false);
        leftBottom.add(promptLabel, BorderLayout.NORTH);
        leftBottom.add(createRoadmapPanel(), BorderLayout.SOUTH);

        leftCard.add(leftBottom, BorderLayout.SOUTH);
        centerSplit.add(leftCard);

        // Right Card: Active Missions List & Quick Action Button
        JPanel rightCard = Theme.createGlassCard(20);
        rightCard.setLayout(new BorderLayout(12, 12));
        rightCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel missionsHeader = new JPanel(new BorderLayout());
        missionsHeader.setOpaque(false);
        JLabel mTitle = Theme.createTitleLabel("🎯  ACTIVE MISSIONS");

        JButton logQuickBtn = Theme.createGradientButton("+ LOG WORKOUT", Theme.GREEN_EMERALD, new Color(0, 180, 80));
        logQuickBtn.setPreferredSize(new Dimension(140, 34));
        logQuickBtn.addActionListener(e -> navigateToLogAction.run());

        missionsHeader.add(mTitle, BorderLayout.WEST);
        missionsHeader.add(logQuickBtn, BorderLayout.EAST);
        rightCard.add(missionsHeader, BorderLayout.NORTH);

        missionsListContainer = new JPanel();
        missionsListContainer.setOpaque(false);
        missionsListContainer.setLayout(new BoxLayout(missionsListContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(missionsListContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        rightCard.add(scrollPane, BorderLayout.CENTER);

        centerSplit.add(rightCard);

        add(centerSplit, BorderLayout.CENTER);

        refreshDashboardData();
    }

    private JPanel createTopStrip() {
        JPanel top = Theme.createGlassCard(16);
        top.setLayout(new BorderLayout(15, 0));
        top.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel greeting = new JLabel("WELCOME BACK, ATHLETE 👋");
        greeting.setFont(Theme.FONT_SUBTITLE);
        greeting.setForeground(Theme.TEXT_PRIMARY);
        top.add(greeting, BorderLayout.WEST);

        energyBar = new EnergyBar();
        top.add(energyBar, BorderLayout.CENTER);

        coinsLabel = new JLabel("🪙  0 COINS");
        coinsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        coinsLabel.setForeground(Theme.GOLD_GLOW);
        top.add(coinsLabel, BorderLayout.EAST);

        return top;
    }

    private JPanel createRoadmapPanel() {
        JPanel roadmap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int y = getHeight() / 2;

                g2.setColor(Theme.CARD_BORDER);
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawLine(30, y, w - 30, y);

                g2.dispose();
            }
        };
        roadmap.setOpaque(false);
        roadmap.setPreferredSize(new Dimension(300, 48));
        roadmap.setLayout(new GridLayout(1, 4, 10, 0));

        int[] levels = {1, 3, 5, 8};

        for (int i = 0; i < 4; i++) {
            JLabel node = new JLabel("Lv " + levels[i], SwingConstants.CENTER);
            node.setFont(Theme.FONT_BOLD);
            node.setForeground(Theme.CYAN_NEON);
            roadmap.add(node);
        }

        return roadmap;
    }

    public void refreshDashboardData() {
        User user = Session.getInstance().getCurrentUser();
        PlayerState state = Session.getInstance().getCurrentPlayerState();
        if (user == null || state == null) return;

        int level = gamificationService.calculateLevel(state.getTotalEnergy());
        int progressPercent = gamificationService.calculateLevelProgressPercent(state.getTotalEnergy());
        WorldStage stage = gamificationService.getWorldStage(level);

        energyBar.updateProgress(level, state.getTotalEnergy(), progressPercent);
        coinsLabel.setText("🪙  " + state.getCoins() + " COINS");

        String stageIcon = stage == WorldStage.BEDROOM ? "🏡" : stage == WorldStage.BACKYARD ? "🌳" : stage == WorldStage.PARK ? "⛲" : "⛰️";
        worldTitleLabel.setText(stageIcon + "  " + stage.name().replace('_', ' ') + " (LEVEL " + level + ")");

        CharacterState charState = gamificationService.deriveCharacterState(false, false, new Date());
        PetState petState = gamificationService.derivePetState(false, false, new Date());
        characterView.updateState(charState, petState, state.getPetName());

        promptLabel.setText(gamificationService.getPromptMessage(charState, state.getPetName()));

        loadMissionsData(user.getUserId());
    }

    private void loadMissionsData(int userId) {
        missionsListContainer.removeAll();
        try {
            List<Mission> missions = missionDao.findAll();
            Set<Integer> completedIds = missionDao.findCompletedMissionIds(userId);

            for (Mission m : missions) {
                boolean completed = completedIds.contains(m.getMissionId());
                JPanel mCard = createMissionCard(m, completed);
                missionsListContainer.add(mCard);
                missionsListContainer.add(Box.createVerticalStrut(10));
            }
        } catch (Exception e) {
            // Fallback default missions
            missionsListContainer.add(createMissionCard(new Mission(1, "First Steps", "WORKOUT_COUNT", 1, 20, 50), true));
            missionsListContainer.add(Box.createVerticalStrut(10));
            missionsListContainer.add(createMissionCard(new Mission(2, "Quarter Hour", "SINGLE_MINUTES", 15, 20, 50), false));
            missionsListContainer.add(Box.createVerticalStrut(10));
            missionsListContainer.add(createMissionCard(new Mission(3, "Hat Trick", "WORKOUT_COUNT", 3, 20, 50), false));
        }
        missionsListContainer.revalidate();
        missionsListContainer.repaint();
    }

    private JPanel createMissionCard(Mission m, boolean completed) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(completed ? Theme.GREEN_EMERALD : Theme.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel title = new JLabel((completed ? "✅  " : "🎯  ") + m.getTitle());
        title.setFont(Theme.FONT_BOLD);
        title.setForeground(completed ? Theme.GREEN_EMERALD : Theme.TEXT_PRIMARY);

        JLabel reward = new JLabel("+" + m.getRewardEnergy() + " XP • +" + m.getRewardCoins() + " Coins");
        reward.setFont(Theme.FONT_SMALL);
        reward.setForeground(Theme.GOLD_GLOW);

        card.add(title, BorderLayout.WEST);
        card.add(reward, BorderLayout.EAST);

        return card;
    }
}
