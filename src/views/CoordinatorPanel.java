package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.TableModelListener;
// import javax.swing.event.TableModelEvent; // Removed unused import
import java.awt.*;

import models.Submission;
import models.Session;
import models.PresentationBoard;
import models.PosterPresentation;
import database.DatabaseConnection;
import database.SessionDAO;
import database.UserDAO;
import database.SubmissionDAO;
import database.ReportDAO;
import services.PosterPresentationService;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID; // Added missing import

public class CoordinatorPanel extends JPanel {
    private DefaultTableModel sessionModel;
    private DefaultTableModel assignmentModel;
    private SessionDAO sessionDAO;
    private UserDAO userDAO;
    private SubmissionDAO submissionDAO;
    private ReportDAO reportDAO;
    private PosterPresentationService posterService;
    private DefaultTableModel boardModel;
    private JButton posterRefreshBtn;

    private JComboBox<String> sessionSeminarCombo;
    private JComboBox<String> reportSeminarCombo;
    private List<String> seminarIds;

    public CoordinatorPanel(MainFrame mainFrame) {
        this.sessionDAO = new SessionDAO();
        this.userDAO = new UserDAO();
        this.submissionDAO = new SubmissionDAO();
        this.reportDAO = new ReportDAO();
        this.posterService = new PosterPresentationService();

        this.seminarIds = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Coordinator Dashboard (Updated)");
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
        sessionSeminarCombo = new JComboBox<>();
        sessionSeminarCombo.setFont(Theme.STANDARD_FONT);
        sessionSeminarCombo.setPreferredSize(new Dimension(350, 30));
        loadSeminars();

        sessionSeminarCombo.addActionListener(e -> {
            // When seminar is selected, reload sessions for that seminar
            loadSessions();
        });

        seminarSelectorPanel.add(seminarLabel);
        seminarSelectorPanel.add(sessionSeminarCombo);

        String[] columns = { "ID", "Date", "Venue", "Type", "Action" };
        sessionModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        loadSessions();
        JTable table = new JTable(sessionModel);
        table.setRowHeight(35); // Slightly taller for buttons
        table.setFont(Theme.STANDARD_FONT);
        table.getTableHeader().setFont(Theme.BOLD_FONT);
        table.getTableHeader().setBackground(Theme.UNVERIFIED_BG);

        // Action Column Buttons
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionButtonsRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionButtonsEditor(new JCheckBox(), table));
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Hide ID column but keep data
        table.getColumnModel().getColumn(4).setPreferredWidth(170); // Wider for two buttons

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
        int idx = sessionSeminarCombo != null ? sessionSeminarCombo.getSelectedIndex() : -1;
        List<Session> sessions;
        if (idx >= 0 && idx < seminarIds.size()) {
            String selectedSeminarId = seminarIds.get(idx);
            sessions = sessionDAO.getSessionsBySeminar(selectedSeminarId);
        } else {
            sessions = sessionDAO.getAllSessions();
        }
        for (Session s : sessions) {
            sessionModel.addRow(new Object[] {
                    s.getSessionID(),
                    s.getSessionDate() + " " + s.getSessionTime(),
                    s.getLocation(),
                    s.getSessionType(),
                    "Edit"
            });
        }
    }

    public void loadSeminars() {
        if (sessionSeminarCombo != null)
            sessionSeminarCombo.removeAllItems();
        if (reportSeminarCombo != null)
            reportSeminarCombo.removeAllItems();
        seminarIds.clear();
        try {
            ResultSet rs = sessionDAO.getAllSeminars();
            while (rs.next()) {
                String seminarId = rs.getString("seminar_id");
                String location = rs.getString("location");
                int semester = rs.getInt("semester");
                int year = rs.getInt("year");
                String displayText = "Semester " + semester + " " + year + " - " + location;
                if (sessionSeminarCombo != null)
                    sessionSeminarCombo.addItem(displayText);
                if (reportSeminarCombo != null)
                    reportSeminarCombo.addItem(displayText);
                seminarIds.add(seminarId);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddSessionDialog() {
        // Check if a seminar is selected
        int seminarIdx = sessionSeminarCombo != null ? sessionSeminarCombo.getSelectedIndex() : -1;
        if (seminarIdx < 0 || seminarIdx >= seminarIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a seminar first.");
            return;
        }
        String selectedSeminarId = seminarIds.get(seminarIdx);

        JTextField locField = new JTextField();
        JTextField dateField = new JTextField("2026-02-10 09:00");
        String[] types = { "Oral Presentation", "Poster Presentation" };
        JComboBox<String> typeBox = new JComboBox<>(types);

        Object[] message = {
                "Location:", locField,
                "Date (YYYY-MM-DD HH:MM):", dateField,
                "Type:", typeBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Session", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = UUID.randomUUID().toString();
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
                String id = UUID.randomUUID().toString();
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

        JLabel header = new JLabel("Manage Students and Evaluators for All Sessions");
        header.setFont(Theme.SUBHEADER_FONT);
        header.setForeground(Theme.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = { "ID", "Date", "Venue", "Type", "Management" };
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
        List<Session> sessions = sessionDAO.getAllSessions();
        for (Session s : sessions) {
            // We could add student counts here if needed, but for now just show basic info
            assignmentModel.addRow(new Object[] {
                    s.getSessionID(),
                    s.getSessionDate() + " " + s.getSessionTime(),
                    s.getLocation(),
                    s.getSessionType(),
                    "Manage"
            });
        }
    }

    private void showManageAssignmentsDialog(String sessionId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Session Assignments",
                true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 600); // Made it slightly wider for the split pane
        dialog.setLocationRelativeTo(this);

        // Directly show the Assignments Overview (Assignment Form + Table)
        JPanel contentPanel = createAssignmentsOverviewPanel(sessionId);
        dialog.add(contentPanel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close & Refresh");
        Theme.styleButton(closeBtn);
        closeBtn.addActionListener(e -> {
            dialog.dispose();
            loadUnassignedSessions();
            loadSessions();
        });

        JPanel south = new JPanel();
        south.add(closeBtn);
        dialog.add(south, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Removed unused createManageStudentsPanel and createManageEvaluatorsPanel
    // methods
    // ... (Use multi_replace to remove them if I want to be clean, but for now
    // focus on the requested changes.
    // Actually, I should probably leave them or remove them? The prompt asks to
    // remove the *tabs*, not necessarily delete the code, but cleaning is good.
    // I will just replace the dialog method and the helper function here.

    // Helper to extract ID from display string (e.g., "username (ID: uuid)")
    private String extractIdFromDisplayString(String displayString) {
        int startIndex = displayString.lastIndexOf("(ID: ");
        if (startIndex != -1) {
            int endIndex = displayString.lastIndexOf(")");
            // "(ID: " is 5 chars long.
            if (endIndex != -1 && endIndex > startIndex + 5) {
                return displayString.substring(startIndex + 5, endIndex).trim();
            }
        }
        return null;
    }

    private void refreshStudentList(String sessionId, DefaultListModel<String> model) {
        model.clear();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT u.username, u.user_id FROM session_students ss JOIN users u ON ss.student_id = u.user_id WHERE ss.session_id = ?::uuid")) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getString("username") + " (ID: " + rs.getString("user_id") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshEvaluatorList(String sessionId, DefaultListModel<String> model) {
        model.clear();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT u.username, u.user_id FROM session_evaluators se JOIN users u ON se.evaluator_id = u.user_id WHERE se.session_id = ?::uuid")) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getString("username") + " (ID: " + rs.getString("user_id") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddStudentToSessionDialog(String sessionId, DefaultListModel<String> listModel) {
        JComboBox<String> studentCombo = new JComboBox<>();
        List<String> studentIds = new ArrayList<>();
        try {
            // Get all students who have submitted, and are not already in this session
            List<models.Submission> submissions = submissionDAO.getAllSubmissionsList();
            List<String> currentStudentIdsInSession = sessionDAO.getStudentIdsInSession(sessionId);

            for (models.Submission s : submissions) {
                if (!currentStudentIdsInSession.contains(s.getStudentId())) {
                    studentCombo
                            .addItem("[" + s.getPresentationType() + "] " + s.getStudentName() + ": " + s.getTitle());
                    studentIds.add(s.getStudentId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available students to add to this session.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, studentCombo, "Add Student to Session",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION && studentCombo.getSelectedIndex() >= 0) {
            try {
                sessionDAO.addStudentToSession(sessionId, studentIds.get(studentCombo.getSelectedIndex()));
                refreshStudentList(sessionId, listModel);
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void showAddEvaluatorToSessionDialog(String sessionId, DefaultListModel<String> listModel) {
        JComboBox<String> evalCombo = new JComboBox<>();
        List<String> evalIds = new ArrayList<>();
        try {
            // Get all evaluators not already assigned to this session
            List<String> currentEvaluatorIdsInSession = sessionDAO.getEvaluatorIdsInSession(sessionId);
            ResultSet rs = userDAO.getUsersByRole("evaluator");
            while (rs.next()) {
                String evaluatorId = rs.getString("user_id");
                if (!currentEvaluatorIdsInSession.contains(evaluatorId)) {
                    evalCombo.addItem(rs.getString("username"));
                    evalIds.add(evaluatorId);
                }
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (evalIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available evaluators to add to this session.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, evalCombo, "Add Evaluator to Session",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION && evalCombo.getSelectedIndex() >= 0) {
            try {
                sessionDAO.addEvaluatorToSession(sessionId, evalIds.get(evalCombo.getSelectedIndex()));
                refreshEvaluatorList(sessionId, listModel);
                JOptionPane.showMessageDialog(this, "Evaluator added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
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

        String[] columns = { "Board ID", "Location", "Assigned Student", "Session", "Type" };
        boardModel = new DefaultTableModel(columns, 0);
        JTable boardTable = new JTable(boardModel);
        boardTable.setRowHeight(30);
        boardTable.setFont(Theme.STANDARD_FONT);
        boardTable.getTableHeader().setFont(Theme.BOLD_FONT);

        posterRefreshBtn = new JButton("Refresh Boards");
        Theme.styleSecondaryButton(posterRefreshBtn);
        posterRefreshBtn.addActionListener(e -> {
            boardModel.setRowCount(0);
            List<PresentationBoard> boards = posterService.getAllBoards();
            for (PresentationBoard b : boards) {
                String studentName = posterService.getStudentNameForBoard(b.getBoardId());
                boardModel.addRow(new Object[] {
                        b.getBoardName(),
                        b.getLocation(),
                        studentName,
                        b.getSessionId(),
                        b.getPresentationType()
                });
            }
        });

        JButton addBoardBtn = new JButton("Add New Board");
        Theme.styleButton(addBoardBtn);
        addBoardBtn.addActionListener(e -> {
            JTextField idF = new JTextField();
            String[] locations = {
                    "North Wing", "South Wing", "East Wing", "West Wing",
                    "Main Lobby", "Exhibition Hall A", "Exhibition Hall B",
                    "Level 1 Corridor", "Level 2 Corridor", "Outdoor Plaza",
                    "Library Atrium", "Science Block Foyer"
            };
            JComboBox<String> locCombo = new JComboBox<>(locations);

            // Session Dropdown
            JComboBox<String> sessionBox = new JComboBox<>();
            List<String> sessionIds = new ArrayList<>();
            List<models.Session> sessions = sessionDAO.getAllSessions();
            for (models.Session s : sessions) {
                // Filter for poster sessions if possible, or show all
                if (s.getSessionType() != null && s.getSessionType().toLowerCase().contains("poster")) {
                    sessionBox.addItem(s.getSessionType() + " (" + s.getLocation() + ")");
                    sessionIds.add(s.getSessionID());
                }
            }
            if (sessionIds.isEmpty()) {
                // Fallback if no poster sessions found for testing
                for (models.Session s : sessions) {
                    sessionBox.addItem(s.getSessionType() + " (" + s.getLocation() + ")");
                    sessionIds.add(s.getSessionID());
                }
            }

            // Type Dropdown
            String[] types = { "A1 Portrait", "A1 Landscape", "A0 Portrait", "Digital Screen" };
            JComboBox<String> typeBox = new JComboBox<>(types);

            Object[] msg = {
                    "Board ID (e.g., P1, P2):", idF,
                    "Select Location:", locCombo,
                    "Select Session:", sessionBox,
                    "Board Type:", typeBox
            };
            if (JOptionPane.showConfirmDialog(this, msg, "Add New Board",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                if (idF.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Board ID is required.");
                    return;
                }
                try {
                    String selectedLoc = (String) locCombo.getSelectedItem();
                    int sessIdx = sessionBox.getSelectedIndex();
                    String selectedSessionId = (sessIdx >= 0 && sessIdx < sessionIds.size()) ? sessionIds.get(sessIdx)
                            : null;
                    String selectedType = (String) typeBox.getSelectedItem();

                    // Default capacity to 1 as per requirements (one student per board)
                    PresentationBoard board = new PresentationBoard(0, idF.getText(), selectedLoc, 1, 0,
                            selectedSessionId, selectedType);
                    posterService.createBoard(board);
                    posterRefreshBtn.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
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
        btns.add(posterRefreshBtn);

        card.add(new JScrollPane(boardTable), BorderLayout.CENTER);
        card.add(btns, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);

        posterRefreshBtn.doClick();
        return panel;
    }

    private void showAssignPosterDialog() {
        JComboBox<String> subCombo = new JComboBox<>();
        JComboBox<String> boardCombo = new JComboBox<>();
        JComboBox<String> sessionCombo = new JComboBox<>();
        List<String> subIds = new ArrayList<>();
        List<String> subTitles = new ArrayList<>();
        List<Integer> boardIds = new ArrayList<>();
        List<String> posterSessionIds = new ArrayList<>();

        List<Submission> allSubmissions = submissionDAO.getAllSubmissionsList();
        for (Submission s : allSubmissions) {
            String studentName = s.getStudentName();
            String title = s.getTitle();
            String type = s.getPresentationType();

            // Show all submitted students, but keep type clear
            subCombo.addItem("[" + type + "] " + studentName + ": " + title);
            subIds.add(s.getId());
            subTitles.add(title);
        }

        try {
            List<PresentationBoard> boards = posterService.getAllBoards();
            for (PresentationBoard b : boards) {
                String assigned = posterService.isBoardAssigned(b.getBoardId()) ? " [ASSIGNED]" : "";
                boardCombo.addItem(b.getBoardName() + " (" + b.getLocation() + ")" + assigned);
                boardIds.add(b.getBoardId());
            }

            List<Session> allSessions = sessionDAO.getAllSessions();
            for (Session s : allSessions) {
                if (s.getSessionType() != null && s.getSessionType().toLowerCase().contains("poster")) {
                    sessionCombo.addItem(s.getSessionType() + " at " + s.getLocation());
                    posterSessionIds.add(s.getSessionID());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[] message = {
                "Select Student / Poster Submission:", subCombo,
                "Select Board ID:", boardCombo,
                "Select Poster Session:", sessionCombo
        };

        if (JOptionPane.showConfirmDialog(this, message, "Assign Student to Board",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int sIdx = subCombo.getSelectedIndex();
            int bIdx = boardCombo.getSelectedIndex();
            if (sIdx >= 0 && bIdx >= 0) {
                int selectedBoardId = boardIds.get(bIdx);
                String selectedSubId = subIds.get(sIdx);
                String selectedTitle = subTitles.get(sIdx);

                if (posterService.isBoardAssigned(selectedBoardId)) {
                    JOptionPane.showMessageDialog(this, "Error: This board is already assigned to a student.");
                    return;
                }

                try {
                    int sessIdx = sessionCombo.getSelectedIndex();
                    String sessionId = null;
                    if (sessIdx >= 0) {
                        sessionId = posterSessionIds.get(sessIdx);
                    }

                    if (sessionId == null) {
                        JOptionPane.showMessageDialog(this,
                                "Error: No Poster Session selected. Please select a valid Poster session.");
                        return;
                    }

                    PosterPresentation pp = new PosterPresentation(0, selectedBoardId, selectedSubId,
                            selectedTitle, "", sessionId, "ASSIGNED");

                    if (posterService.addPresentation(pp)) {
                        JOptionPane.showMessageDialog(this, "Student assigned to board successfully!");
                        if (posterRefreshBtn != null) {
                            posterRefreshBtn.doClick();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to assign student to board.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        }
    }

    private int currentEditPart = 0; // 1: Schedule, 2: Summary, 3: Awards
    private String lastSchedule = "";
    private String lastSummary = "";
    private String lastAwards = "";
    private boolean scheduleDone = false;
    private boolean summaryDone = false;
    private boolean awardsDone = false;

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
        reportArea.setText("--- REPORT GENERATION STEPS ---\n" +
                "1. Click 'Seminar Schedule' to prepare part 1.\n" +
                "2. Click 'Evaluation Summary' to prepare part 2.\n" +
                "3. Click 'Calculate Awards' to prepare part 3.\n" +
                "4. After all 3 steps, click 'Save & Compile' to finalize.\n" +
                "5. Finally, click 'Export Report' to save to file.");
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel seminarSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seminarSelectorPanel.setBackground(Theme.CARD_BG);
        seminarSelectorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel seminarLabel = new JLabel("Select Seminar for Report:");
        seminarLabel.setFont(Theme.BOLD_FONT);
        reportSeminarCombo = new JComboBox<>();
        reportSeminarCombo.setFont(Theme.STANDARD_FONT);
        reportSeminarCombo.setPreferredSize(new Dimension(350, 30));
        loadSeminars();

        JLabel statusLabel = new JLabel("Status: Steps Pending");
        statusLabel.setForeground(Color.RED);

        seminarSelectorPanel.add(seminarLabel);
        seminarSelectorPanel.add(reportSeminarCombo);
        seminarSelectorPanel.add(statusLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Theme.CARD_BG);

        JButton exportButton = new JButton("5. Export Report");
        Theme.styleSecondaryButton(exportButton);
        exportButton.setEnabled(false);

        JButton contextualEditBtn = new JButton("Edit Content");
        Theme.styleSecondaryButton(contextualEditBtn);
        contextualEditBtn.setEnabled(false);
        contextualEditBtn.setPreferredSize(new Dimension(150, 30));

        JButton compileButton = new JButton("4. Save & Compile Report");
        Theme.styleButton(compileButton);
        compileButton.setEnabled(false);

        JButton reportButton = new JButton("1. Seminar Schedule");
        Theme.styleSecondaryButton(reportButton);
        reportButton.addActionListener(e -> {
            int idx = reportSeminarCombo.getSelectedIndex();
            if (idx < 0)
                return;
            String semId = seminarIds.get(idx);
            try {
                StringBuilder sb = new StringBuilder("--- SEMINAR SCHEDULE ---\n\n");
                ResultSet rs = reportDAO.getSeminarSchedule(semId);
                String currentSessionId = null;
                while (rs.next()) {
                    String sessionId = rs.getString("session_id");
                    java.sql.Timestamp sessionDate = rs.getTimestamp("session_date");
                    String location = rs.getString("location");

                    String studentName = rs.getString("student_name");
                    String pType = rs.getString("presentation_type");
                    String evalName = rs.getString("evaluator_name");
                    String boardName = rs.getString("board_name");
                    String title = rs.getString("title");

                    // New session header
                    if (!sessionId.equals(currentSessionId)) {
                        if (currentSessionId != null) {
                            sb.append("\n"); // Add spacing between sessions
                        }
                        currentSessionId = sessionId;
                        sb.append("==========================================\n");
                        sb.append("SESSION: ").append(sessionDate).append("\n");
                        sb.append("Location: ").append(location).append("\n");
                        sb.append("==========================================\n");
                    }

                    // Student details under session
                    if (studentName != null) {
                        sb.append("  [").append(pType != null ? pType : "N/A").append("] ").append(studentName)
                                .append("\n")
                                .append("     Title:     ").append(title).append("\n")
                                .append("     Evaluator: ").append(evalName != null ? evalName : "Not Assigned")
                                .append("\n");
                        if (boardName != null) {
                            sb.append("     Poster:    ").append(boardName).append("\n");
                        }
                        sb.append("  ------------------------------------------\n");
                    } else {
                        sb.append("  [No students assigned to this session]\n");
                    }
                }
                if (currentSessionId == null) {
                    sb.append("No sessions found for this seminar.\n\n");
                    sb.append("DEBUG INFO:\n");
                    sb.append("Seminar ID: ").append(semId).append("\n");
                    sb.append("\nPlease ensure sessions are created in 'Manage Sessions' tab.\n");
                }
                lastSchedule = sb.toString();
                reportArea.setText(lastSchedule);
                reportArea.setEditable(false);
                scheduleDone = true;
                currentEditPart = 1;
                contextualEditBtn.setText("Edit Schedule");
                contextualEditBtn.setEnabled(true);
                contextualEditBtn.setBackground(Color.WHITE);
                rs.getStatement().getConnection().close();
                if (scheduleDone && summaryDone && awardsDone)
                    compileButton.setEnabled(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JButton summaryButton = new JButton("2. Evaluation Summary");
        Theme.styleSecondaryButton(summaryButton);
        summaryButton.addActionListener(e -> {
            int idx = reportSeminarCombo.getSelectedIndex();
            if (idx < 0)
                return;
            String semId = seminarIds.get(idx);
            try {
                StringBuilder sb = new StringBuilder("--- EVALUATION SUMMARY & ANALYTICS ---\n\n");
                ResultSet rs = reportDAO.getEvaluationSummary(semId);
                int count = 0;
                int evaluatedCount = 0;
                double total = 0;
                while (rs.next()) {
                    int score = rs.getInt("overall_score");
                    boolean isEvaluated = !rs.wasNull();
                    String evalName = rs.getString("evaluator_name");
                    String boardName = rs.getString("board_name");

                    sb.append("Student:   ").append(rs.getString("student_name"))
                            .append(" (").append(rs.getString("presentation_type")).append(")\n");

                    if (!isEvaluated) {
                        sb.append("STATUS:    [PENDING EVALUATION]\n");
                        sb.append("Evaluator: ").append(evalName != null ? evalName : "Not Assigned").append("\n");
                    } else {
                        sb.append("Evaluator: ").append(evalName != null ? evalName : "Unknown").append("\n");
                        if (boardName != null) {
                            sb.append("Poster:    ").append(boardName).append("\n");
                        }
                        sb.append("Title:     ").append(rs.getString("title")).append("\n")
                                .append("Detailed Scores:\n")
                                .append("   - Problem Clarity: ").append(rs.getInt("problem_clarity")).append("/10\n")
                                .append("   - Methodology:     ").append(rs.getInt("methodology")).append("/10\n")
                                .append("   - Results:         ").append(rs.getInt("results")).append("/10\n")
                                .append("   - Presentation:    ").append(rs.getInt("presentation")).append("/10\n")
                                .append("OVERALL SCORE: ").append(score).append("/100\n")
                                .append("Comments: ").append(rs.getString("comments")).append("\n");
                        total += score;
                        evaluatedCount++;
                    }
                    sb.append("------------------------------------------\n");
                    count++;
                }
                if (count > 0) {
                    sb.append("\nDATA ANALYTICS:\n");
                    sb.append("Total Students: ").append(count).append("\n");
                    sb.append("Evaluated:      ").append(evaluatedCount).append("\n");
                    if (evaluatedCount > 0) {
                        sb.append("Average Score:  ").append(String.format("%.2f", total / evaluatedCount))
                                .append("\n");
                    }
                }
                lastSummary = sb.toString();
                reportArea.setText(lastSummary);
                reportArea.setEditable(false);
                summaryDone = true;
                currentEditPart = 2;
                contextualEditBtn.setText("Edit Summary");
                contextualEditBtn.setEnabled(true);
                contextualEditBtn.setBackground(Color.WHITE);
                rs.getStatement().getConnection().close();
                if (scheduleDone && summaryDone && awardsDone)
                    compileButton.setEnabled(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JButton awardButton = new JButton("3. Calculate Awards");
        Theme.styleButton(awardButton);

        // Contextual Edit logic
        contextualEditBtn.addActionListener(e -> {
            boolean editing = reportArea.isEditable();
            if (editing) {
                // Save
                String text = reportArea.getText();
                if (currentEditPart == 1)
                    lastSchedule = text;
                else if (currentEditPart == 2)
                    lastSummary = text;
                else if (currentEditPart == 3)
                    lastAwards = text;

                reportArea.setEditable(false);
                String label = (currentEditPart == 1) ? "Edit Schedule"
                        : (currentEditPart == 2) ? "Edit Summary" : "Edit Awards";
                contextualEditBtn.setText(label);
                contextualEditBtn.setBackground(Color.WHITE);
            } else {
                // Start Edit
                reportArea.setEditable(true);
                reportArea.requestFocus();
                contextualEditBtn.setText("Save Changes");
                contextualEditBtn.setBackground(new Color(255, 235, 235));
            }
        });
        awardButton.setBackground(new Color(255, 193, 7));
        awardButton.setForeground(Color.BLACK);
        awardButton.addActionListener(e -> {
            int idx = reportSeminarCombo.getSelectedIndex();
            if (idx < 0)
                return;
            String semId = seminarIds.get(idx);
            try {
                StringBuilder sb = new StringBuilder("--- AWARD WINNERS & CEREMONY AGENDA ---\n\n");
                sb.append("1. Introduction and Welcome\n");
                sb.append("2. Opening Remarks by Faculty Dean\n");
                sb.append("3. Presentation of Awards:\n\n");

                ResultSet rs = reportDAO.getAwardWinners(semId);
                while (rs.next()) {
                    sb.append("- ").append(rs.getString("award_category")).append(": ")
                            .append(rs.getString("student_name")).append(" - ")
                            .append(rs.getString("title")).append(" (Score: ")
                            .append(rs.getInt("overall_score")).append(")\n");
                }
                sb.append("\n4. Keynote Presentation by Winners\n");
                sb.append("5. Closing Ceremony and Refreshments\n");
                lastAwards = sb.toString();
                reportArea.setText(lastAwards);
                reportArea.setEditable(false);
                awardsDone = true;
                currentEditPart = 3;
                contextualEditBtn.setText("Edit Awards");
                contextualEditBtn.setEnabled(true);
                contextualEditBtn.setBackground(Color.WHITE);
                rs.getStatement().getConnection().close();
                if (scheduleDone && summaryDone && awardsDone)
                    compileButton.setEnabled(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        compileButton.addActionListener(e -> {
            StringBuilder finalReport = new StringBuilder();
            finalReport.append("==========================================\n");
            finalReport.append("   OFFICIAL SEMINAR COMPREHENSIVE REPORT  \n");
            finalReport.append("==========================================\n\n");
            finalReport.append(lastSchedule).append("\n\n");
            finalReport.append(lastSummary).append("\n\n");
            finalReport.append(lastAwards).append("\n\n");
            finalReport.append("==========================================\n");
            finalReport.append("Report Generated on: ").append(new java.util.Date()).append("\n");

            reportArea.setText(finalReport.toString());
            statusLabel.setText("Status: Report Compiled!");
            statusLabel.setForeground(new Color(0, 150, 0));
            exportButton.setEnabled(true);
            Theme.styleButton(exportButton);
            JOptionPane.showMessageDialog(this,
                    "Report Compiled Successfully!\nYou can now click '5. Export Report' to save the report.");
        });

        exportButton.addActionListener(e -> {
            String content = reportArea.getText();
            try (FileWriter writer = new FileWriter("seminar_report.txt")) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Complete Report exported to 'seminar_report.txt'");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage());
            }
        });

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Theme.CARD_BG);

        JPanel stepRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        stepRow1.setBackground(Theme.CARD_BG);
        stepRow1.add(reportButton);
        stepRow1.add(summaryButton);
        stepRow1.add(awardButton);
        stepRow1.add(compileButton);

        JPanel stepRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        stepRow2.setBackground(Theme.CARD_BG);
        stepRow2.add(exportButton);

        footerPanel.add(stepRow1, BorderLayout.NORTH);
        footerPanel.add(stepRow2, BorderLayout.SOUTH);

        // Edit button overlay/sub-panel
        JPanel editOverlayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        editOverlayPanel.setBackground(Theme.CARD_BG);
        editOverlayPanel.add(contextualEditBtn);

        card.add(seminarSelectorPanel, BorderLayout.NORTH);
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        centerContainer.add(editOverlayPanel, BorderLayout.SOUTH);

        card.add(centerContainer, BorderLayout.CENTER);
        card.add(footerPanel, BorderLayout.SOUTH);

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
        private JButton manageBtn;

        public AssignmentActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setOpaque(true);
            setBackground(Color.WHITE);

            manageBtn = new JButton("Assign Student & Evaluator");

            styleMiniButton(manageBtn, Theme.PRIMARY_COLOR);

            add(manageBtn);
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
        private JButton manageBtn;
        private String sessionId;

        public AssignmentActionEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            panel.setOpaque(true);

            manageBtn = new JButton("Assign Student & Evaluator");
            styleMiniButton(manageBtn, Theme.PRIMARY_COLOR);

            manageBtn.addActionListener(e -> {
                fireEditingStopped();
                showManageAssignmentsDialog(sessionId);
            });

            panel.add(manageBtn);
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
            return "Assign Student & Evaluator";
        }
    }

    private JPanel createAssignmentsOverviewPanel(String sessionId) {
        JPanel panel = new JPanel(new BorderLayout());

        // --- LEFT SIDE: Assignment Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.CARD_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(Theme.BOLD_FONT);
        JComboBox<String> studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(250, 30));

        JLabel evaluatorLabel = new JLabel("Select Evaluator:");
        evaluatorLabel.setFont(Theme.BOLD_FONT);
        JComboBox<String> evaluatorCombo = new JComboBox<>();
        evaluatorCombo.setPreferredSize(new Dimension(250, 30));

        JButton saveBtn = new JButton("Save Assignment");
        Theme.styleButton(saveBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(studentLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(studentCombo, gbc);
        gbc.gridy = 2;
        formPanel.add(evaluatorLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(evaluatorCombo, gbc);
        gbc.gridy = 4;
        gbc.ipady = 10;
        formPanel.add(saveBtn, gbc);

        // Spacer to push components up
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        formPanel.add(new JPanel() {
            {
                setOpaque(false);
            }
        }, gbc);

        // --- RIGHT SIDE: Table ---
        String[] columns = { "Student", "Submission Title", "Assigned Evaluator", "StudentId" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make read-only as we have the form now
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(Theme.BOLD_FONT);
        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);

        JScrollPane tableScroll = new JScrollPane(table);

        // --- DATA LOADING & ACTIONS ---
        Runnable refreshData = () -> {
            model.setRowCount(0);
            studentCombo.removeAllItems();
            evaluatorCombo.removeAllItems();

            try {
                // 1. Load Table Data (Current Assignments)
                List<String[]> overview = sessionDAO.getAssignmentsOverview(sessionId);
                for (String[] row : overview) {
                    model.addRow(new Object[] { row[0], row[1], row[2], row[4] });
                }

                // 2. Load Combobox Data (ALL Students & Evaluators)

                // Load ALL Submissions (Students)
                List<models.Submission> allSubmissions = submissionDAO.getAllSubmissionsList();
                for (models.Submission s : allSubmissions) {
                    // Format: [Type] Name: Title (ID: uuid)
                    studentCombo.addItem("[" + s.getPresentationType() + "] " + s.getStudentName() + ": " + s.getTitle()
                            + " (ID: " + s.getStudentId() + ")");
                }

                // Load ALL Evaluators
                ResultSet rs = userDAO.getUsersByRole("evaluator");
                while (rs.next()) {
                    evaluatorCombo.addItem(rs.getString("username") + " (ID: " + rs.getString("user_id") + ")");
                }
                rs.getStatement().getConnection().close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(panel, "Error loading data: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Initial Load
        refreshData.run();

        // Save Action
        saveBtn.addActionListener(e -> {
            String selectedStudentRaw = (String) studentCombo.getSelectedItem();
            String selectedEvaluatorRaw = (String) evaluatorCombo.getSelectedItem();

            if (selectedStudentRaw == null || selectedEvaluatorRaw == null) {
                JOptionPane.showMessageDialog(panel, "Please select both a student and an evaluator.");
                return;
            }

            String studentId = extractIdFromDisplayString(selectedStudentRaw);
            String evaluatorId = extractIdFromDisplayString(selectedEvaluatorRaw);

            if (studentId != null && evaluatorId != null) {
                try {
                    // Auto-Add Logic: Ensure they are in the session first
                    sessionDAO.addStudentToSession(sessionId, studentId);
                    sessionDAO.addEvaluatorToSession(sessionId, evaluatorId);

                    // Now Assign
                    sessionDAO.updateStudentEvaluator(sessionId, studentId, evaluatorId);
                    JOptionPane.showMessageDialog(panel, "Assignment Saved Successfully!");
                    refreshData.run(); // Refresh UI
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Error saving assignment: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Error parsing IDs. Please try again.");
            }
        });

        // --- SPLIT PANE ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tableScroll);
        splitPane.setDividerLocation(300); // Give form some space
        splitPane.setResizeWeight(0.0); // Form stays fixed ish
        splitPane.setOneTouchExpandable(true);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }
}
