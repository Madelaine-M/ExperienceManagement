package ui.view;

import java.util.Locale;

public final class DashboardFormatters {
    private DashboardFormatters() {
    }

    public static String formatName(String firstName, String lastName) {
        return defaultText(firstName, "").trim() + " " + defaultText(lastName, "").trim();
    }

    public static String formatPackage(Object bookingPackage) {
        return bookingPackage == null ? "n/a" : bookingPackage.toString();
    }

    public static String formatValue(Object value) {
        return value == null ? "n/a" : value.toString().replace('_', ' ');
    }

    public static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    public static String formatCurrency(int value) {
        return String.format(Locale.GERMANY, "%,d€", value);
    }

    public static String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    public static String priorityLabel(double score) {
        if (score >= 8) {
            return "HIGH PRIORITY";
        }
        if (score >= 5) {
            return "MEDIUM PRIORITY";
        }
        return "LOW PRIORITY";
    }

    public static String priorityStyle(double score) {
        if (score >= 8) {
            return "priority-high";
        }
        if (score >= 5) {
            return "priority-medium";
        }
        return "priority-low";
    }
}
