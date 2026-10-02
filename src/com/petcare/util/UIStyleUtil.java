package com.petcare.util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Styling and Modern Component Helpers for Java Swing UI
 */
public class UIStyleUtil {

    // Theme Color Palette
    public static final Color COLOR_PRIMARY = new Color(24, 76, 120);       // Deep Navy
    public static final Color COLOR_PRIMARY_HOVER = new Color(35, 100, 155);
    public static final Color COLOR_SECONDARY = new Color(74, 144, 226);    // Sky Blue
    public static final Color COLOR_ACCENT = new Color(235, 130, 60);       // Warm Amber/Orange
    public static final Color COLOR_SUCCESS = new Color(46, 139, 87);       // Sea Green
    public static final Color COLOR_DANGER = new Color(217, 83, 79);        // Soft Red
    public static final Color COLOR_WARNING = new Color(240, 173, 78);      // Orange Yellow
    public static final Color COLOR_BG_LIGHT = new Color(245, 247, 250);    // Light Grey Background
    public static final Color COLOR_BG_CARD = Color.WHITE;
    public static final Color COLOR_TEXT_DARK = new Color(40, 44, 52);
    public static final Color COLOR_TEXT_MUTED = new Color(110, 115, 125);
    public static final Color COLOR_BORDER = new Color(220, 224, 230);
    public static final Color COLOR_SIDEBAR_BG = new Color(18, 38, 58);
    public static final Color COLOR_SIDEBAR_HOVER = new Color(30, 60, 90);
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(45, 95, 145);

    // Modern Typography
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_MED = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private UIStyleUtil() {}

    /**
     * Creates a styled, modern button with rounded corners, proper padding and hover effect
     */
    public static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        // Use BasicButtonUI to avoid LookAndFeel specific padding/gradient glitches
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(FONT_BODY_BOLD);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, COLOR_PRIMARY, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, COLOR_SUCCESS, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, COLOR_DANGER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, new Color(225, 230, 238), COLOR_TEXT_DARK);
    }

    /**
     * Creates a modern statistical Card Panel with a metric and label.
     * Uses 3-row GridLayout so text elements can NEVER vertically overlap.
     */
    public static JPanel createStatCard(String title, String value, String subtext, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 3, 3); // Left accent bar
                g2.dispose();
            }
        };
        card.setLayout(new java.awt.GridLayout(3, 1, 0, 2));
        card.setBackground(COLOR_BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(COLOR_TEXT_MUTED);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(COLOR_TEXT_DARK);

        JLabel subLbl = new JLabel(subtext);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(accentColor);

        card.add(titleLbl);
        card.add(valueLbl);
        card.add(subLbl);

        card.setPreferredSize(new Dimension(180, 95));
        card.setMinimumSize(new Dimension(140, 85));
        return card;
    }

    /**
     * Styles JTable with modern padding, headers, selection color, and zebra stripes
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_BODY);
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(new Color(220, 235, 252));
        table.setSelectionForeground(COLOR_TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(new Color(238, 242, 246));
        header.setForeground(COLOR_TEXT_DARK);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Integer.class, centerRenderer);
        table.setDefaultRenderer(Double.class, centerRenderer);
    }
}
