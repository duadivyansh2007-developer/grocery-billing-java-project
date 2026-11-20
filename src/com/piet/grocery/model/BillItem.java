package com.piet.grocery.model;

/** One line on a customer bill (cart row). */
public class BillItem {
    private final Product product;
    private int quantity;

    public BillItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLineSubtotal() {
        return product.getUnitPrice() * quantity;
    }

    public double getLineGst() {
        return getLineSubtotal() * product.getGstPercent() / 100.0;
    }

    public double getLineTotal() {
        return getLineSubtotal() + getLineGst();
    }
}
