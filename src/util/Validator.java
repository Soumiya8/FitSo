package util;

import java.util.regex.Pattern;

/**
 * Robust Input Validation Utility.
 */
public class Validator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static void validateRegistration(String username, String email, String password, String fullName, double heightCm) throws IllegalArgumentException {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username must be 3-30 alphanumeric characters or underscores.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name cannot be empty.");
        }
        if (heightCm < 50 || heightCm > 250) {
            throw new IllegalArgumentException("Height must be between 50 cm and 250 cm.");
        }
    }

    public static void validateWorkout(int durationMin, int calories) throws IllegalArgumentException {
        if (durationMin < 1 || durationMin > 600) {
            throw new IllegalArgumentException("Workout duration must be between 1 and 600 minutes.");
        }
        if (calories < 0 || calories > 10000) {
            throw new IllegalArgumentException("Calories burned must be a positive value under 10,000.");
        }
    }

    public static void validateMeasurement(double weightKg, Double waistCm, Double chestCm, Double hipsCm) throws IllegalArgumentException {
        if (weightKg < 20 || weightKg > 400) {
            throw new IllegalArgumentException("Weight must be between 20 kg and 400 kg.");
        }
        if (waistCm != null && waistCm <= 0) {
            throw new IllegalArgumentException("Waist size must be a positive number.");
        }
        if (chestCm != null && chestCm <= 0) {
            throw new IllegalArgumentException("Chest size must be a positive number.");
        }
        if (hipsCm != null && hipsCm <= 0) {
            throw new IllegalArgumentException("Hips size must be a positive number.");
        }
    }
}
