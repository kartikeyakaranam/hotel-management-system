package com.hotel.model;

import javafx.beans.property.*;

public class Customer {

    private final IntegerProperty customerId = new SimpleIntegerProperty();
    private final StringProperty  name       = new SimpleStringProperty();
    private final StringProperty  contact    = new SimpleStringProperty();
    private final StringProperty  email      = new SimpleStringProperty();
    private final StringProperty  address    = new SimpleStringProperty();

    public Customer() {}

    public Customer(int customerId, String name, String contact, String email, String address) {
        this.customerId.set(customerId);
        this.name.set(name);
        this.contact.set(contact);
        this.email.set(email == null ? "" : email);
        this.address.set(address == null ? "" : address);
    }

    public int getCustomerId()               { return customerId.get(); }
    public void setCustomerId(int v)         { customerId.set(v); }
    public IntegerProperty customerIdProperty() { return customerId; }

    public String getName()                  { return name.get(); }
    public void setName(String v)            { name.set(v); }
    public StringProperty nameProperty()     { return name; }

    public String getContact()               { return contact.get(); }
    public void setContact(String v)         { contact.set(v); }
    public StringProperty contactProperty()  { return contact; }

    public String getEmail()                 { return email.get(); }
    public void setEmail(String v)           { email.set(v); }
    public StringProperty emailProperty()    { return email; }

    public String getAddress()               { return address.get(); }
    public void setAddress(String v)         { address.set(v); }
    public StringProperty addressProperty()  { return address; }

    @Override
    public String toString() { return "[" + getCustomerId() + "] " + getName(); }
}
