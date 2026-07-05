package com.hotel.util;

/**
 * Central validation utility for the Hotel Management System.
 * All input checks live here so controllers stay clean.
 */
public class ValidationUtil {

    /** Returns true only if the string is exactly 10 decimal digits. */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    /** Returns true if numDays > 0 */
    public static boolean isValidDays(int days) {
        return days >= 1;
    }

    /** Returns true if email is empty (optional) OR matches a basic pattern. */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return true;   // optional field
        return email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");
    }

    /** Returns true if price is a positive number (> 0). */
    public static boolean isValidPrice(double price) {
        return price > 0;
    }
}
