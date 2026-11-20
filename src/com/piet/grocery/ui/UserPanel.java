package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.UserAccount;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/** Admin-only user management form. */
public class UserPanel extends JPanel {
    private final JTextField nameField = new JTextField();
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Cashier"});

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Full Name", "Username", "Role"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public UserPanel() {
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
        reload();
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createTitledBorder("User Management (Admin)"));

        JPanel fields = new JPanel(new GridLayout(2, 4, 8, 6));
        fields.setOpaque(false);
        fields.add(UiTheme.fieldLabel("Full Name"));
        fields.add(nameField);
        fields.add(UiTheme.fieldLabel("Username"));
        fields.add(usernameField);
        fields.add(UiTheme.fieldLabel("Password"));
        fields.add(passwordField);
        fields.add(UiTheme.fieldLabel("Role"));
        fields.add(roleBox);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton add = UiTheme.primaryButton("Add");
        JButton update = UiTheme.warningButton("Update");
        JButton delete = UiTheme.dangerButton("Delete");
        JButton clear = UiTheme.infoButton("Clear");
        add.addActionListener(e -> addUser());
        update.addActionListener(e -> updateUser());
        delete.addActionListener(e -> deleteUser());
        clear.addActionListener(e -> clearForm());
        actions.add(add);
        actions.add(update);
        actions.add(delete);
        actions.add(clear);

        wrap.add(fields, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private boolean valid(boolean needPassword) {
        if (nameField.getText().trim().isEmpty() || usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and username are required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (needPassword && new String(passwordField.getPassword()).length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addUser() {
        if (!valid(true)) {
            return;
        }
        String username = usernameField.getText().trim();
        if (DummyStore.get().findByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
            return;
        }
        DummyStore.get().getUsers().add(new UserAccount(
                DummyStore.get().nextUserId(),
                nameField.getText().trim(),
                username,
                new String(passwordField.getPassword()),
                String.valueOf(roleBox.getSelectedItem())));
            if (!DummyStore.get().persistChanges(this)) return;
        reload();
        clearForm();
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to update.");
            return;
        }
        if (!valid(false)) {
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        for (UserAccount u : DummyStore.get().getUsers()) {
            if (u.getId() == id) {
                u.setFullName(nameField.getText().trim());
                u.setUsername(usernameField.getText().trim());
                u.setRole(String.valueOf(roleBox.getSelectedItem()));
                String pwd = new String(passwordField.getPassword());
                if (!pwd.isEmpty()) {
                    if (pwd.length() < 6) {
                        JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
                        return;
                    }
                    u.setPassword(pwd);
                }
            }
        }
        if (!DummyStore.get().persistChanges(this)) return;
        reload();
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        if (id == 1) {
            JOptionPane.showMessageDialog(this, "The default admin account cannot be deleted.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        DummyStore.get().getUsers().removeIf(u -> u.getId() == id);
        if (!DummyStore.get().persistChanges(this)) return;
        reload();
        clearForm();
    }

    private void fillFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        usernameField.setText(tableModel.getValueAt(row, 2).toString());
        roleBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        passwordField.setText("");
    }

    private void clearForm() {
        nameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        roleBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void reload() {
        tableModel.setRowCount(0);
        for (UserAccount u : DummyStore.get().getUsers()) {
            tableModel.addRow(new Object[]{u.getId(), u.getFullName(), u.getUsername(), u.getRole()});
        }
    }
}
