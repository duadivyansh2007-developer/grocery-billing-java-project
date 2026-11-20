package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.UserAccount;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** New user registration (stored only in memory for this GUI prototype). */
public class RegisterFrame extends JFrame {
    private final LoginFrame loginFrame;
    private final JTextField nameField = new JTextField(18);
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JPasswordField confirmField = new JPasswordField(18);

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle(UiTheme.PROJECT + " - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 480);
        setLocationRelativeTo(loginFrame);

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.headerBar("Register"), BorderLayout.NORTH);
        root.add(buildForm(), BorderLayout.CENTER);
        setContentPane(root);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loginFrame.setVisible(true);
            }
        });
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Create Account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(UiTheme.PRIMARY_DARK);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(heading, gbc);

        addRow(form, gbc, 1, "Full Name", nameField);
        addRow(form, gbc, 2, "Username", usernameField);
        addRow(form, gbc, 3, "Password", passwordField);
        addRow(form, gbc, 4, "Confirm Password", confirmField);

        JButton save = UiTheme.primaryButton("Register");
        JButton back = UiTheme.infoButton("Back to Login");
        save.addActionListener(e -> register());
        back.addActionListener(e -> {
            dispose();
            loginFrame.setVisible(true);
        });

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(save);
        buttons.add(back);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);
        return form;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridwidth = 1;
        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(UiTheme.fieldLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private void register() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Password and confirm password do not match.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (DummyStore.get().findByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DummyStore.get().getUsers().add(new UserAccount(
            DummyStore.get().nextUserId(), name, username, password, "Cashier"));
        if (!DummyStore.get().persistChanges(this)) return;
        JOptionPane.showMessageDialog(this, "Registration successful. Please login.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        loginFrame.setVisible(true);
    }
}
