package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.Product;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/** Product inventory: Add / Update / Delete / Search + JTable. */
public class InventoryPanel extends JPanel {
    private final JTextField nameField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(
            new String[]{"Grains", "Dairy", "Spices", "Oils", "Snacks", "Beverages", "Household"});
    private final JTextField qtyField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField gstField = new JTextField("5");
    private final JTextField unitField = new JTextField("pkt");
    private final JTextField searchField = new JTextField(16);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Category", "Qty", "Unit Price", "GST %", "Unit"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public InventoryPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UiTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        UiTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFromSelection();
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
        reloadTable(null);
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel fields = new JPanel(new GridLayout(2, 6, 8, 6));
        fields.setOpaque(false);
        fields.add(UiTheme.fieldLabel("Product Name"));
        fields.add(nameField);
        fields.add(UiTheme.fieldLabel("Category"));
        fields.add(categoryBox);
        fields.add(UiTheme.fieldLabel("Quantity"));
        fields.add(qtyField);
        fields.add(UiTheme.fieldLabel("Unit Price (Rs)"));
        fields.add(priceField);
        fields.add(UiTheme.fieldLabel("GST %"));
        fields.add(gstField);
        fields.add(UiTheme.fieldLabel("Unit"));
        fields.add(unitField);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton add = UiTheme.primaryButton("Add");
        JButton update = UiTheme.warningButton("Update");
        JButton delete = UiTheme.dangerButton("Delete");
        JButton clear = UiTheme.infoButton("Clear");
        add.addActionListener(e -> addProduct());
        update.addActionListener(e -> updateProduct());
        delete.addActionListener(e -> deleteProduct());
        clear.addActionListener(e -> clearForm());
        actions.add(add);
        actions.add(update);
        actions.add(delete);
        actions.add(clear);
        actions.add(new JLabel("Search"));
        actions.add(searchField);
        JButton search = UiTheme.primaryButton("Search");
        search.addActionListener(e -> reloadTable(searchField.getText().trim()));
        actions.add(search);

        wrap.add(fields, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        wrap.setBorder(BorderFactory.createTitledBorder("Inventory Form"));
        return wrap;
    }

    private void addProduct() {
        Product product = readForm(-1);
        if (product == null) {
            return;
        }
        product = new Product(DummyStore.get().nextProductId(), product.getName(), product.getCategory(),
                product.getQuantity(), product.getUnitPrice(), product.getGstPercent(), product.getUnit());
        DummyStore.get().getProducts().add(product);
        if (!DummyStore.get().persistChanges(this)) return;
        reloadTable(null);
        clearForm();
        JOptionPane.showMessageDialog(this, "Product added and saved to the database.");
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product row to update.");
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        Product existing = DummyStore.get().findProduct(id);
        Product edited = readForm(id);
        if (existing == null || edited == null) {
            return;
        }
        existing.setName(edited.getName());
        existing.setCategory(edited.getCategory());
        existing.setQuantity(edited.getQuantity());
        existing.setUnitPrice(edited.getUnitPrice());
        existing.setGstPercent(edited.getGstPercent());
        existing.setUnit(edited.getUnit());
        if (!DummyStore.get().persistChanges(this)) return;
        reloadTable(null);
        JOptionPane.showMessageDialog(this, "Product updated.");
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product row to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this product permanently from the list?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        DummyStore.get().getProducts().removeIf(p -> p.getId() == id);
        if (!DummyStore.get().persistChanges(this)) return;
        reloadTable(null);
        clearForm();
    }

    private Product readForm(int id) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            double gst = Double.parseDouble(gstField.getText().trim());
            if (qty < 0 || price < 0 || gst < 0) {
                throw new NumberFormatException("negative");
            }
            return new Product(id, name, String.valueOf(categoryBox.getSelectedItem()),
                    qty, price, gst, unitField.getText().trim().isEmpty() ? "pkt" : unitField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity, price and GST must be valid non-negative numbers.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void fillFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        categoryBox.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        qtyField.setText(tableModel.getValueAt(row, 3).toString());
        priceField.setText(tableModel.getValueAt(row, 4).toString());
        gstField.setText(tableModel.getValueAt(row, 5).toString());
        unitField.setText(tableModel.getValueAt(row, 6).toString());
    }

    private void clearForm() {
        nameField.setText("");
        qtyField.setText("");
        priceField.setText("");
        gstField.setText("5");
        unitField.setText("pkt");
        table.clearSelection();
    }

    private void reloadTable(String filter) {
        tableModel.setRowCount(0);
        String q = filter == null ? "" : filter.toLowerCase();
        for (Product p : DummyStore.get().getProducts()) {
            if (!q.isEmpty() && !p.getName().toLowerCase().contains(q)
                    && !p.getCategory().toLowerCase().contains(q)) {
                continue;
            }
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(),
                    String.format("%.2f", p.getUnitPrice()), p.getGstPercent(), p.getUnit()
            });
        }
    }
}
