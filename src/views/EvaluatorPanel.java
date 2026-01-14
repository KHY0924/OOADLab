package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import models.User;
import models.Submission;
import models.Evaluation;
import database.AssignmentDAO;
import database.EvaluationDAO;

public class EvaluatorPanel extends JPanel {
    private MainFrame mainFrame;
    private DefaultTableModel assignedStudentModel;
    private JSlider problemSlider;
    private JSlider methodSlider;
    private JSlider resultsSlider;
    private JSlider presentationSlider;
    private JTextArea commentArea;

    private AssignmentDAO assignmentDAO;
    private EvaluationDAO evaluationDAO;
    private List<Submission> currentAssignments;

    public EvaluatorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.assignmentDAO = new AssignmentDAO();
        this.evaluationDAO = new EvaluationDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

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

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Theme.BG_COLOR);

        // Refresh Button
        JButton refreshButton = new JButton("Refresh Assignments");
        Theme.styleButton(refreshButton);
        refreshButton.setFont(Theme.STANDARD_FONT);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        refreshButton.addActionListener(e -> loadAssignments());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Theme.BG_COLOR);
        topBar.add(refreshButton);
        content.add(topBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createListPanel(), createEvaluationForm());
        splitPane.setDividerLocation(350);
        splitPane.setBackground(Theme.BG_COLOR);
        splitPane.setBorder(null);
        content.add(splitPane, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        JLabel header = new JLabel("Assigned Presentations");
        header.setFont(Theme.SUBHEADER_FONT);
        header.setForeground(Theme.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = { "Submission ID", "Title" };
        assignedStudentModel = new DefaultTableModel(new Object[][] {}, columns);
        JTable table = new JTable(assignedStudentModel);
        table.setRowHeight(30);
        table.setFont(Theme.STANDARD_FONT);
        table.getTableHeader().setFont(Theme.BOLD_FONT);
        table.getTableHeader().setBackground(Theme.UNVERIFIED_BG);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEvaluationForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(Theme.CARD_BG);
        formContent.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel studentInfo = new JLabel("Select a submission to evaluate (Enter ID below)");
        studentInfo.setFont(Theme.HEADER_FONT);
        studentInfo.setForeground(Theme.PRIMARY_COLOR);

        JLabel idLabel = new JLabel("Submission ID:");
        Theme.styleLabel(idLabel, false);
        JTextField idField = new JTextField(20);

        problemSlider = createSlider();
        methodSlider = createSlider();
        resultsSlider = createSlider();
        presentationSlider = createSlider();

        JLabel commentLabel = new JLabel("Comments");
        Theme.styleLabel(commentLabel, false);
        commentArea = new JTextArea(4, 20);
        commentArea.setFont(Theme.STANDARD_FONT);
        commentArea.setLineWrap(true);
        commentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton submitButton = new JButton("Submit Evaluation");
        Theme.styleButton(submitButton);
        submitButton.addActionListener(e -> {
            User user = mainFrame.getCurrentUser();
            String subId = idField.getText().trim();
            if (user == null || subId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please login and enter a valid Submission ID.");
                return;
            }

            // Create Evaluation Object
            Evaluation eval = new Evaluation(
                    java.util.UUID.randomUUID().toString(), // Helper for ID generation if needed, or DB handles
                    subId,
                    user.getUserId(),
                    problemSlider.getValue() + methodSlider.getValue() + resultsSlider.getValue()
                            + presentationSlider.getValue(),
                    commentArea.getText());
            // Set rubric scores (requires updating Evaluation model or DAO to handle
            // individual scores if not already matched)
            eval.setProblemClarityScore(problemSlider.getValue());
            eval.setMethodologyScore(methodSlider.getValue());
            eval.setResultsScore(resultsSlider.getValue());
            eval.setPresentationScore(presentationSlider.getValue());

            evaluationDAO.saveEvaluation(eval);
            JOptionPane.showMessageDialog(this, "Evaluation Submitted!");

            // Reset
            problemSlider.setValue(5);
            methodSlider.setValue(5);
            resultsSlider.setValue(5);
            presentationSlider.setValue(5);
            commentArea.setText("");
            idField.setText("");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formContent.add(studentInfo, gbc);

        gbc.gridy++;
        formContent.add(idLabel, gbc);
        gbc.gridy++;
        formContent.add(idField, gbc);

        gbc.gridy++;
        formContent.add(createSliderPanel("Problem Clarity (0-10)", problemSlider), gbc);
        gbc.gridy++;
        formContent.add(createSliderPanel("Methodology (0-10)", methodSlider), gbc);
        gbc.gridy++;
        formContent.add(createSliderPanel("Results (0-10)", resultsSlider), gbc);
        gbc.gridy++;
        formContent.add(createSliderPanel("Presentation Quality (0-10)", presentationSlider), gbc);

        gbc.gridy++;
        formContent.add(commentLabel, gbc);
        gbc.gridy++;
        formContent.add(new JScrollPane(commentArea), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formContent.add(submitButton, gbc);

        JScrollPane scrollWrapper = new JScrollPane(formContent);
        scrollWrapper.setBorder(null);
        panel.add(scrollWrapper, BorderLayout.CENTER);

        return panel;
    }

    private void loadAssignments() {
        User user = mainFrame.getCurrentUser();
        if (user != null) {
            assignedStudentModel.setRowCount(0);
            currentAssignments = assignmentDAO.getAssignmentsForEvaluator(user.getUserId());
            for (Submission s : currentAssignments) {
                assignedStudentModel.addRow(new Object[] { s.getId(), s.getTitle() });
            }
        }
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
}
