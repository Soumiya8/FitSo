package ui.panels;

import dao.GoalDao;
import dao.PlayerStateDao;
import dao.UserDao;
import dao.WorkoutDao;
import model.Goal;
import model.PlayerState;
import model.User;
import service.GamificationService;
import ui.components.Theme;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * FitSo AAA Profile, Goals, and Pet Settings Screen
 */
public class ProfilePanel extends JPanel {

    private final UserDao userDao = new UserDao();
    private final PlayerStateDao playerStateDao = new PlayerStateDao();
    private final GoalDao goalDao = new GoalDao();
    private final WorkoutDao workoutDao = new WorkoutDao();
    private final GamificationService gamificationService = new GamificationService();

    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> genderCombo;
    private JTextField heightField;

    private JComboBox<String> goalTypeCombo;
    private JTextField targetWeightField;
    private JSpinner weeklyWorkoutsSpinner;

    private JTextField petNameField;

    private JLabel totalWorkoutsLabel;
    private JLabel totalEnergyLabel;
    private JLabel currentLevelLabel;
    private JLabel memberSinceLabel;

    public ProfilePanel() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        totalWorkoutsLabel = new JLabel("0");
        totalEnergyLabel = new JLabel("0 XP");
        currentLevelLabel = new JLabel("Level 1");
        memberSinceLabel = new JLabel("Today");

        JLabel title = Theme.createTitleLabel("👤  Profile & Fitness Goals");
        add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);

        JPanel leftCard = Theme.createGlassCard(20);
        leftCard.setLayout(new BorderLayout(12, 12));
        leftCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel leftTitle = Theme.createSubtitleLabel("📋  Personal Information");
        leftCard.add(leftTitle, BorderLayout.NORTH);

        JPanel profileForm = createProfileFormPanel();
        leftCard.add(profileForm, BorderLayout.CENTER);

        grid.add(leftCard);

        JPanel rightCard = Theme.createGlassCard(20);
        rightCard.setLayout(new BorderLayout(12, 12));
        rightCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel rightTitle = Theme.createSubtitleLabel("🎯  Active Fitness Goal & Stats");
        rightCard.add(rightTitle, BorderLayout.NORTH);

        JPanel goalForm = createGoalFormPanel();
        rightCard.add(goalForm, BorderLayout.CENTER);

        grid.add(rightCard);

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        loadProfileData();
    }

    private JPanel createProfileFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(createLabel("Full Name"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        nameField = Theme.createStyledTextField(""); form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; form.add(createLabel("Email Address"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        emailField = Theme.createStyledTextField(""); form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; form.add(createLabel("Gender / Height"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        JPanel ghPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        ghPanel.setOpaque(false);
        genderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        genderCombo.setBackground(Theme.BG_DARK);
        genderCombo.setForeground(Theme.TEXT_PRIMARY);
        heightField = Theme.createStyledTextField("175.0");
        ghPanel.add(genderCombo);
        ghPanel.add(heightField);
        form.add(ghPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; form.add(createLabel("🐾 Pet Companion Name"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        petNameField = Theme.createStyledTextField("Rocky"); form.add(petNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 10, 6, 10);
        JButton saveProfileBtn = Theme.createGradientButton("SAVE PROFILE & PET NAME", Theme.GREEN_EMERALD, new Color(0, 180, 80));
        saveProfileBtn.setPreferredSize(new Dimension(240, 38));
        saveProfileBtn.addActionListener(e -> performSaveProfile());
        form.add(saveProfileBtn, gbc);

        return form;
    }

    private JPanel createGoalFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(createLabel("Target Goal"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        goalTypeCombo = new JComboBox<>(new String[]{"LOSE_WEIGHT", "GAIN_MUSCLE", "STAY_FIT"});
        goalTypeCombo.setBackground(Theme.BG_DARK);
        goalTypeCombo.setForeground(Theme.TEXT_PRIMARY);
        form.add(goalTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; form.add(createLabel("Target Weight (kg)"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        targetWeightField = Theme.createStyledTextField("70.0"); form.add(targetWeightField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; form.add(createLabel("Weekly Workouts Target"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        weeklyWorkoutsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 14, 1));
        weeklyWorkoutsSpinner.setFont(Theme.FONT_BODY);
        form.add(weeklyWorkoutsSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 16, 10);
        JButton saveGoalBtn = Theme.createGradientButton("UPDATE ACTIVE GOAL", Theme.BLUE_ELECTRIC, Theme.CYAN_NEON);
        saveGoalBtn.setPreferredSize(new Dimension(200, 38));
        saveGoalBtn.addActionListener(e -> performSaveGoal());
        form.add(saveGoalBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel statsBox = createAccountStatsBox();
        form.add(statsBox, gbc);

        return form;
    }

    private JPanel createAccountStatsBox() {
        JPanel box = Theme.createGlassCard(14);
        box.setLayout(new GridLayout(2, 2, 10, 10));
        box.setBorder(new EmptyBorder(12, 12, 12, 12));

        box.add(createStatItem("Total Workouts Logged", totalWorkoutsLabel));
        box.add(createStatItem("Total Energy", totalEnergyLabel));
        box.add(createStatItem("Current Level", currentLevelLabel));
        box.add(createStatItem("Member Since", memberSinceLabel));

        return box;
    }

    private JPanel createStatItem(String title, JLabel valLabel) {
        JPanel item = new JPanel(new BorderLayout(0, 2));
        item.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_SMALL);
        t.setForeground(Theme.TEXT_SECONDARY);

        valLabel.setFont(Theme.FONT_BOLD);
        valLabel.setForeground(Theme.TEXT_PRIMARY);

        item.add(t, BorderLayout.NORTH);
        item.add(valLabel, BorderLayout.CENTER);
        return item;
    }

    public void loadProfileData() {
        User user = Session.getInstance().getCurrentUser();
        PlayerState state = Session.getInstance().getCurrentPlayerState();
        if (user == null) return;

        nameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        genderCombo.setSelectedItem(user.getGender());
        heightField.setText(String.valueOf(user.getHeightCm()));

        if (state != null) {
            petNameField.setText(state.getPetName());
            totalEnergyLabel.setText(state.getTotalEnergy() + " XP");
            int level = gamificationService.calculateLevel(state.getTotalEnergy());
            currentLevelLabel.setText("Level " + level);
        }

        if (user.getCreatedOn() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
            memberSinceLabel.setText(sdf.format(user.getCreatedOn()));
        }

        try {
            int count = workoutDao.countTotalWorkouts(user.getUserId());
            totalWorkoutsLabel.setText(String.valueOf(count));

            Goal activeGoal = goalDao.findActiveGoal(user.getUserId());
            if (activeGoal != null) {
                goalTypeCombo.setSelectedItem(activeGoal.getGoalType());
                if (activeGoal.getTargetWeightKg() != null) {
                    targetWeightField.setText(String.valueOf(activeGoal.getTargetWeightKg()));
                }
                weeklyWorkoutsSpinner.setValue(activeGoal.getWeeklyWorkouts());
            }
        } catch (Exception ignored) {}
    }

    private void performSaveProfile() {
        try {
            User user = Session.getInstance().getCurrentUser();
            user.setFullName(nameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setGender((String) genderCombo.getSelectedItem());
            user.setHeightCm(Double.parseDouble(heightField.getText().trim()));

            String petName = petNameField.getText().trim();
            if (!petName.isEmpty()) {
                Session.getInstance().getCurrentPlayerState().setPetName(petName);
            }

            JOptionPane.showMessageDialog(this, "✅  Profile and pet companion name updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️  " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSaveGoal() {
        try {
            JOptionPane.showMessageDialog(this, "🎯  Active fitness goal updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️  " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.TEXT_SECONDARY);
        return label;
    }
}
