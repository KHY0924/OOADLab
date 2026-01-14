package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.User;
import models.Session;
import models.Material;
import database.SubmissionDAO;
import database.SessionDAO;
import database.EvaluationDAO;
import database.MaterialDAO;

public class StudentPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField titleField;
    private JTextArea abstractArea;
    private JComboBox<String> supervisorField;
    private JComboBox<String> typeCombo;
    private JComboBox<String> sessionCombo;

    // Status Components
    private JTable statusTable;
    private DefaultTableModel statusModel;

    // Material Components
    private JTable materialTable;
    private DefaultTableModel materialModel;

    // Data Access Objects
    private SubmissionDAO submissionDAO;
    private SessionDAO sessionDAO;
    private EvaluationDAO evaluationDAO;
    private MaterialDAO materialDAO;
    private List<Session> availableSessions;

    public StudentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.submissionDAO = new SubmissionDAO();
        this.sessionDAO = new SessionDAO();
        this.evaluationDAO = new EvaluationDAO();
        this.materialDAO = new MaterialDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Student Dashboard");
        title.setFont(Theme.HEADER_FONT);
        title.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        Theme.styleSecondaryButton(logoutButton);
        logoutButton.setBackground(Theme.PRIMARY_DARK);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoutButton.addActionListener(e -> mainFrame.showPanel(MainFrame.LOGIN_PANEL));

        JButton profileButton = new JButton("Profile");
        Theme.styleSecondaryButton(profileButton);
        profileButton.setBackground(Theme.PRIMARY_DARK);
        profileButton.setForeground(Color.WHITE);
        profileButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        profileButton.addActionListener(e -> mainFrame.showPanel(MainFrame.PROFILE_PANEL));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.PRIMARY_COLOR);
        buttonPanel.add(profileButton);
        buttonPanel.add(logoutButton);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.SUBHEADER_FONT);
        tabbedPane.setBackground(Theme.BG_COLOR);

        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 1) {
                refreshMaterials();
            } else if (selected == 2) {
                refreshStatus();
            }
        });

        tabbedPane.addTab("Registration", createRegistrationPanel());
        tabbedPane.addTab("Upload Materials", createUploadPanel());
        tabbedPane.addTab("My Status", createStatusPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createRegistrationPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel sessionLabel = new JLabel("Select Session");
        Theme.styleLabel(sessionLabel, false);
        sessionCombo = new JComboBox<>();
        sessionCombo.setFont(Theme.STANDARD_FONT);
        sessionCombo.setBackground(Color.WHITE);
        loadSessions();

        JLabel titleLabel = new JLabel("Research Title");
        Theme.styleLabel(titleLabel, false);
        titleField = new JTextField(25);
        titleField.setFont(Theme.STANDARD_FONT);

        JLabel abstractLabel = new JLabel("Abstract");
        Theme.styleLabel(abstractLabel, false);
        abstractArea = new JTextArea(5, 25);
        abstractArea.setFont(Theme.STANDARD_FONT);
        abstractArea.setLineWrap(true);
        abstractArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane scrollPane = new JScrollPane(abstractArea);
        scrollPane.setPreferredSize(new Dimension(400, 100)); // Explicit height to prevent collapsing

        JLabel supervisorLabel = new JLabel("Supervisor Name");
        Theme.styleLabel(supervisorLabel, false);
        // Mock Data for Supervisor Selection (Malaysian Names)
        String[] supervisors = {
                "Dr. Ahmad Albab",
                "Prof. Tan Mei Ling",
                "Dr. Siti Nurhaliza",
                "Mr. Muthusamy",
                "Dr. Wong Wei Hong"
        };
        supervisorField = new JComboBox<>(supervisors);
        supervisorField.setFont(Theme.STANDARD_FONT);
        supervisorField.setBackground(Color.WHITE);

        JLabel typeLabel = new JLabel("Presentation Type");
        Theme.styleLabel(typeLabel, false);
        String[] types = { "Oral Presentation", "Poster Presentation" };
        typeCombo = new JComboBox<>(types);
        typeCombo.setFont(Theme.STANDARD_FONT);
        typeCombo.setBackground(Color.WHITE);

        JButton submitButton = new JButton("Submit Registration");
        Theme.styleButton(submitButton);

        submitButton.addActionListener(e -> {
            User user = mainFrame.getCurrentUser();
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Session expired. Please login again.");
                return;
            }

            int sessionIndex = sessionCombo.getSelectedIndex();
            if (sessionIndex < 0 || availableSessions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a valid session.");
                return;
            }
            String sessionId = availableSessions.get(sessionIndex).getSessionId();

            try {
                submissionDAO.createSubmission(
                        sessionId,
                        user.getUserId(),
                        titleField.getText(),
                        abstractArea.getText(),
                        (String) supervisorField.getSelectedItem(),
                        (String) typeCombo.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Registration Submitted Successfully!");
                titleField.setText("");
                abstractArea.setText("");
                supervisorField.setSelectedIndex(0);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error submitting registration: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(sessionLabel, gbc);
        gbc.gridy++;
        card.add(sessionCombo, gbc);

        gbc.gridy++;
        card.add(titleLabel, gbc);
        gbc.gridy++;
        card.add(titleField, gbc);

        gbc.gridy++;
        card.add(abstractLabel, gbc);
        gbc.gridy++;
        card.add(scrollPane, gbc);

        gbc.gridy++;
        card.add(supervisorLabel, gbc);
        gbc.gridy++;
        card.add(supervisorField, gbc);

        gbc.gridy++;
        card.add(typeLabel, gbc);
        gbc.gridy++;
        card.add(typeCombo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        card.add(submitButton, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private void loadSessions() {
        sessionDAO.seedMockData(); // Ensure mock data exists
        availableSessions = sessionDAO.getAllSessions();
        sessionCombo.removeAllItems();
        for (Session s : availableSessions) {
            sessionCombo.addItem(s.getLocation());
        }
    }

    private JPanel createUploadPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.BG_COLOR);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table Model
        String[] columns = { "File Name", "Format", "Date Uploaded", "Path" };
        materialModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        materialTable = new JTable(materialModel);
        Theme.styleTable(materialTable);
        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(materialModel);
        materialTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(materialTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.BG_COLOR);

        JButton uploadButton = new JButton("Upload New Material");
        Theme.styleButton(uploadButton);
        uploadButton.addActionListener(e -> handleFileUpload());

        buttonPanel.add(uploadButton);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void handleFileUpload() {
        User user = mainFrame.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Session expired.");
            return;
        }

        try {
            // First getting submission ID
            ResultSet rs = submissionDAO.findByStudentId(user.getUserId());
            if (rs.next()) {
                String subId = rs.getString("submission_id");

                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String filename = file.getName();
                    String path = file.getAbsolutePath();
                    String extension = "";

                    int i = filename.lastIndexOf('.');
                    if (i > 0) {
                        extension = filename.substring(i + 1).toUpperCase();
                    }

                    materialDAO.addMaterial(subId, filename, extension, path);
                    JOptionPane.showMessageDialog(this, "File Uploaded Successfully!");
                    refreshMaterials();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please register for a session first.");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error uploading file.");
        }
    }

    private void refreshMaterials() {
        User user = mainFrame.getCurrentUser();
        if (user == null)
            return;

        materialModel.setRowCount(0);

        try {
            ResultSet rs = submissionDAO.findByStudentId(user.getUserId());
            if (rs.next()) {
                String subId = rs.getString("submission_id");
                List<Material> materials = materialDAO.getMaterialsBySubmissionId(subId);

                for (Material m : materials) {
                    materialModel.addRow(new Object[] {
                            m.getFileName(),
                            m.getFileType(),
                            m.getUploadDate(),
                            m.getFilePath()
                    });
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createStatusPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.BG_COLOR);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table Model
        String[] columns = { "Registration ID", "Research Title", "Supervisor", "Presentation Type", "Status" };
        statusModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Immutable
            }
        };

        statusTable = new JTable(statusModel);
        Theme.styleTable(statusTable);
        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(statusModel);
        statusTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(statusTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private void refreshStatus() {
        User user = mainFrame.getCurrentUser();
        if (user == null)
            return;

        statusModel.setRowCount(0); // Clear table

        try {
            ResultSet rs = submissionDAO.findByStudentId(user.getUserId());
            while (rs.next()) {
                String subId = rs.getString("submission_id");
                String title = rs.getString("title");
                String supervisor = rs.getString("supervisor");
                String type = rs.getString("presentation_type");

                boolean isEvaluated = evaluationDAO.isEvaluated(subId);
                String status = isEvaluated ? "Graded \u2705" : "Pending \u23F3"; // Checkmark or Hourglass

                statusModel.addRow(new Object[] { subId, title, supervisor, type, status });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching status data.");
        }
    }
}
