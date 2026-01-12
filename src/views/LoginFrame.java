import javax.swing.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 600);
        loginFrame.setVisible(true);
        loginFrame.setLocationRelativeTo(null);

        // Role selection
        JPanel loginPanel = new JPanel("Roles");     
        String r[] = {"Student", "Evaluator", "Coordinator"}; // Creates a dropdown menu  
        JCombobox rolesList = new JCombobox(r);
        loginPanel.add(rolesList);

        // Account details (UserID & Password)
        JTextField userID = new JTextField();
        JPasswordField password = new JPasswordField(); // Password hashing
        loginPanel.add(userID);
        loginPanel.add(password);

        // Login button
        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton);
        loginButton.addActionListener (new ActionListener()) {
            public void roleSelect (ActionEvent e) {

                if(userID.equals(studentId) && password.equals(password)){
                    e.StudentFrame().setVisible(true);
                } else if (userID.equals(evaluatorId) && password.equals(password)) {
                    EvaluatorFrame().setVisible(true);
                } else if (userID.equals(coordinatorIid) && password.equals(password)) {
                    CoordinatorFrame().setVisible(true);
                }
            }
        }
        
        // Sign up button
        JDialog signUpDialog = new JDialog();
        signUpDialog.setSize(300, 400);
        }
    }

