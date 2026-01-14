package views;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CoordinatorPanel extends JPanel {
    private MainFrame mainFrame;
    private DefaultTableModel sessionModel;
    private DefaultTableModel assignmentModel;

    public CoordinatorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Coordinator Dashboard");
        title.setFont(Theme.HEADER_FONT);
        title.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        Theme.styleSecondaryButton(logoutButton);
        logoutButton.setBackground(Theme.PRIMARY_DARK);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoutButton.addActionListener(e -> mainFrame.showPanel(MainFrame.LOGIN_PANEL));

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.SUBHEADER_FONT);

        tabbedPane.addTab("Manage Sessions", createSessionPanel());
        tabbedPane.addTab("Assign Evaluators", createAssignmentPanel());
        tabbedPane.addTab("Reports & Awards", createReportPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSessionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(Theme.createPaddingBorder());

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        String[] columns = { "ID", "Date", "Venue", "Type" };
        Object[][] data = {
                { "S01", "2026-02-10 09:00", "Room A", "Oral" },
                { "S02", "2026-02-10 14:00", "Hall B", "Poster" }
        };
        sessionModel = new DefaultTableModel(data, columns);
        JTable table = new JTable(sessionModel);
        table.setRowHeight(30);
        table.setFont(Theme.STANDARD_FONT);
        table.getTableHeader().setFont(Theme.BOLD_FONT);
        table.getTableHeader().setBackground(Theme.UNVERIFIED_BG);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Theme.CARD_BG);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add Session");
        Theme.styleButton(addButton);
        addButton.addActionListener(e -> {
            sessionModel.addRow(new Object[] { "S03", "2026-02-11 10:00", "Lab 1", "Oral" });
        });

        controlPanel.add(addButton);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(controlPanel, BorderLayout.SOUTH);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAssignmentPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel("Assign Student to Evaluator");
        header.setFont(Theme.SUBHEADER_FONT);
        header.setForeground(Theme.PRIMARY_COLOR);

        JLabel studentLabel = new JLabel("Select Student");
        Theme.styleLabel(studentLabel, false);
        String[] students = { "Alice (Oral)", "Bob (Poster)", "Charlie (Oral)" };
        JComboBox<String> studentCombo = new JComboBox<>(students);
        studentCombo.setFont(Theme.STANDARD_FONT);
        studentCombo.setBackground(Color.WHITE);

        JLabel evaluatorLabel = new JLabel("Select Evaluator");
        Theme.styleLabel(evaluatorLabel, false);
        String[] evaluators = { "Dr. Smith", "Prof. Johnson", "Dr. Lee" };
        JComboBox<String> evaluatorCombo = new JComboBox<>(evaluators);
        evaluatorCombo.setFont(Theme.STANDARD_FONT);
        evaluatorCombo.setBackground(Color.WHITE);

        JButton assignButton = new JButton("Assign");
        Theme.styleButton(assignButton);
        assignButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Assigned " + studentCombo.getSelectedItem() + " to " + evaluatorCombo.getSelectedItem());
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(header, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        card.add(studentLabel, gbc);
        gbc.gridx = 1;
        card.add(studentCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(evaluatorLabel, gbc);
        gbc.gridx = 1;
        card.add(evaluatorCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        card.add(assignButton, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(Theme.createPaddingBorder());

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportArea.setText("Click 'Generate Schedule' to view details...");
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.CARD_BG);

        JButton reportButton = new JButton("Generate Schedule");
        Theme.styleSecondaryButton(reportButton);
        reportButton.addActionListener(e -> {
            reportArea.setText("--- SEMINAR SCHEDULE ---\n\n" +
                    "09:00 AM - Alice (Room A)\n" +
                    "09:30 AM - Charlie (Room A)\n" +
                    "14:00 PM - Bob (Hall B)\n\n" +
                    "--- EVALUATION SUMMARY ---\n" +
                    "Pending evaluations for 2 students...");
        });

        JButton awardButton = new JButton("Calculate Awards");
        Theme.styleButton(awardButton);
        awardButton.setBackground(new Color(255, 193, 7));
        awardButton.setForeground(Color.BLACK);
        awardButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Best Oral Presentation: Alice\n" +
                            "Best Poster: Bob\n" +
                            "People's Choice: Charlie",
                    "Award Winners",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(reportButton);
        buttonPanel.add(awardButton);

        card.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }
}
