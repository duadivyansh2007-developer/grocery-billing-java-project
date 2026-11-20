package com.piet.grocery.model;

/** Supplier who provides stock to the grocery store. */
public class Supplier {
    private int id;
    private String name;
    private String phone;
    private String city;
    private String itemsSupplied;

    public Supplier(int id, String name, String phone, String city, String itemsSupplied) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.itemsSupplied = itemsSupplied;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getItemsSupplied() {
        return itemsSupplied;
    }

    public void setItemsSupplied(String itemsSupplied) {
        this.itemsSupplied = itemsSupplied;
    }
}
