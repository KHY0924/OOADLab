package views;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import models.User;
import models.Submission;
import models.Evaluation;
import database.AssignmentDAO;
import database.EvaluationDAO;

public class EvaluatorPanel extends JPanel {
    private MainFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel listPanel; // Container for CardLayout

    // List View Components
    private DefaultTableModel assignedStudentModel;
    private JTable assignmentsTable;

    // Evaluation Form Components
    private JSlider problemSlider;
    private JSlider methodSlider;
    private JSlider resultsSlider;
    private JSlider presentationSlider;
    private JTextArea commentArea;
    private JLabel selectedStudentLabel;

    // State
    private String currentSubmissionId;
    private AssignmentDAO assignmentDAO;
    private EvaluationDAO evaluationDAO;
    private List<Submission> currentAssignments;

    public EvaluatorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.assignmentDAO = new AssignmentDAO();
        this.evaluationDAO = new EvaluationDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Evaluator Dashboard");
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

        // Main Content Area with CardLayout
        cardLayout = new CardLayout();
        listPanel = new JPanel(cardLayout);
        listPanel.setBackground(Theme.BG_COLOR);

        listPanel.add(createDashboardPanel(), "DASHBOARD");
        listPanel.add(createListPanel(), "LIST");
        listPanel.add(createEvaluationForm(), "FORM");

        add(listPanel, BorderLayout.CENTER);

        // Show Dashboard initially
        cardLayout.show(listPanel, "DASHBOARD");
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BG_COLOR);

        JButton viewButton = new JButton("View Assigned Submission");
        Theme.styleButton(viewButton);
        viewButton.setFont(Theme.HEADER_FONT);
        // Resized for better visibility as requested
        viewButton.setPreferredSize(new Dimension(450, 70));

        viewButton.addActionListener(e -> {
            loadAssignments();
            cardLayout.show(listPanel, "LIST");
        });

        panel.add(viewButton);
        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header (Back button removed)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_COLOR);

        JLabel label = new JLabel("Assigned Submissions");
        label.setFont(Theme.SUBHEADER_FONT);
        label.setForeground(Theme.PRIMARY_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        topBar.add(label, BorderLayout.WEST);
        panel.add(topBar, BorderLayout.NORTH);

        // Table
        // Added Student Name column and Action column
        String[] columns = { "Submission ID", "Student Name", "Title", "Action" };
        assignedStudentModel = new DefaultTableModel(new Object[][] {}, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only button column is editable
                return column == 3;
            }
        };
        assignmentsTable = new JTable(assignedStudentModel);
        Theme.styleTable(assignmentsTable);

        // Button Renderer & Editor for "Action" column
        assignmentsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        assignmentsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Adjust column widths
        assignmentsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        assignmentsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        assignmentsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        assignmentsTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEvaluationForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_COLOR);

        JButton backButton = new JButton("Back to List");
        Theme.styleSecondaryButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(listPanel, "LIST"));

        selectedStudentLabel = new JLabel("Evaluating Submission");
        selectedStudentLabel.setFont(Theme.SUBHEADER_FONT);
        selectedStudentLabel.setForeground(Theme.PRIMARY_COLOR);

        topBar.add(selectedStudentLabel, BorderLayout.WEST);
        topBar.add(backButton, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Form Content
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(Theme.CARD_BG);
        formContent.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        problemSlider = createSlider();
        methodSlider = createSlider();
        resultsSlider = createSlider();
        presentationSlider = createSlider();

        formContent.add(createSliderPanel("Problem Clarity (0-10)", problemSlider), gbc);
        gbc.gridy++;
        formContent.add(createSliderPanel("Methodology (0-10)", methodSlider), gbc);
        gbc.gridy++;
        formContent.add(createSliderPanel("Results (0-10)", resultsSlider), gbc);
        gbc.gridy++;
        formContent.add(createSliderPanel("Presentation Quality (0-10)", presentationSlider), gbc);

        gbc.gridy++;
        JLabel commentLabel = new JLabel("Comments");
        Theme.styleLabel(commentLabel, false);
        formContent.add(commentLabel, gbc);

        gbc.gridy++;
        commentArea = new JTextArea(4, 20);
        commentArea.setFont(Theme.STANDARD_FONT);
        commentArea.setLineWrap(true);
        commentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formContent.add(new JScrollPane(commentArea), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton submitButton = new JButton("Submit Evaluation");
        Theme.styleButton(submitButton);

        submitButton.addActionListener(e -> {
            if (currentSubmissionId == null)
                return;

            User user = mainFrame.getCurrentUser();
            if (user == null)
                return;

            Evaluation eval = new Evaluation(
                    java.util.UUID.randomUUID().toString(),
                    currentSubmissionId,
                    user.getUserId(),
                    problemSlider.getValue() + methodSlider.getValue() + resultsSlider.getValue()
                            + presentationSlider.getValue(),
                    commentArea.getText());

            eval.setProblemClarityScore(problemSlider.getValue());
            eval.setMethodologyScore(methodSlider.getValue());
            eval.setResultsScore(resultsSlider.getValue());
            eval.setPresentationScore(presentationSlider.getValue());

            evaluationDAO.saveEvaluation(eval);

            JOptionPane.showMessageDialog(this, "Evaluation Completed");
            cardLayout.show(listPanel, "LIST");
        });

        formContent.add(submitButton, gbc);

        JScrollPane viewport = new JScrollPane(formContent);
        viewport.setBorder(null);
        panel.add(viewport, BorderLayout.CENTER);

        return panel;
    }

    private void loadAssignments() {
        User user = mainFrame.getCurrentUser();
        if (user != null) {
            assignedStudentModel.setRowCount(0);
            currentAssignments = assignmentDAO.getAssignmentsForEvaluator(user.getUserId());
            for (Submission s : currentAssignments) {
                // Populate student name
                assignedStudentModel.addRow(new Object[] { s.getId(), s.getStudentName(), s.getTitle(), "Evaluate" });
            }
        }
    }

    private void openEvaluationForm(String subId, String title) {
        String studentName = "Unknown";
        // Find student name from loaded assignments
        for (Submission s : currentAssignments) {
            if (s.getId().equals(subId)) {
                studentName = s.getStudentName();
                break;
            }
        }

        currentSubmissionId = subId;
        selectedStudentLabel.setText("Evaluating: " + studentName + " - " + title);

        // Clear sliders and text
        problemSlider.setValue(5);
        methodSlider.setValue(5);
        resultsSlider.setValue(5);
        presentationSlider.setValue(5);
        commentArea.setText("");

        cardLayout.show(listPanel, "FORM");
    }

    private JSlider createSlider() {
        JSlider slider = new JSlider(0, 10, 5);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(Theme.CARD_BG);
        slider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        return slider;
    }

    private JPanel createSliderPanel(String title, JSlider slider) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.CARD_BG);
        JLabel l = new JLabel(title);
        Theme.styleLabel(l, false);
        p.add(l, BorderLayout.NORTH);
        p.add(slider, BorderLayout.CENTER);
        return p;
    }

    // --- Inner Classes for Table Button ---

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            Theme.styleButton(this);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Evaluate" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            Theme.styleButton(button);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Evaluate" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Perform action
                int row = assignmentsTable.getSelectedRow();
                // Columns: 0=ID, 1=Name, 2=Title, 3=Action
                if (row >= 0 && row < assignmentsTable.getRowCount()) {
                    String subId = (String) assignmentsTable.getValueAt(row, 0);
                    String title = (String) assignmentsTable.getValueAt(row, 2);
                    openEvaluationForm(subId, title);
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
