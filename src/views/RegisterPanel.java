package views;

import services.AuthService;
import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private MainFrame mainFrame;
    private AuthService authService;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authService = new AuthService();
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
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.PRIMARY_COLOR);
        card.add(titleLabel, gbc);
        gbc.gridy++;
        JLabel subLabel = new JLabel("Join the Seminar Management System", SwingConstants.CENTER);
        subLabel.setFont(Theme.STANDARD_FONT);
        subLabel.setForeground(Theme.TEXT_SECONDARY);
        card.add(subLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);
        JLabel roleLabel = new JLabel("Role");
        Theme.styleLabel(roleLabel, false);
        card.add(roleLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 10, 5);
        String[] roles = { "Student", "Evaluator", "Coordinator" };
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setFont(Theme.STANDARD_FONT);
        card.add(roleCombo, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel userLabel = new JLabel("Username");
        Theme.styleLabel(userLabel, false);
        card.add(userLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 10, 5);
        JTextField userField = new JTextField(20);
        userField.setFont(Theme.STANDARD_FONT);
        card.add(userField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel passLabel = new JLabel("Password");
        Theme.styleLabel(passLabel, false);
        card.add(passLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 10, 5);
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(Theme.STANDARD_FONT);
        card.add(passField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel nameLabel = new JLabel("Full Name");
        Theme.styleLabel(nameLabel, false);
        card.add(nameLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 10, 5);
        JTextField nameField = new JTextField(20);
        nameField.setFont(Theme.STANDARD_FONT);
        card.add(nameField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel emailLabel = new JLabel("Email (Optional)");
        Theme.styleLabel(emailLabel, false);
        card.add(emailLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 5, 10, 5);
        JTextField emailField = new JTextField(20);
        emailField.setFont(Theme.STANDARD_FONT);
        card.add(emailField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 10, 5);
        JButton registerButton = new JButton("REGISTER");
        Theme.styleButton(registerButton);
        card.add(registerButton, gbc);
        gbc.gridy++;
        JButton backButton = new JButton("Back to Login");
        Theme.styleSecondaryButton(backButton);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        card.add(backButton, gbc);
        roleCombo.addActionListener(e -> {
            boolean isStudent = "Student".equals(roleCombo.getSelectedItem());
            nameLabel.setVisible(isStudent);
            nameField.setVisible(isStudent);
            emailLabel.setVisible(isStudent);
            emailField.setVisible(isStudent);
        });
        registerButton.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            String name = nameField.getText();
            String email = emailField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password are required.");
                return;
            }
            boolean success = authService.register(user, pass, role, name, email, "N/A");
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
                mainFrame.showPanel(MainFrame.LOGIN_PANEL);
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed. Username may exist.");
            }
        });
        backButton.addActionListener(e -> mainFrame.showPanel(MainFrame.LOGIN_PANEL));
        add(card);
    }
}
