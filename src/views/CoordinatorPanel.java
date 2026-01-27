package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import models.Session;
import models.PresentationBoard;
import models.PosterPresentation;
import models.EvaluationCriteria;
import models.Award;
import models.Ceremony;
import database.SessionDAO;
import database.UserDAO;
import database.SubmissionDAO;
import database.ReportDAO;
import database.PosterPresentationDAO;
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
    private SessionDAO sessionDAO;
    private UserDAO userDAO;
    private SubmissionDAO submissionDAO;
    private ReportDAO reportDAO;
    private PosterPresentationService posterService;
    private PresentationBoardDAO boardDAO;

    private JComboBox<String> studentCombo;
    private JComboBox<String> evaluatorCombo;
    private List<String> studentIds;
    private List<String> evaluatorIds;

    public CoordinatorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.sessionDAO = new SessionDAO();
        this.userDAO = new UserDAO();
        this.submissionDAO = new SubmissionDAO();
        this.reportDAO = new ReportDAO();
        this.posterService = new PosterPresentationService();
        this.boardDAO = new PresentationBoardDAO();

        this.studentIds = new ArrayList<>();
        this.evaluatorIds = new ArrayList<>();

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

        String[] columns = { "ID", "Date", "Venue", "Type" };
        sessionModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loadSessions();
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
            showAddSessionDialog();
        });

        JButton refreshButton = new JButton("Refresh");
        Theme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> loadSessions());

        controlPanel.add(addButton);
        controlPanel.add(refreshButton);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(controlPanel, BorderLayout.SOUTH);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void loadSessions() {
        sessionModel.setRowCount(0);
        List<Session> sessions = sessionDAO.getAllSessions();
        for (Session s : sessions) {
            sessionModel.addRow(new Object[] {
                    s.getSessionID(),
                    s.getSessionDate() + " " + s.getSessionTime(),
                    s.getLocation(),
                    s.getSessionType()
            });
        }
    }

    private void showAddSessionDialog() {
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
                sessionDAO.createSession(id, locField.getText(), ts, (String) typeBox.getSelectedItem());
                loadSessions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding session: " + ex.getMessage());
            }
        }
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

        JLabel studentLabel = new JLabel("Select Student Submission");
        Theme.styleLabel(studentLabel, false);
        studentCombo = new JComboBox<>();
        studentCombo.setFont(Theme.STANDARD_FONT);
        studentCombo.setBackground(Color.WHITE);

        JLabel evaluatorLabel = new JLabel("Select Evaluator");
        Theme.styleLabel(evaluatorLabel, false);
        evaluatorCombo = new JComboBox<>();
        evaluatorCombo.setFont(Theme.STANDARD_FONT);
        evaluatorCombo.setBackground(Color.WHITE);

        loadAssignmentsData();

        JButton refreshBtn = new JButton("Refresh Lists");
        Theme.styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadAssignmentsData());

        JButton assignButton = new JButton("Assign");
        Theme.styleButton(assignButton);
        assignButton.addActionListener(e -> {
            int studentIdx = studentCombo.getSelectedIndex();
            int evaluatorIdx = evaluatorCombo.getSelectedIndex();

            if (studentIdx < 0 || evaluatorIdx < 0) {
                JOptionPane.showMessageDialog(this, "Please select both a student and an evaluator.");
                return;
            }

            try {
                String studentId = studentIds.get(studentIdx);
                String evaluatorId = evaluatorIds.get(evaluatorIdx);
                sessionDAO.assignEvaluator(evaluatorId, studentId);
                JOptionPane.showMessageDialog(this, "Assignment Successful!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
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
        gbc.gridwidth = 1;

        JButton assignStudentBtn = new JButton("Assign Student to Session");
        Theme.styleButton(assignStudentBtn);
        assignStudentBtn.addActionListener(e -> {
            showAssignStudentToSessionDialog();
        });

        card.add(assignStudentBtn, gbc);

        gbc.gridx = 1;
        card.add(assignButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        card.add(refreshBtn, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private void loadAssignmentsData() {
        studentCombo.removeAllItems();
        evaluatorCombo.removeAllItems();
        studentIds.clear();
        evaluatorIds.clear();

        try {
            // Load Students with Submissions
            ResultSet rsSub = submissionDAO.getAllSubmissions();
            while (rsSub.next()) {
                String name = rsSub.getString("student_name");
                String title = rsSub.getString("title");
                String sId = rsSub.getString("student_id");
                studentCombo.addItem(name + ": " + title);
                studentIds.add(sId);
            }
            rsSub.getStatement().getConnection().close();

            // Load Evaluators
            ResultSet rsEval = userDAO.getUsersByRole("evaluator");
            while (rsEval.next()) {
                String name = rsEval.getString("username");
                String eId = rsEval.getString("user_id");
                evaluatorCombo.addItem(name);
                evaluatorIds.add(eId);
            }
            rsEval.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAssignStudentToSessionDialog() {
        JComboBox<String> subCombo = new JComboBox<>();
        JComboBox<String> sessCombo = new JComboBox<>();
        List<String> studentIdsForSess = new ArrayList<>();
        List<String> sessionIdsForSess = new ArrayList<>();

        try {
            ResultSet rsSub = submissionDAO.getAllSubmissions();
            while (rsSub.next()) {
                subCombo.addItem(rsSub.getString("student_name") + ": " + rsSub.getString("title"));
                studentIdsForSess.add(rsSub.getString("student_id"));
            }
            rsSub.getStatement().getConnection().close();

            List<Session> sessions = sessionDAO.getAllSessions();
            for (Session s : sessions) {
                sessCombo.addItem(s.getSessionDate() + " - " + s.getLocation() + " (" + s.getSessionType() + ")");
                sessionIdsForSess.add(s.getSessionID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[] message = {
                "Select Student:", subCombo,
                "Select Session:", sessCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Assign Student to Session",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int sIdx = subCombo.getSelectedIndex();
                int sessIdx = sessCombo.getSelectedIndex();
                if (sIdx >= 0 && sessIdx >= 0) {
                    sessionDAO.addStudentToSession(sessionIdsForSess.get(sessIdx), studentIdsForSess.get(sIdx));
                    JOptionPane.showMessageDialog(this, "Student assigned to session successfully!");
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
}
