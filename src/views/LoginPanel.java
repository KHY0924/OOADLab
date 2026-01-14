package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.PRIMARY_COLOR);
        card.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subLabel.setFont(Theme.STANDARD_FONT);
        subLabel.setForeground(Theme.TEXT_SECONDARY);
        card.add(subLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);

        JLabel roleLabel = new JLabel("I am a:");
        roleLabel.setFont(Theme.BOLD_FONT);
        roleLabel.setForeground(Theme.TEXT_PRIMARY);
        card.add(roleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 15, 5);
        String[] roles = { "Student", "Coordinator", "Evaluator" };
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setFont(Theme.STANDARD_FONT);
        card.add(roleCombo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.BOLD_FONT);
        userLabel.setForeground(Theme.TEXT_PRIMARY);
        card.add(userLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 15, 5);
        JTextField userField = new JTextField(20);
        userField.setFont(Theme.STANDARD_FONT);
        card.add(userField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.BOLD_FONT);
        passLabel.setForeground(Theme.TEXT_PRIMARY);
        card.add(passLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 25, 5);
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(Theme.STANDARD_FONT);
        card.add(passField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 10, 5);
        JButton loginButton = new JButton("LOGIN");
        Theme.styleButton(loginButton);
        card.add(loginButton, gbc);

        loginButton.addActionListener((ActionEvent e) -> {
            String role = (String) roleCombo.getSelectedItem();
            if (role != null) {
                switch (role) {
                    case "Student":
                        mainFrame.showPanel(MainFrame.STUDENT_PANEL);
                        break;
                    case "Coordinator":
                        mainFrame.showPanel(MainFrame.COORDINATOR_PANEL);
                        break;
                    case "Evaluator":
                        mainFrame.showPanel(MainFrame.EVALUATOR_PANEL);
                        break;
                }
            }
        });

        add(card);
    }
}
