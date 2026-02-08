package views;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import models.User;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;
    private Map<String, Object> sessionData = new HashMap<>();

    public static final String LOGIN_PANEL = "LOGIN";
    public static final String REGISTER_PANEL = "REGISTER";
    public static final String STUDENT_PANEL = "STUDENT";
    public static final String COORDINATOR_PANEL = "COORDINATOR";
    public static final String EVALUATOR_PANEL = "EVALUATOR";
    public static final String PROFILE_PANEL = "PROFILE";

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private StudentPanel studentPanel;
    private CoordinatorPanel coordinatorPanel;
    private EvaluatorPanel evaluatorPanel;
    private ProfilePanel profilePanel;

    public MainFrame() {
        super("Seminar Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        studentPanel = new StudentPanel(this);
        coordinatorPanel = new CoordinatorPanel(this);
        evaluatorPanel = new EvaluatorPanel(this);
        profilePanel = new ProfilePanel(this);
        mainPanel.add(loginPanel, LOGIN_PANEL);
        mainPanel.add(registerPanel, REGISTER_PANEL);
        mainPanel.add(studentPanel, STUDENT_PANEL);
        mainPanel.add(coordinatorPanel, COORDINATOR_PANEL);
        mainPanel.add(evaluatorPanel, EVALUATOR_PANEL);
        mainPanel.add(profilePanel, PROFILE_PANEL);
        add(mainPanel);
        showPanel(LOGIN_PANEL);
        setVisible(true);
    }

    public void showPanel(String panelName) {
        if (PROFILE_PANEL.equals(panelName)) {
            profilePanel.loadProfile();
        } else if (STUDENT_PANEL.equals(panelName)) {
            studentPanel.refreshData();
        } else if (COORDINATOR_PANEL.equals(panelName)) {
            coordinatorPanel.loadSeminars();
        } else if (EVALUATOR_PANEL.equals(panelName)) {
            evaluatorPanel.refreshData();
        }
        cardLayout.show(mainPanel, panelName);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
