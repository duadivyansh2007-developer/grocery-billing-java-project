package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** Dashboard home with summary cards and quick navigation. */
public class HomePanel extends JPanel {
    private final DashboardFrame dashboard;
    private final JPanel cardsRow = new JPanel(new GridLayout(1, 4, 12, 12));

    public HomePanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(16, 16));
        setBackground(UiTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(4, 20, 20, 20));

        JLabel welcome = new JLabel("Welcome, " + dashboard.getCurrentUser().getFullName()
                + "  (" + dashboard.getCurrentUser().getRole() + ")");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setForeground(UiTheme.PRIMARY_DARK);

        JLabel hint = new JLabel("A quick view of today's store activity and available modules.");
        hint.setForeground(UiTheme.MUTED);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(welcome);
        top.add(hint);

        cardsRow.setOpaque(false);
        refreshCounts();

        JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        quick.setOpaque(false);
        quick.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UiTheme.BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        quick.add(nav("Open Inventory", DashboardFrame.INVENTORY, UiTheme.PRIMARY));
        quick.add(nav("New Bill", DashboardFrame.BILLING, new Color(245, 124, 0)));
        quick.add(nav("Customers", DashboardFrame.CUSTOMERS, UiTheme.INFO));
        quick.add(nav("Suppliers", DashboardFrame.SUPPLIERS, new Color(81, 45, 168)));

        add(top, BorderLayout.NORTH);
        add(cardsRow, BorderLayout.CENTER);
        add(quick, BorderLayout.SOUTH);
    }

    public void refreshCounts() {
        DummyStore store = DummyStore.get();
        cardsRow.removeAll();
        cardsRow.add(UiTheme.summaryCard("Products in stock", String.valueOf(store.getProducts().size()), UiTheme.PRIMARY));
        cardsRow.add(UiTheme.summaryCard("Customers", String.valueOf(store.getCustomers().size()), UiTheme.INFO));
        cardsRow.add(UiTheme.summaryCard("Suppliers", String.valueOf(store.getSuppliers().size()), new Color(81, 45, 168)));
        cardsRow.add(UiTheme.summaryCard("Users", String.valueOf(store.getUsers().size()), new Color(245, 124, 0)));
        cardsRow.revalidate();
        cardsRow.repaint();
    }

    private JButton nav(String text, String screen, Color color) {
        JButton button = UiTheme.filledButton(text, color, Color.WHITE);
        button.setPreferredSize(new java.awt.Dimension(160, 40));
        button.addActionListener(e -> dashboard.showScreen(screen));
        return button;
    }
}
