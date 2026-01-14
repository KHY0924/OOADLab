package views;

import javax.swing.*;
import java.awt.*;

public class StudentPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField titleField;
    private JTextArea abstractArea;
    private JTextField supervisorField;
    private JComboBox<String> typeCombo;
    private JLabel uploadedFileLabel;
    private JLabel statusLabel;

    public StudentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Student Dashboard");
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

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.SUBHEADER_FONT);
        tabbedPane.setBackground(Theme.BG_COLOR);

        tabbedPane.addTab("Registration", createRegistrationPanel());
        tabbedPane.addTab("Upload Materials", createUploadPanel());
        tabbedPane.addTab("My Status", createStatusPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createRegistrationPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Research Title");
        Theme.styleLabel(titleLabel, false);
        titleField = new JTextField(25);
        titleField.setFont(Theme.STANDARD_FONT);

        JLabel abstractLabel = new JLabel("Abstract");
        Theme.styleLabel(abstractLabel, false);
        abstractArea = new JTextArea(5, 25);
        abstractArea.setFont(Theme.STANDARD_FONT);
        abstractArea.setLineWrap(true);
        abstractArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane scrollPane = new JScrollPane(abstractArea);

        JLabel supervisorLabel = new JLabel("Supervisor Name");
        Theme.styleLabel(supervisorLabel, false);
        supervisorField = new JTextField(25);
        supervisorField.setFont(Theme.STANDARD_FONT);

        JLabel typeLabel = new JLabel("Presentation Type");
        Theme.styleLabel(typeLabel, false);
        String[] types = { "Oral Presentation", "Poster Presentation" };
        typeCombo = new JComboBox<>(types);
        typeCombo.setFont(Theme.STANDARD_FONT);
        typeCombo.setBackground(Color.WHITE);

        JButton submitButton = new JButton("Submit Registration");
        Theme.styleButton(submitButton);
        submitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Registration Submitted Successfully!");
            titleField.setText("");
            abstractArea.setText("");
            supervisorField.setText("");
            statusLabel.setText("Status: Registered (" + typeCombo.getSelectedItem() + ")");
            statusLabel.setForeground(new Color(0, 150, 0));
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(titleLabel, gbc);
        gbc.gridy++;
        card.add(titleField, gbc);

        gbc.gridy++;
        card.add(abstractLabel, gbc);
        gbc.gridy++;
        card.add(scrollPane, gbc);

        gbc.gridy++;
        card.add(supervisorLabel, gbc);
        gbc.gridy++;
        card.add(supervisorField, gbc);

        gbc.gridy++;
        card.add(typeLabel, gbc);
        gbc.gridy++;
        card.add(typeCombo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        card.add(submitButton, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createUploadPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel icon = new JLabel("\uD83D\uDCC2");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        card.add(icon, gbc);

        gbc.gridy++;
        JLabel instruction = new JLabel("Upload your slides (PPT/PDF) or Poster image.");
        Theme.styleLabel(instruction, false);
        card.add(instruction, gbc);

        gbc.gridy++;
        uploadedFileLabel = new JLabel("No file selected");
        Theme.styleLabel(uploadedFileLabel, false);
        uploadedFileLabel.setForeground(Color.GRAY);
        card.add(uploadedFileLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 20, 10, 20);
        JButton uploadButton = new JButton("Choose File...");
        Theme.styleButton(uploadButton);
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getName();
                uploadedFileLabel.setText("Selected: " + filename);
                uploadedFileLabel.setForeground(new Color(0, 100, 0));
                JOptionPane.showMessageDialog(this, "File uploaded: " + filename);
            }
        });
        card.add(uploadButton, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createStatusPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        statusLabel = new JLabel("Status: Not Registered");
        statusLabel.setFont(Theme.HEADER_FONT);
        statusLabel.setForeground(Theme.TEXT_SECONDARY);

        card.add(statusLabel);
        wrapper.add(card);
        return wrapper;
    }
}
