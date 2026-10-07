package ui.panels;

import service.ReportService;
import service.ReportService.WeeklySummary;
import ui.components.BarChartPanel;
import ui.components.LineChartPanel;
import ui.components.Theme;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FitSo Graphical Reports Screen
 */
public class ReportsPanel extends JPanel {

    private final ReportService reportService = new ReportService();

    private JLabel totalWorkoutsVal;
    private JLabel totalMinutesVal;
    private JLabel totalCaloriesVal;
    private JLabel totalEnergyVal;

    private BarChartPanel caloriesChart;
    private BarChartPanel workoutsChart;
    private LineChartPanel weightChart;

    public ReportsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Stats Labels References
        totalWorkoutsVal = new JLabel("0", SwingConstants.CENTER);
        totalMinutesVal = new JLabel("0m", SwingConstants.CENTER);
        totalCaloriesVal = new JLabel("0 kcal", SwingConstants.CENTER);
        totalEnergyVal = new JLabel("0 XP", SwingConstants.CENTER);

        JLabel title = Theme.createTitleLabel("📊  Graphical Fitness Reports");
        add(title, BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new BorderLayout(16, 16));
        contentGrid.setOpaque(false);

        JPanel summaryCard = createWeeklySummaryCard();
        contentGrid.add(summaryCard, BorderLayout.NORTH);

        JPanel chartsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        chartsGrid.setOpaque(false);

        caloriesChart = new BarChartPanel("🔥 Daily Calories Burned (Past 7 Days)", Theme.CYAN_NEON);
        workoutsChart = new BarChartPanel("🏋️ Workouts Logged Per Week (Past 8 Weeks)", Theme.GOLD_GLOW);
        weightChart = new LineChartPanel("📈 Weight Progression History (kg)", Theme.GREEN_EMERALD);

        chartsGrid.add(wrapChartCard(caloriesChart));
        chartsGrid.add(wrapChartCard(workoutsChart));
        chartsGrid.add(wrapChartCard(weightChart));

        JPanel tipCard = Theme.createGlassCard(20);
        tipCard.setLayout(new BorderLayout(12, 12));
        tipCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel tipTitle = Theme.createSubtitleLabel("💡 Fitness Insights");
        JLabel tipText = new JLabel("<html>Consistency beats intensity! Logging workouts regularly keeps your pet happy and unlocks milestone badges faster.</html>");
        tipText.setFont(Theme.FONT_BODY);
        tipText.setForeground(Theme.TEXT_SECONDARY);
        tipCard.add(tipTitle, BorderLayout.NORTH);
        tipCard.add(tipText, BorderLayout.CENTER);

        chartsGrid.add(tipCard);

        contentGrid.add(chartsGrid, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(contentGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        loadReportsData();
    }

    private JPanel createWeeklySummaryCard() {
        JPanel card = Theme.createGlassCard(20);
        card.setLayout(new BorderLayout(12, 12));
        card.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = Theme.createSubtitleLabel("🗓️  Past 7-Day Performance Summary");
        card.add(title, BorderLayout.NORTH);

        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setOpaque(false);

        metricsGrid.add(createMetricBox("Workouts", totalWorkoutsVal, Theme.BLUE_ELECTRIC));
        metricsGrid.add(createMetricBox("Minutes Logged", totalMinutesVal, Theme.GOLD_GLOW));
        metricsGrid.add(createMetricBox("Calories Burned", totalCaloriesVal, Theme.RED_CORAL));
        metricsGrid.add(createMetricBox("Energy Earned", totalEnergyVal, Theme.GREEN_EMERALD));

        card.add(metricsGrid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMetricBox(String label, JLabel valLabel, Color accent) {
        JPanel box = new JPanel(new BorderLayout(0, 4));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createEmptyBorder(4, 12, 4, 4)
        ));

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valLabel.setForeground(Theme.TEXT_PRIMARY);

        box.add(l, BorderLayout.NORTH);
        box.add(valLabel, BorderLayout.CENTER);
        return box;
    }

    private JPanel wrapChartCard(JPanel chartPanel) {
        JPanel card = Theme.createGlassCard(20);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    public void loadReportsData() {
        if (Session.getInstance().getCurrentUser() == null) return;
        int userId = Session.getInstance().getCurrentUser().getUserId();

        try {
            WeeklySummary summary = reportService.getWeeklySummary(userId);
            totalWorkoutsVal.setText(String.valueOf(summary.totalWorkouts));
            totalMinutesVal.setText(summary.totalMinutes + "m");
            totalCaloriesVal.setText(summary.totalCalories + " kcal");
            totalEnergyVal.setText("+" + summary.totalEnergy + " XP");

            Map<Date, Integer> dailyCalMap = reportService.getDailyCaloriesLast7Days(userId);
            Map<String, Integer> formattedCalMap = new LinkedHashMap<>();
            SimpleDateFormat sdfDay = new SimpleDateFormat("EEE");
            for (Map.Entry<Date, Integer> entry : dailyCalMap.entrySet()) {
                formattedCalMap.put(sdfDay.format(entry.getKey()), entry.getValue());
            }
            caloriesChart.setData(formattedCalMap);

            Map<String, Integer> weekWorkoutsMap = reportService.getWorkoutsPerWeekLast8Weeks(userId);
            workoutsChart.setData(weekWorkoutsMap);

            Map<Date, Double> weightMap = reportService.getWeightHistory(userId);
            Map<String, Double> formattedWeightMap = new LinkedHashMap<>();
            SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd");
            for (Map.Entry<Date, Double> entry : weightMap.entrySet()) {
                formattedWeightMap.put(sdfDate.format(entry.getKey()), entry.getValue());
            }
            weightChart.setData(formattedWeightMap);

        } catch (Exception e) {
            System.err.println("Error loading reports: " + e.getMessage());
        }
    }
}
