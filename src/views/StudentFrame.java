// fml 
package views;

import javax.swing.*;


public class StudentFrame extends JFrame {
    public StudentFrame () {
        super ("Student");
        setSize(600,400);
        setLocationRelativeTo(null);
        setVisible(true); //temp (just to check functionality)
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane studentPane = new JTabbedPane();
        studentPane.setBounds(450,200,600,400);
        this.add(studentPane);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        
        // Register and Upload tabs
        studentPane.add("Registration", panel1);
        studentPane.add("Upload", panel2);
    }
    
    public static void main(String[] args) {
        new StudentFrame();
    }
}