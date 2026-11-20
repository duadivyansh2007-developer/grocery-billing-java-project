package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.BillItem;
import com.piet.grocery.model.Customer;
import com.piet.grocery.model.Product;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Point-of-sale billing screen with cart table and saved receipt preview. */
public class BillingPanel extends JPanel {
    private final JComboBox<Customer> customerBox = new JComboBox<>();
    private final JComboBox<Product> productBox = new JComboBox<>();
    private final JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
    private final JTextField discountField = new JTextField("0");
    private final JLabel subtotalLabel = new JLabel("0.00");
    private final JLabel gstLabel = new JLabel("0.00");
    private final JLabel discountLabel = new JLabel("0.00");
    private final JLabel grandLabel = new JLabel("0.00");

    private final List<BillItem> cart = new ArrayList<>();
    private final DefaultTableModel cartModel = new DefaultTableModel(
            new String[]{"Product", "Qty", "Rate", "GST %", "Line Total"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable cartTable = new JTable(cartModel);

    public BillingPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UiTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTop(), BorderLayout.NORTH);
        UiTheme.styleTable(cartTable);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);
        add(buildTotals(), BorderLayout.SOUTH);
        reloadCombos();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new GridLayout(2, 1, 6, 6));
        top.setOpaque(false);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setOpaque(false);
        row1.setBorder(BorderFactory.createTitledBorder("Customer & Product"));
        row1.add(new JLabel("Customer"));
        customerBox.setPreferredSize(new java.awt.Dimension(220, 26));
        productBox.setPreferredSize(new java.awt.Dimension(260, 26));
        row1.add(customerBox);
        row1.add(new JLabel("Product"));
        row1.add(productBox);
        row1.add(new JLabel("Qty"));
        qtySpinner.setPreferredSize(new java.awt.Dimension(70, 26));
        row1.add(qtySpinner);
        JButton add = UiTheme.primaryButton("Add to Cart");
        add.addActionListener(e -> addToCart());
        row1.add(add);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setOpaque(false);
        JButton remove = UiTheme.dangerButton("Remove Line");
        JButton clear = UiTheme.infoButton("Clear Cart");
        JButton refresh = UiTheme.warningButton("Refresh Lists");
        remove.addActionListener(e -> removeLine());
        clear.addActionListener(e -> clearCart());
        refresh.addActionListener(e -> reloadCombos());
        row2.add(remove);
        row2.add(clear);
        row2.add(refresh);
        row2.add(new JLabel("Discount (Rs)"));
        discountField.setColumns(8);
        discountField.addActionListener(e -> refreshTotals());
        row2.add(discountField);

        top.add(row1);
        top.add(row2);
        return top;
    }

    private JPanel buildTotals() {
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);

        JPanel totals = new JPanel(new GridLayout(2, 4, 8, 4));
        totals.setBorder(BorderFactory.createTitledBorder("Bill Summary"));
        totals.setOpaque(false);
        totals.add(label("Subtotal (Rs)"));
        totals.add(subtotalLabel);
        totals.add(label("GST (Rs)"));
        totals.add(gstLabel);
        totals.add(label("Discount (Rs)"));
        totals.add(discountLabel);
        totals.add(label("Grand Total (Rs)"));
        grandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        grandLabel.setForeground(UiTheme.PRIMARY_DARK);
        totals.add(grandLabel);

        JButton bill = UiTheme.primaryButton("Generate Bill");
        bill.setPreferredSize(new java.awt.Dimension(160, 40));
        bill.addActionListener(e -> generateBill());
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.setOpaque(false);
        btn.add(bill);

        south.add(totals, BorderLayout.CENTER);
        south.add(btn, BorderLayout.EAST);
        return south;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void reloadCombos() {
        customerBox.removeAllItems();
        for (Customer c : DummyStore.get().getCustomers()) {
            customerBox.addItem(c);
        }
        productBox.removeAllItems();
        for (Product p : DummyStore.get().getProducts()) {
            productBox.addItem(p);
        }
    }

    private void addToCart() {
        Product product = (Product) productBox.getSelectedItem();
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Select a product.");
            return;
        }
        int qty = (Integer) qtySpinner.getValue();
        if (qty > product.getQuantity()) {
            JOptionPane.showMessageDialog(this,
                    "Only " + product.getQuantity() + " units available in dummy stock.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (BillItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + qty);
                refreshCartTable();
                return;
            }
        }
        cart.add(new BillItem(product, qty));
        refreshCartTable();
    }

    private void removeLine() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a cart line to remove.");
            return;
        }
        cart.remove(row);
        refreshCartTable();
    }

    private void clearCart() {
        cart.clear();
        discountField.setText("0");
        refreshCartTable();
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (BillItem item : cart) {
            cartModel.addRow(new Object[]{
                    item.getProduct().getName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getProduct().getUnitPrice()),
                    item.getProduct().getGstPercent(),
                    String.format("%.2f", item.getLineTotal())
            });
        }
        refreshTotals();
    }

    private double[] computeTotals() {
        double sub = 0;
        double gst = 0;
        for (BillItem item : cart) {
            sub += item.getLineSubtotal();
            gst += item.getLineGst();
        }
        double discount;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
        } catch (NumberFormatException ex) {
            discount = 0;
        }
        if (discount < 0) {
            discount = 0;
        }
        double grand = Math.max(0, sub + gst - discount);
        return new double[]{sub, gst, discount, grand};
    }

    private void refreshTotals() {
        double[] t = computeTotals();
        subtotalLabel.setText(String.format("%.2f", t[0]));
        gstLabel.setText(String.format("%.2f", t[1]));
        discountLabel.setText(String.format("%.2f", t[2]));
        grandLabel.setText(String.format("%.2f", t[3]));
    }

    private void generateBill() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add products first.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Customer customer = (Customer) customerBox.getSelectedItem();
        double[] t = computeTotals();
        try {
            DummyStore.get().recordBill(customer, t, cart);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Could not save bill. " + exception.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(UiTheme.PROJECT).append("\n");
        sb.append("Student: ").append(UiTheme.STUDENT).append("  ").append(UiTheme.ROLL).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Customer: ").append(customer == null ? "-" : customer.getName()).append("\n");
        sb.append("----------------------------------------\n");
        for (BillItem item : cart) {
            sb.append(String.format("%s  x%d  = Rs %.2f%n",
                    item.getProduct().getName(), item.getQuantity(), item.getLineTotal()));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal: Rs %.2f%n", t[0]));
        sb.append(String.format("GST:      Rs %.2f%n", t[1]));
        sb.append(String.format("Discount: Rs %.2f%n", t[2]));
        sb.append(String.format("TOTAL:    Rs %.2f%n", t[3]));
        sb.append("----------------------------------------\n");
        sb.append("Thank you! Bill saved to the database.");

        JTextArea area = new JTextArea(sb.toString(), 18, 40);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Generated Bill",
                JOptionPane.INFORMATION_MESSAGE);
        clearCart();
    }
}
