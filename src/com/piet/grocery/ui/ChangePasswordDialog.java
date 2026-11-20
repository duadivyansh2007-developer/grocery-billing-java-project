package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.UserAccount;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/** Change password after verifying the current password. */
public class ChangePasswordDialog extends JDialog {
    private final UserAccount user;
    private final JPasswordField oldField = new JPasswordField();
    private final JPasswordField newField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();

    public ChangePasswordDialog(JFrame owner, UserAccount user) {
        super(owner, "Change Password", true);
        this.user = user;
        setSize(420, 260);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.headerBar("Change Password"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 8, 16));
        form.setBackground(UiTheme.BACKGROUND);
        form.add(new JLabel("Current Password"));
        form.add(oldField);
        form.add(new JLabel("New Password"));
        form.add(newField);
        form.add(new JLabel("Confirm New Password"));
        form.add(confirmField);

        JButton save = UiTheme.primaryButton("Save");
        JButton cancel = UiTheme.infoButton("Cancel");
        save.addActionListener(e -> save());
        cancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.setBackground(UiTheme.BACKGROUND);
        buttons.add(save);
        buttons.add(cancel);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void save() {
        String oldPwd = new String(oldField.getPassword());
        String newPwd = new String(newField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!oldPwd.equals(user.getPassword())) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Validation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPwd.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New password and confirm password do not match.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (newPwd.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        user.setPassword(newPwd);
        if (!DummyStore.get().persistChanges(this)) return;
        JOptionPane.showMessageDialog(this, "Password changed and saved to the database.");
        dispose();
    }
}
