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

/** Premium Admin / Cashier login experience. */
public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JRadioButton adminRadio = new JRadioButton("Admin", true);
    private final JRadioButton cashierRadio = new JRadioButton("Cashier");

    public LoginFrame() {
        setTitle(UiTheme.PROJECT + " - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(buildLeftBanner(), BorderLayout.WEST);
        root.add(buildForm(), BorderLayout.CENTER);
        setContentPane(root);
        UiTheme.fadeIn(this);
    }

    private JPanel buildLeftBanner() {
        JPanel banner = new JPanel(new GridBagLayout());
        banner.setPreferredSize(new Dimension(330, 0));
        banner.setBackground(UiTheme.PRIMARY_DARK);
        banner.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel eyebrow = new JLabel("GROCERY POS");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        eyebrow.setForeground(UiTheme.ACCENT);

        JLabel line1 = new JLabel("<html>Run your store<br>with confidence.</html>");
        line1.setFont(new Font("Segoe UI Semibold", Font.BOLD, 30));
        line1.setForeground(new Color(250, 248, 239));

        JLabel line2 = new JLabel("<html><div style='width:245px'>Manage inventory, billing, customers and suppliers from one refined workspace.</div></html>");
        line2.setForeground(new Color(205, 216, 205));
        line2.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridy = 0; banner.add(eyebrow, gbc);
        gbc.gridy = 1; banner.add(line1, gbc);
        gbc.gridy = 2; banner.add(line2, gbc);
        return banner;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(34, 48, 34, 48)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        heading.setForeground(UiTheme.TEXT);
        JLabel subheading = new JLabel("Sign in to continue to your workspace");
        subheading.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subheading.setForeground(UiTheme.MUTED);

        usernameField.setPreferredSize(new Dimension(300, 42));
        passwordField.setPreferredSize(new Dimension(300, 42));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));

        ButtonGroup roles = new ButtonGroup();
        roles.add(adminRadio); roles.add(cashierRadio);
        styleRadio(adminRadio); styleRadio(cashierRadio);
        JPanel roleRow = new JPanel();
        roleRow.setOpaque(false);
        roleRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        roleRow.add(adminRadio); roleRow.add(cashierRadio);

        JButton loginBtn = UiTheme.primaryButton("Sign in");
        JButton registerBtn = UiTheme.infoButton("Create account");
        loginBtn.setPreferredSize(new Dimension(145, 42));
        registerBtn.setPreferredSize(new Dimension(145, 42));
        getRootPane().setDefaultButton(loginBtn);
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            new RegisterFrame(this).setVisible(true); setVisible(false);
        });

        JPanel buttons = new JPanel();
        buttons.setOpaque(false); buttons.add(loginBtn); buttons.add(registerBtn);

        JLabel hint = new JLabel("Demo access: admin / admin123  or  cashier / cashier123");
        hint.setForeground(UiTheme.MUTED);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        int row = 0;
        gbc.gridy = row++; form.add(heading, gbc);
        gbc.gridy = row++; form.add(subheading, gbc);
        gbc.gridy = row++; form.add(roleRow, gbc);
        gbc.gridy = row++; form.add(UiTheme.fieldLabel("Username"), gbc);
        gbc.gridy = row++; form.add(usernameField, gbc);
        gbc.gridy = row++; form.add(UiTheme.fieldLabel("Password"), gbc);
        gbc.gridy = row++; form.add(passwordField, gbc);
        gbc.gridy = row++; form.add(buttons, gbc);
        gbc.gridy = row++; form.add(hint, gbc);
        return form;
    }

    private void styleRadio(JRadioButton radio) {
        radio.setOpaque(false);
        radio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        radio.setForeground(UiTheme.TEXT);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = adminRadio.isSelected() ? "Admin" : "Cashier";
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        UserAccount user = DummyStore.get().findUser(username, password, role);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials or role.", "Login Failed", JOptionPane.ERROR_MESSAGE); return;
        }
        DashboardFrame dashboard = new DashboardFrame(user, this);
        dashboard.setVisible(true); setVisible(false);
        usernameField.setText(""); passwordField.setText("");
    }
}
