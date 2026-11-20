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
import java.awt.BorderLayout;

/** Shared POS visual tokens and component styles. */
public final class UiTheme {
    public static final Color PRIMARY = new Color(48, 166, 170);
    public static final Color PRIMARY_DARK = new Color(35, 104, 112);
    public static final Color SIDEBAR = new Color(54, 173, 176);
    public static final Color ACCENT = new Color(245, 158, 11);
    public static final Color BACKGROUND = new Color(246, 248, 248);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(38, 52, 60);
    public static final Color MUTED = new Color(112, 126, 132);
    public static final Color DANGER = new Color(190, 63, 67);
    public static final Color INFO = new Color(47, 111, 173);
    public static final Color BORDER = new Color(226, 233, 232);

    public static final String COLLEGE = "Poornima Institute of Engineering & Technology";
    public static final String PROJECT = "Grocery Billing & Inventory Management System";
    public static final String ROLL = "PIET25CS050";
    public static final String STUDENT = "DIVYANSH DUA";

    private UiTheme() {}

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
        return filledButton(text, new Color(100, 145, 148), Color.WHITE);
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
        button.setPreferredSize(new Dimension(130, 38));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    public static JPanel headerBar(String pageTitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        JLabel title = new JLabel(pageTitle);
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JLabel meta = new JLabel(STUDENT + "  ·  " + ROLL);
        meta.setForeground(MUTED);
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        header.add(meta, BorderLayout.EAST);
        return header;
    }

    public static JPanel summaryCard(String caption, String value, Color stripe) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JPanel copy = new JPanel(new GridLayout(2, 1, 0, 4));
        copy.setOpaque(false);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT);
        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        captionLabel.setForeground(MUTED);
        copy.add(captionLabel);
        copy.add(valueLabel);

        JPanel icon = new JPanel();
        icon.setPreferredSize(new Dimension(38, 38));
        icon.setBackground(stripe);
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(icon, BorderLayout.WEST);
        card.add(copy, BorderLayout.CENTER);
        return card;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(220, 242, 241));
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(245, 248, 248));
        header.setForeground(MUTED);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.setDefaultRenderer(Object.class, renderer);
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(MUTED);
        return label;
    }
}
