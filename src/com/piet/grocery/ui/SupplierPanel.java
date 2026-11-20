package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

/** Supplier master: Add / Update / Delete / Search + JTable. */
public class SupplierPanel extends JPanel {
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField cityField = new JTextField();
    private final JTextField itemsField = new JTextField();
    private final JTextField searchField = new JTextField(16);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Phone", "City", "Items Supplied"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public SupplierPanel() {
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
        reload(null);
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createTitledBorder("Supplier Form"));

        JPanel fields = new JPanel(new GridLayout(2, 4, 8, 6));
        fields.setOpaque(false);
        fields.add(UiTheme.fieldLabel("Name"));
        fields.add(nameField);
        fields.add(UiTheme.fieldLabel("Phone"));
        fields.add(phoneField);
        fields.add(UiTheme.fieldLabel("City"));
        fields.add(cityField);
        fields.add(UiTheme.fieldLabel("Items Supplied"));
        fields.add(itemsField);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton add = UiTheme.primaryButton("Add");
        JButton update = UiTheme.warningButton("Update");
        JButton delete = UiTheme.dangerButton("Delete");
        JButton clear = UiTheme.infoButton("Clear");
        JButton search = UiTheme.primaryButton("Search");
        add.addActionListener(e -> addSupplier());
        update.addActionListener(e -> updateSupplier());
        delete.addActionListener(e -> deleteSupplier());
        clear.addActionListener(e -> clearForm());
        search.addActionListener(e -> reload(searchField.getText().trim()));
        actions.add(add);
        actions.add(update);
        actions.add(delete);
        actions.add(clear);
        actions.add(UiTheme.fieldLabel("Search"));
        actions.add(searchField);
        actions.add(search);

        wrap.add(fields, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private boolean valid() {
        if (nameField.getText().trim().isEmpty() || cityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and city are required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addSupplier() {
        if (!valid()) {
            return;
        }
        DummyStore.get().getSuppliers().add(new Supplier(
                DummyStore.get().nextSupplierId(),
                nameField.getText().trim(),
                phoneField.getText().trim(),
                cityField.getText().trim(),
                itemsField.getText().trim()));
            if (!DummyStore.get().persistChanges(this)) return;
        reload(null);
        clearForm();
    }

    private void updateSupplier() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a supplier to update.");
            return;
        }
        if (!valid()) {
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        for (Supplier s : DummyStore.get().getSuppliers()) {
            if (s.getId() == id) {
                s.setName(nameField.getText().trim());
                s.setPhone(phoneField.getText().trim());
                s.setCity(cityField.getText().trim());
                s.setItemsSupplied(itemsField.getText().trim());
            }
        }
        if (!DummyStore.get().persistChanges(this)) return;
        reload(null);
    }

    private void deleteSupplier() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a supplier to delete.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this supplier?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        DummyStore.get().getSuppliers().removeIf(s -> s.getId() == id);
        if (!DummyStore.get().persistChanges(this)) return;
        reload(null);
        clearForm();
    }

    private void fillFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        phoneField.setText(tableModel.getValueAt(row, 2).toString());
        cityField.setText(tableModel.getValueAt(row, 3).toString());
        itemsField.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        cityField.setText("");
        itemsField.setText("");
        table.clearSelection();
    }

    private void reload(String filter) {
        tableModel.setRowCount(0);
        String q = filter == null ? "" : filter.toLowerCase();
        for (Supplier s : DummyStore.get().getSuppliers()) {
            if (!q.isEmpty() && !s.getName().toLowerCase().contains(q)
                    && !s.getCity().toLowerCase().contains(q)) {
                continue;
            }
            tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getPhone(), s.getCity(), s.getItemsSupplied()
            });
        }
    }
}
