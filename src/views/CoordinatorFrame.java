package views;

import javax.swing.*;
import java.awt.event.*;

public class CoordinatorFrame extends JFrame {
    public CoordinatorFrame () {
        super("Coordinator");
        setSize(400,600);
        setLocationRelativeTo(null);
        setVisible(true); //temp (just to check functionality)
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // wtf that's a lot :sob:        
        JTabbedPane coordinatorPane = new JTabbedPane();
        coordinatorPane.setBounds(450,200,600,400);
        this.add(coordinatorPane);

        // Creates and manages seminar sessions (date, venue, session type)
        JPanel panel1 = new JPanel();
        coordinatorPane.add("Create and manage seminars", panel1);

        // Assigns evaluators and presenters to sessions 
        JPanel panel2 = new JPanel();
        coordinatorPane.add("Assign to session", panel2);

        // Generates seminar schedules and final evaluation reports
        JPanel panel3 = new JPanel();
        coordinatorPane.add("Generate schedule/report", panel3);
       
        // Oversees award nomination for Best Oral, Best Poster, and People’s Choice
        JPanel panel4 = new JPanel();
        coordinatorPane.add("Award nomination", panel4);        
    }

    public static void main(String[] args) {
        new CoordinatorFrame();
    }
}