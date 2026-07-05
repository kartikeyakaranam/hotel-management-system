package com.hotel.model;

/**
 * Canonical set of room categories recognised by the system.
 */
public enum RoomType {
    STANDARD("Standard"),
    DOUBLE("Double"),
    DELUXE("Deluxe"),
    SUITE("Suite"),
    EXECUTIVE("Executive");

    private final String displayName;

    RoomType(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }

    /** Parse from a DB / form string; returns STANDARD as fallback. */
    public static RoomType fromString(String s) {
        if (s == null) return STANDARD;
        for (RoomType t : values()) {
            if (t.displayName.equalsIgnoreCase(s.trim())) return t;
        }
        return STANDARD;
    }

    @Override
    public String toString() { return displayName; }
}
