package views;

import User.java; // needs User.java to utilize userID and password
import javax.swing.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(600, 400);
        loginFrame.setVisible(true);
        loginFrame.setLocationRelativeTo(null);

        // Role selection
        JPanel loginPanel = new JPanel();     

        String roles[] = {"Student", "Evaluator", "Coordinator"}; // Creates a dropdown menu  
        JComboBox<String> rolesList = new JComboBox<>(roles);
        loginPanel.add(rolesList);

        // Account details (UserID & Password)
        JTextField userID = new JTextField();
        JPasswordField password = new JPasswordField(); // Password hashing
        loginPanel.add(userID);
        loginPanel.add(password);

        // Login button
        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        // from here on, it's incomplete and needs fixing, will get to it at some point hrmm
        loginButton.addActionListener (new ActionListener()) {
            public void roleSelect (ActionEvent e) {

                if(userID.equals(studentId) && password.equals(password)){
                    StudentFrame().setVisible(true);
                } else if (userID.equals(evaluatorId) && password.equals(password)) {
                    EvaluatorFrame().setVisible(true);
                } else if (userID.equals(coordinatorId) && password.equals(password)) {
                    CoordinatorFrame().setVisible(true);
                }
            }
        }
        
        // Sign up button
        JDialog signUpDialog = new JDialog();
        signUpDialog.setSize(300, 400);

    }
    
    public static void main(String[] args) {
            new LoginFrame();
        }
}
