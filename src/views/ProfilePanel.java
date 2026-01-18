package views;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import models.User;
import database.StudentProfileDAO;

public class ProfilePanel extends JPanel {
    private MainFrame mainFrame;
    private StudentProfileDAO profileDAO;

    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField majorField;

    public ProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.profileDAO = new StudentProfileDAO();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Edit Profile");
        title.setFont(Theme.HEADER_FONT);
        title.setForeground(Color.WHITE);

        JButton backButton = new JButton("Back");
        Theme.styleSecondaryButton(backButton);
        backButton.setBackground(Theme.PRIMARY_DARK);
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backButton.addActionListener(e -> mainFrame.showPanel(MainFrame.STUDENT_PANEL));

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(Theme.createCardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel nameLabel = new JLabel("Full Name");
        Theme.styleLabel(nameLabel, false);
        fullNameField = new JTextField(25);
        fullNameField.setFont(Theme.STANDARD_FONT);

        JLabel emailLabel = new JLabel("Email");
        Theme.styleLabel(emailLabel, false);
        emailField = new JTextField(25);
        emailField.setFont(Theme.STANDARD_FONT);

        JLabel majorLabel = new JLabel("Major");
        Theme.styleLabel(majorLabel, false);
        majorField = new JTextField(25);
        majorField.setFont(Theme.STANDARD_FONT);

        JButton saveButton = new JButton("Save Changes");
        Theme.styleButton(saveButton);
        saveButton.addActionListener(e -> saveProfile());

        card.add(nameLabel, gbc);
        gbc.gridy++;
        card.add(fullNameField, gbc);

        gbc.gridy++;
        card.add(emailLabel, gbc);
        gbc.gridy++;
        card.add(emailField, gbc);

        gbc.gridy++;
        card.add(majorLabel, gbc);
        gbc.gridy++;
        card.add(majorField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        card.add(saveButton, gbc);

        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    public void loadProfile() {
        User user = mainFrame.getCurrentUser();
        if (user == null)
            return;

        try {
            ResultSet rs = profileDAO.findByUserId(user.getUserId());
            if (rs.next()) {
                fullNameField.setText(rs.getString("full_name"));
                emailField.setText(rs.getString("email"));
                majorField.setText(rs.getString("major"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile.");
        }
    }

    private void saveProfile() {
        User user = mainFrame.getCurrentUser();
        if (user == null)
            return;

        try {
            profileDAO.updateProfile(user.getUserId(), fullNameField.getText(), emailField.getText(),
                    majorField.getText());
            JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile.");
        }
    }
}
