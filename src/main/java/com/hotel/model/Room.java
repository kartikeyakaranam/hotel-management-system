package com.hotel.model;

import javafx.beans.property.*;

public class Room {

    private final IntegerProperty roomNumber  = new SimpleIntegerProperty();
    // ── roomType now stored as RoomType enum internally ────────────────────
    private RoomType roomType = RoomType.STANDARD;
    private final DoubleProperty  pricePerDay = new SimpleDoubleProperty();
    private final BooleanProperty available   = new SimpleBooleanProperty();

    public Room() {}

    public Room(int roomNumber, String roomTypeRaw, double pricePerDay, boolean available) {
        this.roomNumber.set(roomNumber);
        this.roomType = RoomType.fromString(roomTypeRaw);
        this.pricePerDay.set(pricePerDay);
        this.available.set(available);
    }

    public int getRoomNumber()                  { return roomNumber.get(); }
    public void setRoomNumber(int v)            { roomNumber.set(v); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }

    /** Enum getter – for business logic */
    public RoomType getRoomTypeEnum()           { return roomType; }
    public void setRoomTypeEnum(RoomType t)     { this.roomType = t; }

    /** String getter – used by TableView PropertyValueFactory("roomType") */
    public String getRoomType()                 { return roomType.getDisplayName(); }
    public void setRoomType(String s)           { this.roomType = RoomType.fromString(s); }

    public double getPricePerDay()              { return pricePerDay.get(); }
    public void setPricePerDay(double v)        { pricePerDay.set(v); }
    public DoubleProperty pricePerDayProperty() { return pricePerDay; }

    public boolean isAvailable()                { return available.get(); }
    public void setAvailable(boolean v)         { available.set(v); }
    public BooleanProperty availableProperty()  { return available; }

    public String getStatusString() {
        return available.get() ? "✔ Available" : "✘ Occupied";
    }

    @Override
    public String toString() {
        return "Room #" + getRoomNumber() + " – " + getRoomType();
    }
}
