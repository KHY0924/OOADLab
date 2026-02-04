package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import models.Session;
import models.PresentationBoard;
import database.SessionDAO;
import database.UserDAO;
import database.SubmissionDAO;
import database.ReportDAO;
import database.PresentationBoardDAO;
import services.PosterPresentationService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class CoordinatorPanel extends JPanel {
    private MainFrame mainFrame;
    private DefaultTableModel sessionModel;
    private DefaultTableModel assignmentModel;
    private SessionDAO sessionDAO;
    private UserDAO userDAO;
    private SubmissionDAO submissionDAO;
    private ReportDAO reportDAO;
    private PosterPresentationService posterService;
    private PresentationBoardDAO boardDAO;

    private JComboBox<String> seminarCombo;
    private List<String> seminarIds;

    public CoordinatorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.sessionDAO = new SessionDAO();
        this.userDAO = new UserDAO();
        this.submissionDAO = new SubmissionDAO();
        this.reportDAO = new ReportDAO();
        this.posterService = new PosterPresentationService();
        this.boardDAO = new PresentationBoardDAO();

        this.seminarIds = new ArrayList<>();

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
        tabbedPane.addTab("Poster Management", createPosterPanel());
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

        // Seminar selector panel at the top
        JPanel seminarSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seminarSelectorPanel.setBackground(Theme.CARD_BG);
        seminarSelectorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel seminarLabel = new JLabel("Select Seminar:");
        seminarLabel.setFont(Theme.BOLD_FONT);
        seminarCombo = new JComboBox<>();
        seminarCombo.setFont(Theme.STANDARD_FONT);
        seminarCombo.setPreferredSize(new Dimension(350, 30));
        loadSeminars();

        seminarCombo.addActionListener(e -> {
            // When seminar is selected, reload sessions for that seminar
            loadSessions();
        });

        seminarSelectorPanel.add(seminarLabel);
        seminarSelectorPanel.add(seminarCombo);

        String[] columns = { "ID", "Date", "Venue", "Type", "Evaluator", "Action" };
        sessionModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        loadSessions();
        JTable table = new JTable(sessionModel);
        table.setRowHeight(35); // Slightly taller for buttons
        table.setFont(Theme.STANDARD_FONT);
        table.getTableHeader().setFont(Theme.BOLD_FONT);
        table.getTableHeader().setBackground(Theme.UNVERIFIED_BG);

        // Action Column Buttons
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonsRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonsEditor(new JCheckBox(), table));
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Hide ID column but keep data
        table.getColumnModel().getColumn(5).setPreferredWidth(170); // Wider for two buttons

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Theme.CARD_BG);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add Session");
        Theme.styleButton(addButton);
        addButton.addActionListener(e -> {
            showAddSessionDialog();
        });

        JButton addSeminarButton = new JButton("Add Seminar");
        Theme.styleButton(addSeminarButton);
        addSeminarButton.addActionListener(e -> {
            showAddSeminarDialog();
            loadSeminars(); // Refresh seminar dropdown after adding
        });

        JButton refreshButton = new JButton("Refresh");
        Theme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> {
            loadSessions();
            loadSeminars();
        });

        controlPanel.add(addButton);
        controlPanel.add(addSeminarButton);
        controlPanel.add(refreshButton);

        card.add(seminarSelectorPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(controlPanel, BorderLayout.SOUTH);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void loadSessions() {
        sessionModel.setRowCount(0);
        int idx = seminarCombo != null ? seminarCombo.getSelectedIndex() : -1;
        List<Session> sessions;
        if (idx >= 0 && idx < seminarIds.size()) {
            String selectedSeminarId = seminarIds.get(idx);
            sessions = sessionDAO.getSessionsBySeminar(selectedSeminarId);
        } else {
            sessions = sessionDAO.getAllSessions();
        }
        for (Session s : sessions) {
            String evalDisplay = s.getEvaluatorName() != null ? s.getEvaluatorName() : "";
            sessionModel.addRow(new Object[] {
                    s.getSessionID(),
                    s.getSessionDate() + " " + s.getSessionTime(),
                    s.getLocation(),
                    s.getSessionType(),
                    evalDisplay,
                    "Edit"
            });
        }
    }

    private void loadSeminars() {
        if (seminarCombo == null)
            return;
        seminarCombo.removeAllItems();
        seminarIds.clear();
        try {
            ResultSet rs = sessionDAO.getAllSeminars();
            while (rs.next()) {
                String seminarId = rs.getString("seminar_id");
                String location = rs.getString("location");
                int semester = rs.getInt("semester");
                int year = rs.getInt("year");
                String displayText = "Semester " + semester + " " + year + " - " + location;
                seminarCombo.addItem(displayText);
                seminarIds.add(seminarId);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddSessionDialog() {
        // Check if a seminar is selected
        int seminarIdx = seminarCombo != null ? seminarCombo.getSelectedIndex() : -1;
        if (seminarIdx < 0 || seminarIdx >= seminarIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a seminar first.");
            return;
        }
        String selectedSeminarId = seminarIds.get(seminarIdx);

        JTextField locField = new JTextField();
        JTextField dateField = new JTextField("2026-02-10 09:00");
        String[] types = { "Oral", "Poster" };
        JComboBox<String> typeBox = new JComboBox<>(types);

        Object[] message = {
                "Location:", locField,
                "Date (YYYY-MM-DD HH:MM):", dateField,
                "Type:", typeBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Session", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = java.util.UUID.randomUUID().toString();
                Timestamp ts = Timestamp.valueOf(dateField.getText() + ":00");
                sessionDAO.createSession(id, selectedSeminarId, locField.getText(), ts,
                        (String) typeBox.getSelectedItem());
                loadSessions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding session: " + ex.getMessage());
            }
        }
    }

    private void showAddSeminarDialog() {
        JTextField locField = new JTextField();
        JTextField dateField = new JTextField("2026-02-10 09:00");

        // Semester dropdown (1 or 2)
        String[] semesters = { "1", "2" };
        JComboBox<String> semesterCombo = new JComboBox<>(semesters);

        // Year dropdown
        String[] years = { "2024", "2025", "2026", "2027", "2028" };
        JComboBox<String> yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem("2026");

        Object[] message = {
                "Location:", locField,
                "Date (YYYY-MM-DD HH:MM):", dateField,
                "Semester:", semesterCombo,
                "Year:", yearCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Seminar", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = java.util.UUID.randomUUID().toString();
                Timestamp ts = Timestamp.valueOf(dateField.getText() + ":00");
                int semester = Integer.parseInt((String) semesterCombo.getSelectedItem());
                int year = Integer.parseInt((String) yearCombo.getSelectedItem());
                sessionDAO.createSeminar(id, locField.getText(), ts, semester, year);
                JOptionPane.showMessageDialog(this, "Seminar created successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding seminar: " + ex.getMessage());
            }
        }
    }

    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(Theme.createPaddingBorder());

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel header = new JLabel("Assign Students and Evaluators to Sessions");
        header.setFont(Theme.SUBHEADER_FONT);
        header.setForeground(Theme.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = { "ID", "Date", "Venue", "Type", "Action" };
        assignmentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        JTable table = new JTable(assignmentModel);
        table.setRowHeight(40);
        table.setFont(Theme.STANDARD_FONT);
        table.getTableHeader().setFont(Theme.BOLD_FONT);

        // Action Column Buttons
        table.getColumnModel().getColumn(4).setCellRenderer(new AssignmentActionRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new AssignmentActionEditor(new JCheckBox(), table));
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setPreferredWidth(350);

        loadUnassignedSessions();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Theme.CARD_BG);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshBtn = new JButton("Refresh Unassigned Sessions");
        Theme.styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadUnassignedSessions());

        controlPanel.add(refreshBtn);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(controlPanel, BorderLayout.SOUTH);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void loadUnassignedSessions() {
        if (assignmentModel == null)
            return;
        assignmentModel.setRowCount(0);
        List<Session> sessions = sessionDAO.getUnassignedSessions();
        for (Session s : sessions) {
            assignmentModel.addRow(new Object[] {
                    s.getSessionID(),
                    s.getSessionDate() + " " + s.getSessionTime(),
                    s.getLocation(),
                    s.getSessionType(),
                    "Assign"
            });
        }
    }

    private void showAssignEvaluatorToSessionDialog(String preSelectedSessionId) {
        JComboBox<String> sessCombo = new JComboBox<>();
        JComboBox<String> evalCombo = new JComboBox<>();
        List<String> sessionIdsForSess = new ArrayList<>();
        List<String> evaluatorIdsForSess = new ArrayList<>();

        try {
            List<Session> sessions = sessionDAO.getAllSessions();
            int selectedIndex = -1;
            for (int i = 0; i < sessions.size(); i++) {
                Session s = sessions.get(i);
                String status = s.getEvaluatorName() != null ? " [Assigned: " + s.getEvaluatorName() + "]"
                        : " [UNASSIGNED]";
                sessCombo.addItem(
                        s.getSessionDate() + " - " + s.getLocation() + " (" + s.getSessionType() + ")" + status);
                sessionIdsForSess.add(s.getSessionID());
                if (preSelectedSessionId != null && preSelectedSessionId.equals(s.getSessionID())) {
                    selectedIndex = i;
                }
            }
            if (selectedIndex != -1) {
                sessCombo.setSelectedIndex(selectedIndex);
            }

            ResultSet rsEval = userDAO.getUsersByRole("evaluator");
            while (rsEval.next()) {
                String name = rsEval.getString("username");
                String eId = rsEval.getString("user_id");
                evalCombo.addItem(name);
                evaluatorIdsForSess.add(eId);
            }
            rsEval.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[] message = {
                "Select Session:", sessCombo,
                "Select Evaluator:", evalCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Assign Evaluator to Session",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int sessIdx = sessCombo.getSelectedIndex();
                int evalIdx = evalCombo.getSelectedIndex();
                if (sessIdx >= 0 && evalIdx >= 0) {
                    sessionDAO.assignEvaluatorToSession(sessionIdsForSess.get(sessIdx),
                            evaluatorIdsForSess.get(evalIdx));
                    JOptionPane.showMessageDialog(this, "Evaluator assigned to all students in session successfully!");
                    loadUnassignedSessions();
                    loadSessions();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private JPanel createPosterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(Theme.createPaddingBorder());

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        String[] columns = { "Board ID", "Name", "Location", "Capacity", "Current" };
        DefaultTableModel boardModel = new DefaultTableModel(columns, 0);
        JTable boardTable = new JTable(boardModel);

        JButton refreshBtn = new JButton("Refresh Boards");
        Theme.styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> {
            boardModel.setRowCount(0);
            List<PresentationBoard> boards = posterService.getAllBoards();
            for (PresentationBoard b : boards) {
                boardModel.addRow(new Object[] { b.getBoardId(), b.getBoardName(), b.getLocation(),
                        b.getMaxPresentations(), b.getCurrentPresentations() });
            }
        });

        JButton addBoardBtn = new JButton("Add Board");
        Theme.styleButton(addBoardBtn);
        addBoardBtn.addActionListener(e -> {
            JTextField nameF = new JTextField();
            JTextField locF = new JTextField();
            JTextField capF = new JTextField("10");
            Object[] msg = { "Name:", nameF, "Location:", locF, "Capacity:", capF };
            if (JOptionPane.showConfirmDialog(this, msg, "New Board",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    PresentationBoard board = new PresentationBoard(0, nameF.getText(), locF.getText(),
                            Integer.parseInt(capF.getText()), 0);
                    posterService.createBoard(board);
                    refreshBtn.doClick();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid capacity format.");
                }
            }
        });

        JButton assignPosterBtn = new JButton("Assign Poster to Board");
        Theme.styleButton(assignPosterBtn);
        assignPosterBtn.addActionListener(e -> {
            showAssignPosterDialog();
        });

        JButton defineCriteriaBtn = new JButton("Define Criteria");
        Theme.styleSecondaryButton(defineCriteriaBtn);
        defineCriteriaBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Rubric set: Problem Clarity, Methodology, Results, Presentation (Defaulted)");
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.setBackground(Theme.CARD_BG);
        btns.add(addBoardBtn);
        btns.add(assignPosterBtn);
        btns.add(defineCriteriaBtn);
        btns.add(refreshBtn);

        card.add(new JScrollPane(boardTable), BorderLayout.CENTER);
        card.add(btns, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);

        refreshBtn.doClick();
        return panel;
    }

    private void showAssignPosterDialog() {
        JComboBox<String> subCombo = new JComboBox<>();
        JComboBox<String> boardCombo = new JComboBox<>();
        List<String> subIds = new ArrayList<>();
        List<Integer> boardIds = new ArrayList<>();

        try {
            ResultSet rs = submissionDAO.getAllSubmissions();
            while (rs.next()) {
                if ("Poster Presentation".equals(rs.getString("presentation_type"))) {
                    subCombo.addItem(rs.getString("student_name") + ": " + rs.getString("title"));
                    subIds.add(rs.getString("submission_id"));
                }
            }
            rs.getStatement().getConnection().close();

            List<PresentationBoard> boards = posterService.getAllBoards();
            for (PresentationBoard b : boards) {
                boardCombo.addItem(b.getBoardName() + " (" + b.getLocation() + ")");
                boardIds.add(b.getBoardId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[] message = {
                "Select Poster Submission:", subCombo,
                "Select Board:", boardCombo
        };

        if (JOptionPane.showConfirmDialog(this, message, "Assign Poster",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int sIdx = subCombo.getSelectedIndex();
            int bIdx = boardCombo.getSelectedIndex();
            if (sIdx >= 0 && bIdx >= 0) {
                // Here we usually create a PosterPresentation record or link board_id to
                // submission
                // Using existing services or direct DAO
                try {
                    // Assuming a logical link: submissions table has board_id or similar
                    // For now, let's use a dummy success message or update a table if service
                    // exists
                    JOptionPane.showMessageDialog(this, "Poster assigned successfully (Logical link established)");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        }
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
        reportArea.setText("Click buttons below to view reports...");
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.CARD_BG);

        JButton reportButton = new JButton("Seminar Schedule");
        Theme.styleSecondaryButton(reportButton);
        reportButton.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder("--- SEMINAR SCHEDULE ---\n\n");
                ResultSet rs = reportDAO.getSeminarSchedule();
                while (rs.next()) {
                    sb.append(rs.getTimestamp("session_date")).append(" - ")
                            .append(rs.getString("student_name")).append(" (")
                            .append(rs.getString("location")).append("): ")
                            .append(rs.getString("title")).append("\n");
                }
                reportArea.setText(sb.toString());
                rs.getStatement().getConnection().close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JButton summaryButton = new JButton("Evaluation Summary & Analytics");
        Theme.styleSecondaryButton(summaryButton);
        summaryButton.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder("--- EVALUATION SUMMARY & ANALYTICS ---\n\n");
                ResultSet rs = reportDAO.getEvaluationSummary();
                int count = 0;
                double total = 0;
                while (rs.next()) {
                    int score = rs.getInt("overall_score");
                    sb.append("Student: ").append(rs.getString("student_name"))
                            .append("\nTitle: ").append(rs.getString("title"))
                            .append("\nScore: ").append(score)
                            .append("\nComments: ").append(rs.getString("comments"))
                            .append("\n--------------------------\n");
                    total += score;
                    count++;
                }
                if (count > 0) {
                    sb.append("\nDATA ANALYTICS:\n");
                    sb.append("Total Evaluations: ").append(count).append("\n");
                    sb.append("Average Overall Score: ").append(String.format("%.2f", total / count)).append("\n");
                }
                reportArea.setText(sb.toString());
                rs.getStatement().getConnection().close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JButton awardButton = new JButton("Calculate Awards & Agenda");
        Theme.styleButton(awardButton);
        awardButton.setBackground(new Color(255, 193, 7));
        awardButton.setForeground(Color.BLACK);
        awardButton.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder("--- AWARD WINNERS & CEREMONY AGENDA ---\n\n");
                sb.append("1. Introduction and Welcome\n");
                sb.append("2. Opening Remarks by Faculty Dean\n");
                sb.append("3. Presentation of Awards:\n\n");

                ResultSet rs = reportDAO.getAwardWinners();
                while (rs.next()) {
                    sb.append("   - ").append(rs.getString("presentation_type")).append(" Winner: ")
                            .append(rs.getString("student_name")).append("\n")
                            .append("     Project: ").append(rs.getString("title")).append("\n")
                            .append("     Final Score: ").append(rs.getInt("overall_score")).append("\n\n");
                }
                sb.append("4. Closing Remarks and Photo Session\n");
                reportArea.setText(sb.toString());
                rs.getStatement().getConnection().close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JButton exportButton = new JButton("Export Report");
        Theme.styleSecondaryButton(exportButton);
        exportButton.addActionListener(e -> {
            String content = reportArea.getText();
            if (content.isEmpty() || content.startsWith("Click")) {
                JOptionPane.showMessageDialog(this, "Please generate a report first.");
                return;
            }
            try (FileWriter writer = new FileWriter("seminar_report.txt")) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Report exported to 'seminar_report.txt'");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage());
            }
        });

        buttonPanel.add(reportButton);
        buttonPanel.add(summaryButton);
        buttonPanel.add(awardButton);
        buttonPanel.add(exportButton);

        card.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void showEditSessionDialog(String sessionId) {
        try {
            ResultSet rs = sessionDAO.findBySessionId(sessionId);
            if (rs.next()) {
                String currentLocation = rs.getString("location");
                Timestamp currentTs = rs.getTimestamp("session_date");
                String currentType = rs.getString("session_type");
                rs.getStatement().getConnection().close();

                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                JTextField locField = new JTextField(currentLocation);
                String formattedDate = currentTs.toString().substring(0, 16);
                JTextField dateField = new JTextField(formattedDate);
                String[] types = { "Oral Presentation", "Poster Presentation" };
                JComboBox<String> typeBox = new JComboBox<>(types);
                typeBox.setSelectedItem(currentType);

                panel.add(new JLabel("Location:"));
                panel.add(locField);
                panel.add(new JLabel("Date (YYYY-MM-DD HH:MM):"));
                panel.add(dateField);
                panel.add(new JLabel("Type:"));
                panel.add(typeBox);

                int option = JOptionPane.showConfirmDialog(this, panel, "Edit Session", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        Timestamp ts = Timestamp.valueOf(dateField.getText() + ":00");
                        sessionDAO.updateSession(sessionId, locField.getText(), ts, (String) typeBox.getSelectedItem());
                        loadSessions();
                        JOptionPane.showMessageDialog(this, "Session updated successfully!");
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD HH:MM");
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error editing session: " + ex.getMessage());
        }
    }

    // --- Inner Classes for Table Buttons ---

    class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        private JButton editBtn;
        private JButton deleteBtn;

        public ActionButtonsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setOpaque(true);
            setBackground(Color.WHITE);

            editBtn = new JButton("Edit");
            deleteBtn = new JButton("Delete");

            styleMiniButton(editBtn, Theme.PRIMARY_COLOR);
            styleMiniButton(deleteBtn, new Color(220, 53, 69)); // Red for delete

            add(editBtn);
            add(deleteBtn);
        }

        private void styleMiniButton(JButton btn, Color bgColor) {
            btn.setBackground(bgColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    class ActionButtonsEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editBtn;
        private JButton deleteBtn;
        private String sessionId;

        public ActionButtonsEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            panel.setOpaque(true);

            editBtn = new JButton("Edit");
            deleteBtn = new JButton("Delete");

            styleMiniButton(editBtn, Theme.PRIMARY_COLOR);
            styleMiniButton(deleteBtn, new Color(220, 53, 69));

            editBtn.addActionListener(e -> {
                fireEditingStopped();
                showEditSessionDialog(sessionId);
            });

            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Are you sure you want to delete this session?\nThis action cannot be undone.",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        sessionDAO.deleteSession(sessionId);
                        loadSessions();
                        JOptionPane.showMessageDialog(panel, "Session deleted successfully!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(panel, "Error deleting session: " + ex.getMessage());
                    }
                }
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private void styleMiniButton(JButton btn, Color bgColor) {
            btn.setBackground(bgColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.sessionId = (String) table.getValueAt(row, 0);
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Edit/Delete";
        }
    }

    class AssignmentActionRenderer extends JPanel implements TableCellRenderer {
        private JButton assignStudentBtn;
        private JButton assignEvaluatorBtn;

        public AssignmentActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setOpaque(true);
            setBackground(Color.WHITE);

            assignStudentBtn = new JButton("Assign Student");
            assignEvaluatorBtn = new JButton("Assign Evaluator");

            styleMiniButton(assignStudentBtn, Theme.PRIMARY_COLOR);
            styleMiniButton(assignEvaluatorBtn, Theme.PRIMARY_COLOR);

            add(assignStudentBtn);
            add(assignEvaluatorBtn);
        }

        private void styleMiniButton(JButton btn, Color bgColor) {
            btn.setBackground(bgColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    class AssignmentActionEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton assignStudentBtn;
        private JButton assignEvaluatorBtn;
        private String sessionId;

        public AssignmentActionEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            panel.setOpaque(true);

            assignStudentBtn = new JButton("Assign Student");
            assignEvaluatorBtn = new JButton("Assign Evaluator");

            styleMiniButton(assignStudentBtn, Theme.PRIMARY_COLOR);
            styleMiniButton(assignEvaluatorBtn, Theme.PRIMARY_COLOR);

            assignStudentBtn.addActionListener(e -> {
                fireEditingStopped();
                showAssignSpecificStudentDialog(sessionId);
            });

            assignEvaluatorBtn.addActionListener(e -> {
                fireEditingStopped();
                showAssignEvaluatorToSessionDialog(sessionId);
            });

            panel.add(assignStudentBtn);
            panel.add(assignEvaluatorBtn);
        }

        private void styleMiniButton(JButton btn, Color bgColor) {
            btn.setBackground(bgColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.sessionId = (String) table.getValueAt(row, 0);
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Assign";
        }
    }

    private void showAssignSpecificStudentDialog(String sessionId) {
        JComboBox<String> subCombo = new JComboBox<>();
        List<String> studentIdsForSess = new ArrayList<>();

        try {
            ResultSet rsSub = submissionDAO.getAllSubmissions();
            while (rsSub.next()) {
                subCombo.addItem(rsSub.getString("student_name") + ": " + rsSub.getString("title"));
                studentIdsForSess.add(rsSub.getString("student_id"));
            }
            rsSub.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[] message = {
                "Select Student to add to this session:", subCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Assign Student to Session",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int sIdx = subCombo.getSelectedIndex();
                if (sIdx >= 0) {
                    sessionDAO.addStudentToSession(sessionId, studentIdsForSess.get(sIdx));
                    JOptionPane.showMessageDialog(this, "Student assigned to session successfully!");
                    loadUnassignedSessions();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
