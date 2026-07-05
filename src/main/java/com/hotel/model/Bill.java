package com.hotel.model;

import javafx.beans.property.*;

public class Bill {

    private final IntegerProperty billId       = new SimpleIntegerProperty();
    private final IntegerProperty bookingId    = new SimpleIntegerProperty();
    private final IntegerProperty customerId   = new SimpleIntegerProperty();
    private final IntegerProperty roomNumber   = new SimpleIntegerProperty();
    private final IntegerProperty numDays      = new SimpleIntegerProperty();
    private final DoubleProperty  roomCharges  = new SimpleDoubleProperty();
    private final DoubleProperty  serviceTax   = new SimpleDoubleProperty();
    private final DoubleProperty  totalAmount  = new SimpleDoubleProperty();
    private final StringProperty  billDate     = new SimpleStringProperty();

    // Denormalized for display
    private final StringProperty  customerName = new SimpleStringProperty();

    public Bill() {}

    public Bill(int billId, int bookingId, int customerId, int roomNumber,
                int numDays, double roomCharges, double serviceTax,
                double totalAmount, String billDate) {
        this.billId.set(billId);
        this.bookingId.set(bookingId);
        this.customerId.set(customerId);
        this.roomNumber.set(roomNumber);
        this.numDays.set(numDays);
        this.roomCharges.set(roomCharges);
        this.serviceTax.set(serviceTax);
        this.totalAmount.set(totalAmount);
        this.billDate.set(billDate == null ? "" : billDate);
    }

    public int getBillId()                   { return billId.get(); }
    public void setBillId(int v)             { billId.set(v); }
    public IntegerProperty billIdProperty()  { return billId; }

    public int getBookingId()                { return bookingId.get(); }
    public void setBookingId(int v)          { bookingId.set(v); }
    public IntegerProperty bookingIdProperty(){ return bookingId; }

    public int getCustomerId()               { return customerId.get(); }
    public void setCustomerId(int v)         { customerId.set(v); }
    public IntegerProperty customerIdProperty(){ return customerId; }

    public int getRoomNumber()               { return roomNumber.get(); }
    public void setRoomNumber(int v)         { roomNumber.set(v); }
    public IntegerProperty roomNumberProperty(){ return roomNumber; }

    public int getNumDays()                  { return numDays.get(); }
    public void setNumDays(int v)            { numDays.set(v); }
    public IntegerProperty numDaysProperty() { return numDays; }

    public double getRoomCharges()           { return roomCharges.get(); }
    public void setRoomCharges(double v)     { roomCharges.set(v); }
    public DoubleProperty roomChargesProperty(){ return roomCharges; }

    public double getServiceTax()            { return serviceTax.get(); }
    public void setServiceTax(double v)      { serviceTax.set(v); }
    public DoubleProperty serviceTaxProperty(){ return serviceTax; }

    public double getTotalAmount()           { return totalAmount.get(); }
    public void setTotalAmount(double v)     { totalAmount.set(v); }
    public DoubleProperty totalAmountProperty(){ return totalAmount; }

    public String getBillDate()              { return billDate.get(); }
    public void setBillDate(String v)        { billDate.set(v); }
    public StringProperty billDateProperty() { return billDate; }

    public String getCustomerName()          { return customerName.get(); }
    public void setCustomerName(String v)    { customerName.set(v); }
    public StringProperty customerNameProperty(){ return customerName; }
}
