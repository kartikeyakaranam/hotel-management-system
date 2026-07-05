package com.hotel.model;

/**
 * Represents the lifecycle state of a booking.
 * Used everywhere instead of raw strings like "ACTIVE" / "CHECKED_OUT".
 */
public enum BookingStatus {
    ACTIVE("ACTIVE", "🟢 Active"),
    CHECKED_OUT("CHECKED_OUT", "🔵 Checked Out"),
    CANCELLED("CANCELLED", "🔴 Cancelled");

    private final String dbValue;     // stored in DB column
    private final String displayLabel; // shown in UI

    BookingStatus(String dbValue, String displayLabel) {
        this.dbValue      = dbValue;
        this.displayLabel = displayLabel;
    }

    public String getDbValue()      { return dbValue; }
    public String getDisplayLabel() { return displayLabel; }

    /** Parse from the raw DB string; defaults to ACTIVE if unknown. */
    public static BookingStatus fromDb(String raw) {
        if (raw == null) return ACTIVE;
        for (BookingStatus s : values()) {
            if (s.dbValue.equalsIgnoreCase(raw)) return s;
        }
        return ACTIVE;
    }

    @Override
    public String toString() { return displayLabel; }
}
