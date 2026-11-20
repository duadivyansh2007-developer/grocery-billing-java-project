package com.piet.grocery.model;

/**
 * One grocery item shown in Inventory and Billing.
 * Product fields are stored in the MySQL products table by the data layer.
 */
public class Product {
    private int id;
    private String name;
    private String category;
    private int quantity;
    private double unitPrice;
    private double gstPercent;
    private String unit;

    public Product(int id, String name, String category, int quantity,
                   double unitPrice, double gstPercent, String unit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.gstPercent = gstPercent;
        this.unit = unit;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getGstPercent() {
        return gstPercent;
    }

    public void setGstPercent(double gstPercent) {
        this.gstPercent = gstPercent;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
