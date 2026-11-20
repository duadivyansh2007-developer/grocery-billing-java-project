package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.Customer;

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

/** Customer master: Add / Update / Delete / Search + JTable. */
public class CustomerPanel extends JPanel {
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField searchField = new JTextField(16);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Phone", "Address"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public CustomerPanel() {
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
        wrap.setBorder(BorderFactory.createTitledBorder("Customer Form"));

        JPanel fields = new JPanel(new GridLayout(2, 4, 8, 6));
        fields.setOpaque(false);
        fields.add(UiTheme.fieldLabel("Name"));
        fields.add(nameField);
        fields.add(UiTheme.fieldLabel("Phone"));
        fields.add(phoneField);
        fields.add(UiTheme.fieldLabel("Address"));
        fields.add(addressField);
        fields.add(UiTheme.fieldLabel("Search"));
        fields.add(searchField);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton add = UiTheme.primaryButton("Add");
        JButton update = UiTheme.warningButton("Update");
        JButton delete = UiTheme.dangerButton("Delete");
        JButton clear = UiTheme.infoButton("Clear");
        JButton search = UiTheme.primaryButton("Search");
        add.addActionListener(e -> addCustomer());
        update.addActionListener(e -> updateCustomer());
        delete.addActionListener(e -> deleteCustomer());
        clear.addActionListener(e -> clearForm());
        search.addActionListener(e -> reload(searchField.getText().trim()));
        actions.add(add);
        actions.add(update);
        actions.add(delete);
        actions.add(clear);
        actions.add(search);

        wrap.add(fields, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private boolean valid() {
        if (nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and phone are required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!phoneField.getText().trim().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone must be 10 digits.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addCustomer() {
        if (!valid()) {
            return;
        }
        DummyStore.get().getCustomers().add(new Customer(
                DummyStore.get().nextCustomerId(),
                nameField.getText().trim(),
                phoneField.getText().trim(),
                addressField.getText().trim()));
            if (!DummyStore.get().persistChanges(this)) return;
        reload(null);
        clearForm();
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to update.");
            return;
        }
        if (!valid()) {
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        for (Customer c : DummyStore.get().getCustomers()) {
            if (c.getId() == id) {
                c.setName(nameField.getText().trim());
                c.setPhone(phoneField.getText().trim());
                c.setAddress(addressField.getText().trim());
            }
        }
        if (!DummyStore.get().persistChanges(this)) return;
        reload(null);
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        DummyStore.get().getCustomers().removeIf(c -> c.getId() == id);
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
        addressField.setText(tableModel.getValueAt(row, 3).toString());
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        table.clearSelection();
    }

    private void reload(String filter) {
        tableModel.setRowCount(0);
        String q = filter == null ? "" : filter.toLowerCase();
        for (Customer c : DummyStore.get().getCustomers()) {
            if (!q.isEmpty() && !c.getName().toLowerCase().contains(q)
                    && !c.getPhone().contains(q)) {
                continue;
            }
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getAddress()});
        }
    }
}
