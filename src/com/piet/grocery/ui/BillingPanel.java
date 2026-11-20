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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** POS-style billing screen with cart and bill summary. */
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
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable cartTable = new JTable(cartModel);

    public BillingPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UiTheme.BACKGROUND);
        UiTheme.styleTable(cartTable);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(UiTheme.CARD);
        left.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel cartTitle = new JLabel("Current sale");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cartTitle.setForeground(UiTheme.TEXT);
        left.add(cartTitle, BorderLayout.NORTH);
        left.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        left.add(buildCartActions(), BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.add(buildTop(), BorderLayout.NORTH);
        right.add(buildQuickProducts(), BorderLayout.CENTER);
        right.add(buildTotals(), BorderLayout.SOUTH);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
        reloadCombos();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new GridLayout(2, 3, 10, 8));
        top.setBackground(UiTheme.CARD);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        top.add(UiTheme.fieldLabel("Customer"));
        top.add(UiTheme.fieldLabel("Product"));
        top.add(UiTheme.fieldLabel("Quantity"));
        customerBox.setPreferredSize(new Dimension(220, 36));
        top.add(customerBox);
        productBox.setPreferredSize(new Dimension(260, 36));
        top.add(productBox);

        JPanel qtyRow = new JPanel(new BorderLayout(8, 0));
        qtyRow.setOpaque(false);
        qtySpinner.setPreferredSize(new Dimension(72, 36));
        JButton add = UiTheme.primaryButton("Add to sale");
        add.setPreferredSize(new Dimension(115, 36));
        add.addActionListener(e -> addToCart());
        qtyRow.add(qtySpinner, BorderLayout.WEST);
        qtyRow.add(add, BorderLayout.EAST);
        top.add(qtyRow);
        return top;
    }

    private JPanel buildQuickProducts() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);
        JLabel title = new JLabel("Products");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(UiTheme.TEXT);
        panel.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 8, 8));
        grid.setOpaque(false);
        panel.add(grid, BorderLayout.CENTER);

        productBox.addActionListener(e -> refreshQuickProducts(grid));
        refreshQuickProducts(grid);
        return panel;
    }

    private void refreshQuickProducts(JPanel grid) {
        grid.removeAll();
        int count = 0;
        for (Product product : DummyStore.get().getProducts()) {
            if (count++ >= 6) break;
            JPanel card = new JPanel(new BorderLayout(6, 4));
            card.setBackground(UiTheme.CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UiTheme.BORDER),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            JLabel name = new JLabel("<html><b>" + product.getName() + "</b></html>");
            name.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            name.setForeground(UiTheme.TEXT);
            JLabel price = new JLabel("Rs " + String.format("%.2f", product.getUnitPrice()));
            price.setFont(new Font("Segoe UI", Font.BOLD, 12));
            price.setForeground(UiTheme.PRIMARY_DARK);
            JButton add = UiTheme.primaryButton("+");
            add.setPreferredSize(new Dimension(34, 34));
            add.addActionListener(e -> {
                productBox.setSelectedItem(product);
                addToCart();
            });
            card.add(name, BorderLayout.NORTH);
            card.add(price, BorderLayout.WEST);
            card.add(add, BorderLayout.EAST);
            grid.add(card);
        }
        grid.revalidate();
        grid.repaint();
    }

    private JPanel buildCartActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setOpaque(false);
        JButton remove = UiTheme.dangerButton("Remove line");
        JButton clear = UiTheme.infoButton("Clear sale");
        JButton refresh = UiTheme.primaryButton("Refresh");
        remove.addActionListener(e -> removeLine());
        clear.addActionListener(e -> clearCart());
        refresh.addActionListener(e -> { reloadCombos(); refreshCartTable(); });
        actions.add(remove); actions.add(clear); actions.add(refresh);
        return actions;
    }

    private JPanel buildTotals() {
        JPanel south = new JPanel(new BorderLayout(12, 8));
        south.setBackground(UiTheme.CARD);
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JPanel totals = new JPanel(new GridLayout(4, 2, 10, 6));
        totals.setOpaque(false);
        totals.add(label("Subtotal")); totals.add(subtotalLabel);
        totals.add(label("GST")); totals.add(gstLabel);
        totals.add(label("Discount (Rs)")); totals.add(discountLabel);
        totals.add(label("Total")); totals.add(grandLabel);

        JPanel discount = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discount.setOpaque(false);
        discountField.setPreferredSize(new Dimension(90, 34));
        discountField.addActionListener(e -> refreshTotals());
        discount.add(new JLabel("Discount"));
        discount.add(discountField);

        JButton bill = UiTheme.primaryButton("Generate bill");
        bill.setPreferredSize(new Dimension(160, 42));
        bill.addActionListener(e -> generateBill());

        JPanel actions = new JPanel(new BorderLayout(8, 8));
        actions.setOpaque(false);
        actions.add(discount, BorderLayout.NORTH);
        actions.add(bill, BorderLayout.SOUTH);

        south.add(totals, BorderLayout.CENTER);
        south.add(actions, BorderLayout.EAST);
        return south;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(UiTheme.MUTED);
        return l;
    }

    private void reloadCombos() {
        customerBox.removeAllItems();
        for (Customer c : DummyStore.get().getCustomers()) customerBox.addItem(c);
        productBox.removeAllItems();
        for (Product p : DummyStore.get().getProducts()) productBox.addItem(p);
    }

    private void addToCart() {
        Product product = (Product) productBox.getSelectedItem();
        if (product == null) { JOptionPane.showMessageDialog(this, "Select a product."); return; }
        int qty = (Integer) qtySpinner.getValue();
        if (qty > product.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Only " + product.getQuantity() + " units available.",
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
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a cart line to remove."); return; }
        cart.remove(row); refreshCartTable();
    }

    private void clearCart() {
        cart.clear(); discountField.setText("0"); refreshCartTable();
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (BillItem item : cart) {
            cartModel.addRow(new Object[]{item.getProduct().getName(), item.getQuantity(),
                    String.format("%.2f", item.getProduct().getUnitPrice()), item.getProduct().getGstPercent(),
                    String.format("%.2f", item.getLineTotal())});
        }
        refreshTotals();
    }

    private double[] computeTotals() {
        double sub = 0, gst = 0;
        for (BillItem item : cart) { sub += item.getLineSubtotal(); gst += item.getLineGst(); }
        double discount;
        try { discount = Double.parseDouble(discountField.getText().trim()); }
        catch (NumberFormatException ex) { discount = 0; }
        if (discount < 0) discount = 0;
        if (discount > sub + gst) discount = sub + gst;
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
            JOptionPane.showMessageDialog(this, "Cart is empty. Add products first.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        Customer customer = (Customer) customerBox.getSelectedItem();
        double[] t = computeTotals();
        try { DummyStore.get().recordBill(customer, t, cart); }
        catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Could not save bill. " + exception.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE); return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(UiTheme.PROJECT).append("\n");
        sb.append("Student: ").append(UiTheme.STUDENT).append("  ").append(UiTheme.ROLL).append("\n");
        sb.append("----------------------------------------\nCustomer: ").append(customer == null ? "-" : customer.getName()).append("\n----------------------------------------\n");
        for (BillItem item : cart) sb.append(String.format("%s  x%d  = Rs %.2f%n", item.getProduct().getName(), item.getQuantity(), item.getLineTotal()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal: Rs %.2f%nGST: Rs %.2f%nDiscount: Rs %.2f%nTOTAL: Rs %.2f%n", t[0], t[1], t[2], t[3]));
        sb.append("----------------------------------------\nThank you! Bill saved to the database.");
        JTextArea area = new JTextArea(sb.toString(), 18, 40);
        area.setEditable(false); area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Generated Bill", JOptionPane.INFORMATION_MESSAGE);
        clearCart();
    }
}
