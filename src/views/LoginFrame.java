package views;

import models.User; // needs User.java to utilize userID and password 
import javax.swing.*;
import java.awt.event.*;

public class LoginFrame extends JFrame implements ActionListener{

    private JComboBox<String> rolesList;
    private JButton loginButton, signUpButton;
    private JPasswordField password;
    private JTextField userID; 

    public LoginFrame() {
        super("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        // Role selection
        JPanel loginPanel = new JPanel();     

        String rolesAvailable[] = {"Student", "Evaluator", "Coordinator"}; // Creates a dropdown menu  
        JComboBox<String> rolesList = new JComboBox<>(rolesAvailable);
        loginPanel.add(rolesList);

        // Account details (UserID & Password)
        JTextField userID = new JTextField();
        JPasswordField password = new JPasswordField(); // Password hashing
        loginPanel.add(userID);
        loginPanel.add(password);

        // Login button
        JButton loginButton = new JButton("LOGIN");
        JButton signUpButton = new JButton("SIGN UP");
        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);
        loginPanel.add(loginButton);
        loginPanel.add(signUpButton);
        
    }

    public void handleLogin(ActionEvent e) {
        String role = (String) rolesList.getSelectedItem();
        String givenID = userID.getText();
        String givenPassword = password.getPassword();

        this.dispose();
       
        if(role.equals("Student") && givenID.equals(studentId) && givenPassword.equals(password)){
            new StudentFrame().setVisible(true);
        } else if (role.equals("Evaluator") && givenID.equals(evaluatorId) && givenPassword.equals(password)) {
            new EvaluatorFrame().setVisible(true);
        } else if (role.equals("Coordinator") && givenID.equals(coordinatorId) && givenPassword.equals(password)) {
            new CoordinatorFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid login details. Please ensure you have the correct details.");
        }
    }
    
    public static void main(String[] args) {
        new LoginFrame();
    }
}
