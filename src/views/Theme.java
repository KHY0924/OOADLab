package views;

import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

public class Theme {
    public static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    public static final Color PRIMARY_DARK = new Color(48, 63, 159);
    public static final Color ACCENT_COLOR = new Color(255, 64, 129);

    public static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    public static final Color TEXT_SECONDARY = new Color(66, 66, 66);

    public static final Color BG_COLOR = new Color(245, 245, 245);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color UNVERIFIED_BG = new Color(224, 224, 224);

    public static final Color SECONDARY_COLOR = new Color(232, 234, 246);

    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font STANDARD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public static void styleButton(JButton button) {
        button.setFont(BOLD_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);

        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setUI(new BasicButtonUI());

        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(BOLD_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_COLOR);

        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleLabel(JLabel label, boolean isHeader) {
        label.setFont(isHeader ? HEADER_FONT : STANDARD_FONT);
        label.setForeground(isHeader ? TEXT_PRIMARY : TEXT_SECONDARY);
    }

    public static Border createPaddingBorder() {
        return BorderFactory.createEmptyBorder(20, 20, 20, 20);
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public static void styleTable(javax.swing.JTable table) {
        table.setFont(STANDARD_FONT);
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.getTableHeader().setFont(BOLD_FONT);
        table.getTableHeader().setBackground(BG_COLOR);
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
    }
}
