package com.piet.grocery.ui;

import com.piet.grocery.model.UserAccount;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

/** Main window after login: menu, toolbar, and CardLayout screens. */
public class DashboardFrame extends JFrame {
    public static final String HOME = "HOME";
    public static final String INVENTORY = "INVENTORY";
    public static final String BILLING = "BILLING";
    public static final String CUSTOMERS = "CUSTOMERS";
    public static final String SUPPLIERS = "SUPPLIERS";
    public static final String USERS = "USERS";

    private final UserAccount currentUser;
    private final LoginFrame loginFrame;
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);
    private final HomePanel homePanel;

    public DashboardFrame(UserAccount currentUser, LoginFrame loginFrame) {
        this.currentUser = currentUser;
        this.loginFrame = loginFrame;
        this.homePanel = new HomePanel(this);

        setTitle(UiTheme.PROJECT + " - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenu());

        JPanel root = new JPanel(new BorderLayout());
        root.add(UiTheme.headerBar("Dashboard"), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(UiTheme.BACKGROUND);
        center.add(buildToolbar(), BorderLayout.NORTH);

        cardPanel.add(homePanel, HOME);
        cardPanel.add(new InventoryPanel(), INVENTORY);
        cardPanel.add(new BillingPanel(), BILLING);
        cardPanel.add(new CustomerPanel(), CUSTOMERS);
        cardPanel.add(new SupplierPanel(), SUPPLIERS);
        cardPanel.add(new UserPanel(), USERS);
        center.add(cardPanel, BorderLayout.CENTER);

        JLabel status = new JLabel("  Logged in as " + currentUser.getFullName()
                + "  (" + currentUser.getRole() + ")   |   Java Swing desktop app | MySQL/JDBC connected");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        status.setForeground(Color.WHITE);
        status.setOpaque(true);
        status.setBackground(UiTheme.PRIMARY_DARK);
        status.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));

        root.add(center, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        setContentPane(root);
        showScreen(HOME);
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void showScreen(String name) {
        if (USERS.equals(name) && !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Only Admin can open User Management.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cards.show(cardPanel, name);
        homePanel.refreshCounts();
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu modules = new JMenu("Modules");
        modules.setMnemonic(KeyEvent.VK_M);
        modules.add(item("Dashboard", HOME, KeyEvent.VK_D));
        modules.add(item("Inventory", INVENTORY, KeyEvent.VK_I));
        modules.add(item("Billing", BILLING, KeyEvent.VK_B));
        modules.add(item("Customers", CUSTOMERS, KeyEvent.VK_C));
        modules.add(item("Suppliers", SUPPLIERS, KeyEvent.VK_S));
        if ("Admin".equalsIgnoreCase(currentUser.getRole())) {
            modules.add(item("Users", USERS, KeyEvent.VK_U));
        }

        JMenu account = new JMenu("Account");
        JMenuItem changePassword = new JMenuItem("Change Password");
        changePassword.addActionListener(e -> new ChangePasswordDialog(this, currentUser).setVisible(true));
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> logout());
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        account.add(changePassword);
        account.add(logout);
        account.addSeparator();
        account.add(exit);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                UiTheme.PROJECT + "\n"
                        + "Student: " + UiTheme.STUDENT + "\n"
                        + "Roll No: " + UiTheme.ROLL + "\n"
                        + UiTheme.COLLEGE + "\n"
                        + "Tech: Java Swing (GUI prototype for Mid Term I)",
                "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);

        bar.add(modules);
        bar.add(account);
        bar.add(help);
        return bar;
    }

    private JMenuItem item(String label, String screen, int key) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(key, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(e -> showScreen(screen));
        return menuItem;
    }

    private JToolBar buildToolbar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 16, 0, 16));
        bar.setBackground(UiTheme.BACKGROUND);
        bar.add(tool("Dashboard", HOME));
        bar.add(tool("Inventory", INVENTORY));
        bar.add(tool("Billing", BILLING));
        bar.add(tool("Customers", CUSTOMERS));
        bar.add(tool("Suppliers", SUPPLIERS));
        if ("Admin".equalsIgnoreCase(currentUser.getRole())) {
            bar.add(tool("Users", USERS));
        }
        bar.addSeparator();
        JButton password = UiTheme.warningButton("Password");
        password.addActionListener(e -> new ChangePasswordDialog(this, currentUser).setVisible(true));
        JButton logout = UiTheme.dangerButton("Logout");
        logout.addActionListener(e -> logout());
        bar.add(password);
        bar.add(logout);
        return bar;
    }

    private JButton tool(String label, String screen) {
        JButton button = UiTheme.primaryButton(label);
        button.addActionListener(e -> showScreen(screen));
        return button;
    }

    private void logout() {
        int ok = JOptionPane.showConfirmDialog(this, "Logout and return to the login screen?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            dispose();
            loginFrame.setVisible(true);
        }
    }
}
