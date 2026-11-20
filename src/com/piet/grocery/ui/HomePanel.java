package com.piet.grocery.ui;

import com.piet.grocery.data.DummyStore;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

/** Dashboard home with live store totals and quick navigation. */
public class HomePanel extends JPanel {
    private final DashboardFrame dashboard;
    private final JPanel cardsRow = new JPanel(new GridLayout(1, 4, 12, 12));

    public HomePanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout(16, 16));
        setBackground(UiTheme.BACKGROUND);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Today's store overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UiTheme.TEXT);
        JLabel hint = new JLabel("Monitor inventory and move quickly between POS operations.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(UiTheme.MUTED);
        JPanel copy = new JPanel(new GridLayout(2, 1, 0, 3));
        copy.setOpaque(false);
        copy.add(title);
        copy.add(hint);
        heading.add(copy, BorderLayout.WEST);

        cardsRow.setOpaque(false);
        refreshCounts();

        JPanel quick = new JPanel(new GridLayout(1, 4, 12, 12));
        quick.setOpaque(false);
        quick.add(action("New bill", "Billing", DashboardFrame.BILLING));
        quick.add(action("Inventory", "Manage products", DashboardFrame.INVENTORY));
        quick.add(action("Customers", "View records", DashboardFrame.CUSTOMERS));
        quick.add(action("Suppliers", "View records", DashboardFrame.SUPPLIERS));

        JPanel quickWrap = new JPanel(new BorderLayout(0, 8));
        quickWrap.setOpaque(false);
        JLabel quickTitle = new JLabel("Quick actions");
        quickTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        quickTitle.setForeground(UiTheme.TEXT);
        quickWrap.add(quickTitle, BorderLayout.NORTH);
        quickWrap.add(quick, BorderLayout.CENTER);

        add(heading, BorderLayout.NORTH);
        add(cardsRow, BorderLayout.CENTER);
        add(quickWrap, BorderLayout.SOUTH);
    }

    public void refreshCounts() {
        DummyStore store = DummyStore.get();
        cardsRow.removeAll();
        cardsRow.add(UiTheme.summaryCard("Products", String.valueOf(store.getProducts().size()), UiTheme.PRIMARY));
        cardsRow.add(UiTheme.summaryCard("Customers", String.valueOf(store.getCustomers().size()), UiTheme.INFO));
        cardsRow.add(UiTheme.summaryCard("Suppliers", String.valueOf(store.getSuppliers().size()), new Color(101, 154, 157)));
        cardsRow.add(UiTheme.summaryCard("Users", String.valueOf(store.getUsers().size()), new Color(245, 158, 11)));
        cardsRow.revalidate();
        cardsRow.repaint();
    }

    private JPanel action(String title, String detail, String screen) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UiTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel name = new JLabel(title);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(UiTheme.TEXT);
        JLabel desc = new JLabel(detail);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        desc.setForeground(UiTheme.MUTED);
        JPanel copy = new JPanel(new GridLayout(2, 1, 0, 3));
        copy.setOpaque(false);
        copy.add(name);
        copy.add(desc);
        JButton open = UiTheme.primaryButton("Open");
        open.setPreferredSize(new java.awt.Dimension(72, 34));
        open.addActionListener(e -> dashboard.showScreen(screen));
        card.add(copy, BorderLayout.CENTER);
        card.add(open, BorderLayout.EAST);
        return card;
    }
}
