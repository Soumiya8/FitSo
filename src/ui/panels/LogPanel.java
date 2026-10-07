package ui.panels;

import dao.WorkoutTypeDao;
import model.BodyMeasurement;
import model.Mission;
import model.WorkoutType;
import service.WorkoutService;
import service.WorkoutService.WorkoutResult;
import ui.components.Theme;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * FitSo Workout & Body Measurement Logging Screen
 */
public class LogPanel extends JPanel {

    private final WorkoutService workoutService = new WorkoutService();
    private final WorkoutTypeDao workoutTypeDao = new WorkoutTypeDao();

    private JComboBox<WorkoutType> typeCombo;
    private JSpinner durationSpinner;
    private JComboBox<String> intensityCombo;
    private JTextField caloriesField;
    private JTextField notesField;
    private JLabel calcNoticeLabel;

    private JTextField weightField;
    private JTextField waistField;
    private JTextField chestField;
    private JTextField hipsField;

    private final Runnable onLogSuccessCallback;

    public LogPanel(Runnable onLogSuccessCallback) {
        this.onLogSuccessCallback = onLogSuccessCallback;
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = Theme.createTitleLabel("📝  Log Fitness Data");
        add(title, BorderLayout.NORTH);

        JPanel card = Theme.createGlassCard(20);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_BOLD);
        tabbedPane.setBackground(Theme.PANEL_BG);
        tabbedPane.setForeground(Theme.TEXT_PRIMARY);

        tabbedPane.addTab("🏋️  Log Workout", createWorkoutFormTab());
        tabbedPane.addTab("📏  Body Measurements", createMeasurementFormTab());

        card.add(tabbedPane, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        loadWorkoutTypes();
    }

    private JPanel createWorkoutFormTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Exercise Type
        gbc.gridx = 0; gbc.gridy = 0; form.add(createFormLabel("Exercise Type"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        typeCombo = new JComboBox<>();
        typeCombo.setBackground(Theme.BG_DARK);
        typeCombo.setForeground(Theme.TEXT_PRIMARY);
        typeCombo.addActionListener(e -> updateEstimatedCalories());
        form.add(typeCombo, gbc);

        // Duration (Minutes)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; form.add(createFormLabel("Duration (Minutes)"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        durationSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 600, 5));
        durationSpinner.setFont(Theme.FONT_BODY);
        durationSpinner.addChangeListener(e -> updateEstimatedCalories());
        form.add(durationSpinner, gbc);

        // Intensity
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; form.add(createFormLabel("Intensity"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        intensityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        intensityCombo.setSelectedIndex(1);
        intensityCombo.setBackground(Theme.BG_DARK);
        intensityCombo.setForeground(Theme.TEXT_PRIMARY);
        form.add(intensityCombo, gbc);

        // Calories Burned
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; form.add(createFormLabel("Calories Burned (kcal)"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;

        JPanel calPanel = new JPanel(new BorderLayout(10, 0));
        calPanel.setOpaque(false);
        caloriesField = Theme.createStyledTextField("200");
        calcNoticeLabel = new JLabel("⚡ Auto-estimated from MET value");
        calcNoticeLabel.setFont(Theme.FONT_SMALL);
        calcNoticeLabel.setForeground(Theme.CYAN_NEON);
        calPanel.add(caloriesField, BorderLayout.CENTER);
        calPanel.add(calcNoticeLabel, BorderLayout.SOUTH);
        form.add(calPanel, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; form.add(createFormLabel("Workout Notes (Optional)"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        notesField = Theme.createStyledTextField("");
        form.add(notesField, gbc);

        // Submit Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 10, 15);
        JButton submitBtn = Theme.createGradientButton("SAVE WORKOUT & EARN REWARDS", Theme.GREEN_EMERALD, new Color(0, 180, 80));
        submitBtn.setPreferredSize(new Dimension(250, 42));
        submitBtn.addActionListener(e -> performLogWorkout());
        form.add(submitBtn, gbc);

        return form;
    }

    private JPanel createMeasurementFormTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(createFormLabel("Weight (kg) * Required"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        weightField = Theme.createStyledTextField("70.0");
        form.add(weightField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; form.add(createFormLabel("Waist Circumference (cm)"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        waistField = Theme.createStyledTextField("");
        form.add(waistField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; form.add(createFormLabel("Chest Circumference (cm)"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        chestField = Theme.createStyledTextField("");
        form.add(chestField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; form.add(createFormLabel("Hips Circumference (cm)"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        hipsField = Theme.createStyledTextField("");
        form.add(hipsField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 10, 15);
        JButton submitBtn = Theme.createGradientButton("SAVE MEASUREMENTS", Theme.BLUE_ELECTRIC, Theme.CYAN_NEON);
        submitBtn.setPreferredSize(new Dimension(220, 42));
        submitBtn.addActionListener(e -> performSaveMeasurement());
        form.add(submitBtn, gbc);

        return form;
    }

    private void loadWorkoutTypes() {
        try {
            List<WorkoutType> types = workoutTypeDao.findAll();
            typeCombo.removeAllItems();
            for (WorkoutType t : types) {
                typeCombo.addItem(t);
            }
            updateEstimatedCalories();
        } catch (Exception ignored) {
            typeCombo.addItem(new WorkoutType(1, "Running", 9.8));
            typeCombo.addItem(new WorkoutType(2, "Cycling", 7.5));
            typeCombo.addItem(new WorkoutType(3, "Strength Training", 5.0));
            typeCombo.addItem(new WorkoutType(4, "Walking", 3.5));
            typeCombo.addItem(new WorkoutType(5, "HIIT", 8.0));
            updateEstimatedCalories();
        }
    }

    private void updateEstimatedCalories() {
        WorkoutType type = (WorkoutType) typeCombo.getSelectedItem();
        if (type == null) return;

        int duration = (int) durationSpinner.getValue();
        double met = type.getMetValue();

        int estimatedCal = (int) Math.round(met * 70.0 * (duration / 60.0));
        caloriesField.setText(String.valueOf(estimatedCal));
    }

    private void performLogWorkout() {
        try {
            int userId = Session.getInstance().getCurrentUser().getUserId();
            WorkoutType selectedType = (WorkoutType) typeCombo.getSelectedItem();
            if (selectedType == null) return;

            int duration = (int) durationSpinner.getValue();
            String intensity = (String) intensityCombo.getSelectedItem();
            int calories = Integer.parseInt(caloriesField.getText().trim());
            String notes = notesField.getText().trim();

            WorkoutResult result = workoutService.logWorkout(userId, selectedType.getTypeId(), new Date(), duration, intensity, calories, notes);

            Session.getInstance().getCurrentPlayerState().setTotalEnergy(
                Session.getInstance().getCurrentPlayerState().getTotalEnergy() + result.energyEarned
            );
            Session.getInstance().getCurrentPlayerState().setCoins(
                Session.getInstance().getCurrentPlayerState().getCoins() + result.coinsEarned
            );

            showRewardDialog(result);

            if (onLogSuccessCallback != null) {
                onLogSuccessCallback.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️  " + e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSaveMeasurement() {
        try {
            int userId = Session.getInstance().getCurrentUser().getUserId();
            double weight = Double.parseDouble(weightField.getText().trim());
            Double waist = parseOptionalDouble(waistField.getText().trim());
            Double chest = parseOptionalDouble(chestField.getText().trim());
            Double hips = parseOptionalDouble(hipsField.getText().trim());

            BodyMeasurement m = new BodyMeasurement(0, userId, new Date(), weight, waist, chest, hips);
            workoutService.logMeasurement(m);

            JOptionPane.showMessageDialog(this, "✅  Body measurements recorded successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);

            if (onLogSuccessCallback != null) {
                onLogSuccessCallback.run();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠️  Weight must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️  " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRewardDialog(WorkoutResult result) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Workout Complete!", true);
        dialog.setSize(440, 330);
        dialog.setLocationRelativeTo(this);

        JPanel p = Theme.createGlassCard(20);
        p.setLayout(new BorderLayout(15, 15));
        p.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("🎉 WORKOUT COMPLETE!", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.GREEN_EMERALD);
        p.add(title, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel energyLbl = new JLabel("⚡ Energy Earned: +" + result.energyEarned + " XP", SwingConstants.CENTER);
        energyLbl.setFont(Theme.FONT_SUBTITLE);
        energyLbl.setForeground(Theme.TEXT_PRIMARY);

        JLabel coinsLbl = new JLabel("🪙 Coins Earned: +" + result.coinsEarned + " Coins", SwingConstants.CENTER);
        coinsLbl.setFont(Theme.FONT_SUBTITLE);
        coinsLbl.setForeground(Theme.GOLD_GLOW);

        info.add(energyLbl);
        info.add(Box.createVerticalStrut(8));
        info.add(coinsLbl);

        if (result.completedMissions != null && !result.completedMissions.isEmpty()) {
            info.add(Box.createVerticalStrut(12));
            JLabel mCompleted = new JLabel("🏆 Missions Unlocked: " + result.completedMissions.size(), SwingConstants.CENTER);
            mCompleted.setFont(Theme.FONT_BOLD);
            mCompleted.setForeground(Theme.CYAN_NEON);
            info.add(mCompleted);
        }

        p.add(info, BorderLayout.CENTER);

        JButton okBtn = Theme.createGradientButton("AWESOME!", Theme.GREEN_EMERALD, new Color(0, 180, 80));
        okBtn.addActionListener(e -> dialog.dispose());
        p.add(okBtn, BorderLayout.SOUTH);

        dialog.setContentPane(p);
        dialog.setVisible(true);
    }

    private Double parseOptionalDouble(String str) {
        if (str == null || str.isEmpty()) return null;
        return Double.parseDouble(str);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.TEXT_SECONDARY);
        return label;
    }
}
