package com.hotel.model;

import javafx.beans.property.*;

public class Booking {

    private final IntegerProperty bookingId    = new SimpleIntegerProperty();
    private final IntegerProperty customerId   = new SimpleIntegerProperty();
    private final IntegerProperty roomNumber   = new SimpleIntegerProperty();
    private final StringProperty  checkIn      = new SimpleStringProperty();
    private final StringProperty  checkOut     = new SimpleStringProperty();
    private final IntegerProperty numDays      = new SimpleIntegerProperty();
    // ── status now uses BookingStatus enum internally ──────────────────────
    private BookingStatus status = BookingStatus.ACTIVE;

    // Denormalized — populated via JOIN for display
    private final StringProperty customerName = new SimpleStringProperty();
    private final StringProperty roomType     = new SimpleStringProperty();

    public Booking() {}

    public Booking(int bookingId, int customerId, int roomNumber,
                   String checkIn, String checkOut, int numDays, String statusRaw) {
        this.bookingId.set(bookingId);
        this.customerId.set(customerId);
        this.roomNumber.set(roomNumber);
        this.checkIn.set(checkIn  == null ? "" : checkIn);
        this.checkOut.set(checkOut == null ? "" : checkOut);
        this.numDays.set(numDays);
        this.status = BookingStatus.fromDb(statusRaw);
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public int getBookingId()                  { return bookingId.get(); }
    public void setBookingId(int v)            { bookingId.set(v); }
    public IntegerProperty bookingIdProperty() { return bookingId; }

    public int getCustomerId()                  { return customerId.get(); }
    public void setCustomerId(int v)            { customerId.set(v); }
    public IntegerProperty customerIdProperty() { return customerId; }

    public int getRoomNumber()                  { return roomNumber.get(); }
    public void setRoomNumber(int v)            { roomNumber.set(v); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }

    public String getCheckIn()                 { return checkIn.get(); }
    public void setCheckIn(String v)           { checkIn.set(v); }
    public StringProperty checkInProperty()    { return checkIn; }

    public String getCheckOut()                { return checkOut.get(); }
    public void setCheckOut(String v)          { checkOut.set(v); }
    public StringProperty checkOutProperty()   { return checkOut; }

    public int getNumDays()                    { return numDays.get(); }
    public void setNumDays(int v)              { numDays.set(v); }
    public IntegerProperty numDaysProperty()   { return numDays; }

    /** Enum getter – used in business logic */
    public BookingStatus getStatusEnum()       { return status; }
    public void setStatusEnum(BookingStatus s) { this.status = s; }

    /** String getter – used by TableView PropertyValueFactory("status") */
    public String getStatus()                  { return status.getDisplayLabel(); }
    public void setStatus(String raw)          { this.status = BookingStatus.fromDb(raw); }

    public String getCustomerName()            { return customerName.get(); }
    public void setCustomerName(String v)      { customerName.set(v); }
    public StringProperty customerNameProperty(){ return customerName; }

    public String getRoomType()                { return roomType.get(); }
    public void setRoomType(String v)          { roomType.set(v); }
    public StringProperty roomTypeProperty()   { return roomType; }
}
