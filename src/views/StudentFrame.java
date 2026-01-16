// fml 
package views;

import views.RegistrationTab;
import views.SubmissionsTab;
import javax.swing.*;

public class StudentFrame extends JFrame {
    public StudentFrame () {
        super ("Student");
        setSize(600,400);
        setLocationRelativeTo(null);
        setVisible(true); //temp (just to check functionality)
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Creates tabs
        JTabbedPane studentPane = new JTabbedPane();
        studentPane.setBounds(450,200,600,400);

        // Register and Upload tabs
        studentPane.add("Seminar Registration", new RegistrationTab());
        studentPane.add("Submission Upload", new SubmissionsTab());

        this.add(studentPane);
    }
    
    public static void main(String[] args) {
        new StudentFrame();
    }
}