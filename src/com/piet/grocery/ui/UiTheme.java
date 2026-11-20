package com.piet.grocery.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Window;

/** Shared POS visual tokens and component styles. */
public final class UiTheme {
    public static final Color PRIMARY = new Color(35, 151, 155);
    public static final Color PRIMARY_DARK = new Color(24, 82, 88);
    public static final Color SIDEBAR = new Color(37, 142, 146);
    public static final Color ACCENT = new Color(213, 145, 48);
    public static final Color BACKGROUND = new Color(246, 248, 247);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(31, 46, 51);
    public static final Color MUTED = new Color(105, 119, 124);
    public static final Color DANGER = new Color(178, 71, 73);
    public static final Color INFO = new Color(73, 128, 132);
    public static final Color BORDER = new Color(224, 231, 229);

    public static final String COLLEGE = "Poornima Institute of Engineering & Technology";
    public static final String PROJECT = "Grocery Billing & Inventory Management System";
    public static final String ROLL = "PIET25CS050";
    public static final String STUDENT = "DIVYANSH DUA";

    private UiTheme() {}

    public static JButton primaryButton(String text) {
        return filledButton(text, PRIMARY, Color.WHITE);
    }

    public static JButton warningButton(String text) {
        return filledButton(text, new Color(210, 137, 48), Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return filledButton(text, DANGER, Color.WHITE);
    }

    public static JButton infoButton(String text) {
        return filledButton(text, new Color(91, 130, 132), Color.WHITE);
    }

    public static JButton filledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(130, 40));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        animateBackground(button, background, brighten(background, 12));
        return button;
    }

    public static void animateBackground(JButton button, Color normal, Color hovered) {
        button.setBackground(normal);
        button.addMouseListener(new MouseAdapter() {
            private final Timer[] timer = { null };

            @Override public void mouseEntered(MouseEvent event) { transition(button, normal, hovered, timer); }
            @Override public void mouseExited(MouseEvent event) { transition(button, hovered, normal, timer); }
            @Override public void mousePressed(MouseEvent event) {
                button.setLocation(button.getX(), button.getY() + 1);
                button.setBackground(darken(normal, 8));
            }
            @Override public void mouseReleased(MouseEvent event) {
                button.setLocation(button.getX(), button.getY() - 1);
            }
        });
    }

    private static void transition(JButton button, Color from, Color to, Timer[] holder) {
        if (holder[0] != null) holder[0].stop();
        final int[] step = {0};
        holder[0] = new Timer(12, event -> {
            step[0]++;
            float progress = Math.min(1f, step[0] / 10f);
            button.setBackground(blend(from, to, progress));
            if (progress >= 1f) ((Timer) event.getSource()).stop();
        });
        holder[0].start();
    }

    public static void fadeIn(Window window) {
        try {
            window.setOpacity(0f);
            final int[] step = {0};
            Timer timer = new Timer(16, null);
            timer.addActionListener(event -> {
                step[0]++;
                float opacity = Math.min(1f, step[0] / 10f);
                window.setOpacity(opacity);
                if (opacity >= 1f) timer.stop();
            });
            timer.start();
        } catch (Exception ignored) {}
    }

    private static Color blend(Color a, Color b, float amount) {
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * amount),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * amount),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * amount));
    }

    public static Color brighten(Color color, int amount) {
        return new Color(Math.min(255, color.getRed() + amount),
                Math.min(255, color.getGreen() + amount),
                Math.min(255, color.getBlue() + amount));
    }

    public static Color darken(Color color, int amount) {
        return new Color(Math.max(0, color.getRed() - amount),
                Math.max(0, color.getGreen() - amount),
                Math.max(0, color.getBlue() - amount));
    }

    public static JPanel headerBar(String pageTitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(14, 22, 14, 22)));
        JLabel title = new JLabel(pageTitle);
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);
        JLabel meta = new JLabel(STUDENT + "  ·  " + ROLL);
        meta.setForeground(MUTED);
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        header.add(meta, BorderLayout.EAST);
        return header;
    }

    public static JPanel summaryCard(String caption, String value, Color stripe) {
        JPanel card = new JPanel(new BorderLayout(12, 4));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        JPanel copy = new JPanel(new GridLayout(2, 1, 0, 5));
        copy.setOpaque(false);
        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        captionLabel.setForeground(MUTED);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 25));
        valueLabel.setForeground(TEXT);
        copy.add(captionLabel); copy.add(valueLabel);
        JPanel icon = new JPanel();
        icon.setPreferredSize(new Dimension(42, 42));
        icon.setBackground(brighten(stripe, 20));
        icon.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(brighten(stripe, 4)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.add(icon, BorderLayout.WEST); card.add(copy, BorderLayout.CENTER);
        return card;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(222, 242, 240));
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        header.setBackground(new Color(247, 249, 248));
        header.setForeground(MUTED);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.setDefaultRenderer(Object.class, renderer);
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(MUTED);
        return label;
    }
}
