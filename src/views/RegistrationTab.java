package views; 

import models.Evaluator;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;

public class RegistrationTab extends JPanel implements ActionListener{

    // Research title, Abstract, Supervisor name
    // Presentation type (Oral/Poster). 
    private JTextField titleField;
    private JTextField supervisorNameField;
    private JTextArea abstractArea;
    private JComboBox<String> presentationTypeBox;
    private JButton submitButton, uploadButton, cancelButton;

    public RegistrationTab() {

        JLabel title = new JLabel("Presentation Title: ");
        title.setBounds(310,30,500,50);
        titleField = new JTextField();
        add(title);
        add(titleField); 

        supervisorNameField = new JTextField();
        this.add(supervisorNameField);

        abstractArea = new JTextArea();
        this.add(abstractArea);

        String presentationTypes[] = {"Oral", "Poster"};
        presentationTypeBox = new JComboBox<>(presentationTypes);
        this.add(presentationTypeBox);
        
        submitButton = new JButton("SUBMIT");
        uploadButton = new JButton("UPLOAD FILE");
        cancelButton = new JButton("CANCEL");
        submitButton.addActionListener(this);
        uploadButton.addActionListener(this);
        cancelButton.addActionListener(this);
        this.add(submitButton);
        this.add(uploadButton);
        this.add(cancelButton);

        
    }

    public void uploadMaterials (ActionListener u) {
        JFileChooser files = new JFileChooser();
        files.showOpenDialog(this);
    }

    public void submitActionPerformed (ActionListener s) {
        if(s.getSource() == submitButton) {
            evaluatorId.isAssigned(false);
            System.out.println("Registration successful!");
        } else if (s.getSource() != submitButton) {
            evaluatorId.isAssigned(true);
            System.out.println("Registration unsuccessful. Supervisor of choice is not available!");
            supervisorNameField.setText(" ");
        }
        
    }

    public static void main(String[] args) {
        
    }
}