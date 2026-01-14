package views;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static final String LOGIN_PANEL = "LOGIN";
    public static final String STUDENT_PANEL = "STUDENT";
    public static final String COORDINATOR_PANEL = "COORDINATOR";
    public static final String EVALUATOR_PANEL = "EVALUATOR";

    private LoginPanel loginPanel;
    private JPanel studentPanel;
    private JPanel coordinatorPanel;
    private JPanel evaluatorPanel;

    public MainFrame() {
        super("Seminar Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        studentPanel = new StudentPanel(this);
        coordinatorPanel = new CoordinatorPanel(this);
        evaluatorPanel = new EvaluatorPanel(this);

        mainPanel.add(loginPanel, LOGIN_PANEL);
        mainPanel.add(studentPanel, STUDENT_PANEL);
        mainPanel.add(coordinatorPanel, COORDINATOR_PANEL);
        mainPanel.add(evaluatorPanel, EVALUATOR_PANEL);

        add(mainPanel);

        showPanel(LOGIN_PANEL);

        setVisible(true);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void setStudentPanel(JPanel panel) {
        mainPanel.add(panel, STUDENT_PANEL);
        studentPanel = panel;
    }

    public void setCoordinatorPanel(JPanel panel) {
        mainPanel.add(panel, COORDINATOR_PANEL);
        coordinatorPanel = panel;
    }

    public void setEvaluatorPanel(JPanel panel) {
        mainPanel.add(panel, EVALUATOR_PANEL);
        evaluatorPanel = panel;
    }
}
