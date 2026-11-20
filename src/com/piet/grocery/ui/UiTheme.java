package com.piet.grocery.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/** Shared colours, fonts and component styles for the application. */
public final class UiTheme {
    public static final Color PRIMARY = new Color(15, 118, 110);
    public static final Color PRIMARY_DARK = new Color(18, 52, 59);
    public static final Color ACCENT = new Color(245, 158, 11);
    public static final Color BACKGROUND = new Color(244, 247, 246);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(23, 48, 66);
    public static final Color MUTED = new Color(92, 108, 118);
    public static final Color DANGER = new Color(190, 63, 67);
    public static final Color INFO = new Color(47, 111, 173);
    public static final Color BORDER = new Color(218, 227, 226);

    public static final String COLLEGE = "Poornima Institute of Engineering & Technology";
    public static final String PROJECT = "Grocery Billing & Inventory Management System";
    public static final String ROLL = "PIET25CS050";
    public static final String STUDENT = "DIVYANSH DUA";

    private UiTheme() {
    }

    public static JButton primaryButton(String text) {
        return filledButton(text, PRIMARY, Color.WHITE);
    }

    public static JButton warningButton(String text) {
        return filledButton(text, new Color(245, 124, 0), Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return filledButton(text, DANGER, Color.WHITE);
    }

    public static JButton infoButton(String text) {
        return filledButton(text, INFO, Color.WHITE);
    }

    public static JButton filledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(130, 36));
        button.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        return button;
    }

    public static JPanel headerBar(String pageTitle) {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel(PROJECT + "  |  " + pageTitle);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JLabel meta = new JLabel(COLLEGE + "   ·   " + ROLL + "   ·   " + STUDENT);
        meta.setForeground(new Color(184, 211, 211));
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(title);
        header.add(meta);
        return header;
    }

    public static JPanel summaryCard(String caption, String value, Color stripe) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, stripe),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 16, 13, 16))));

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        captionLabel.setForeground(MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(TEXT);

        card.add(captionLabel);
        card.add(valueLabel);
        return card;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(210, 237, 234));
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 34));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
}
