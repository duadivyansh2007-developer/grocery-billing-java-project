package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;
import com.piet.grocery.model.UserAccount;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** First screen: Admin / Cashier login. */
public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JRadioButton adminRadio = new JRadioButton("Admin", true);
    private final JRadioButton cashierRadio = new JRadioButton("Cashier");

    public LoginFrame() {
        setTitle(UiTheme.PROJECT + " - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiTheme.BACKGROUND);
        root.add(UiTheme.headerBar("Login"), BorderLayout.NORTH);
        root.add(buildLeftBanner(), BorderLayout.WEST);
        root.add(buildForm(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildLeftBanner() {
        JPanel banner = new JPanel(new GridBagLayout());
        banner.setPreferredSize(new Dimension(280, 0));
        banner.setBackground(UiTheme.PRIMARY_DARK);
        banner.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel icon = new JLabel("GROCERY");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        icon.setForeground(Color.WHITE);

        JLabel line1 = new JLabel("Welcome");
        line1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        line1.setForeground(Color.WHITE);

        JLabel line2 = new JLabel("<html><div style='text-align:center;width:220px'>"
                + "Sign in to manage inventory, bills,<br>customers and suppliers.</div></html>");
        line2.setForeground(new Color(211, 229, 228));
        line2.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridy = 0;
        banner.add(icon, gbc);
        gbc.gridy = 1;
        banner.add(line1, gbc);
        gbc.gridy = 2;
        banner.add(line2, gbc);
        return banner;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Sign in to continue");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(UiTheme.PRIMARY_DARK);

        usernameField.setPreferredSize(new Dimension(220, 36));
        passwordField.setPreferredSize(new Dimension(220, 36));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        ButtonGroup roles = new ButtonGroup();
        roles.add(adminRadio);
        roles.add(cashierRadio);
        styleRadio(adminRadio);
        styleRadio(cashierRadio);
        JPanel roleRow = new JPanel();
        roleRow.setOpaque(false);
        roleRow.add(adminRadio);
        roleRow.add(cashierRadio);

        JButton loginBtn = UiTheme.primaryButton("Login");
        JButton registerBtn = UiTheme.infoButton("Register");
        getRootPane().setDefaultButton(loginBtn);
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            new RegisterFrame(this).setVisible(true);
            setVisible(false);
        });

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(loginBtn);
        buttons.add(registerBtn);

        JLabel hint = new JLabel("Demo access: admin / admin123   or   cashier / cashier123");
        hint.setForeground(UiTheme.MUTED);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        int row = 0;
        gbc.gridy = row++;
        form.add(heading, gbc);
        gbc.gridy = row++;
        form.add(roleRow, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(UiTheme.fieldLabel("Username"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(UiTheme.fieldLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy = ++row;
        form.add(buttons, gbc);
        gbc.gridy = ++row;
        form.add(hint, gbc);
        return form;
    }

    private void styleRadio(JRadioButton radio) {
        radio.setOpaque(false);
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = adminRadio.isSelected() ? "Admin" : "Cashier";

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserAccount user = DummyStore.get().findUser(username, password, role);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid credentials or role.\nUse the demo logins shown on this screen.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new DashboardFrame(user, this).setVisible(true);
        setVisible(false);
        usernameField.setText("");
        passwordField.setText("");
    }
}
