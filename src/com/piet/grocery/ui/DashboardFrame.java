package com.piet.grocery.ui;

import com.piet.grocery.model.UserAccount;

import javax.swing.BorderFactory;
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
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

/** Main window after login: POS sidebar, header, and card-based module workspace. */
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
        setSize(1280, 780);
        setMinimumSize(new java.awt.Dimension(980, 650));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiTheme.BACKGROUND);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
        setContentPane(root);
        showScreen(HOME);
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UiTheme.BACKGROUND);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UiTheme.CARD);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 22, 14, 22)));
        JLabel heading = new JLabel("Welcome back, " + currentUser.getFullName());
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(UiTheme.TEXT);
        JLabel subtitle = new JLabel("Store overview for today's operations");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UiTheme.MUTED);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        titles.add(heading);
        titles.add(subtitle);
        top.add(titles, BorderLayout.WEST);

        JLabel profile = new JLabel(" " + currentUser.getRole() + "  ·  " + currentUser.getUsername() + " ");
        profile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        profile.setForeground(UiTheme.TEXT);
        profile.setOpaque(true);
        profile.setBackground(new Color(238, 245, 244));
        profile.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        top.add(profile, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        cardPanel.setOpaque(true);
        cardPanel.setBackground(UiTheme.BACKGROUND);
        cardPanel.add(homePanel, HOME);
        cardPanel.add(new InventoryPanel(), INVENTORY);
        cardPanel.add(new BillingPanel(), BILLING);
        cardPanel.add(new CustomerPanel(), CUSTOMERS);
        cardPanel.add(new SupplierPanel(), SUPPLIERS);
        cardPanel.add(new UserPanel(), USERS);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(UiTheme.BACKGROUND);
        workspace.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        workspace.add(cardPanel, BorderLayout.CENTER);
        main.add(workspace, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new java.awt.Dimension(210, 0));
        sidebar.setBackground(UiTheme.SIDEBAR);

        JLabel brand = new JLabel("  GROCERY POS");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setBorder(BorderFactory.createEmptyBorder(20, 12, 18, 12));
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel links = new JPanel(new GridLayout(12, 1, 0, 4));
        links.setOpaque(false);
        links.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        links.add(sideButton("Dashboard", HOME));
        links.add(sideButton("Billing", BILLING));
        links.add(sideButton("Inventory", INVENTORY));
        links.add(sideButton("Customers", CUSTOMERS));
        links.add(sideButton("Suppliers", SUPPLIERS));
        if ("Admin".equalsIgnoreCase(currentUser.getRole())) {
            links.add(sideButton("Users", USERS));
        }
        sidebar.add(links, BorderLayout.CENTER);

        JPanel account = new JPanel(new GridLayout(2, 1, 0, 6));
        account.setOpaque(false);
        account.setBorder(BorderFactory.createEmptyBorder(10, 10, 14, 10));
        JButton password = sidebarButton("Change Password");
        password.addActionListener(e -> new ChangePasswordDialog(this, currentUser).setVisible(true));
        JButton logout = sidebarButton("Logout");
        logout.addActionListener(e -> logout());
        account.add(password);
        account.add(logout);
        sidebar.add(account, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton sideButton(String label, String screen) {
        JButton button = sidebarButton(label);
        button.addActionListener(e -> showScreen(screen));
        return button;
    }

    private JButton sidebarButton(String label) {
        JButton button = new JButton(label);
        button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button.setForeground(Color.WHITE);
        button.setBackground(UiTheme.SIDEBAR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setPreferredSize(new java.awt.Dimension(190, 40));
        return button;
    }

    public UserAccount getCurrentUser() { return currentUser; }

    public void showScreen(String name) {
        if (USERS.equals(name) && !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Only Admin can open User Management.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cards.show(cardPanel, name);
        homePanel.refreshCounts();
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
