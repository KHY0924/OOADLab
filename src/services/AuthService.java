package services;

import models.User;
import database.DatabaseConnection;
import database.UserDAO;
import database.StudentProfileDAO;
import java.sql.*;

public class AuthService {

    private UserDAO userDAO = new UserDAO();
    private StudentProfileDAO profileDAO = new StudentProfileDAO();

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String id = rs.getString("user_id");
                String name = rs.getString("username");
                String role = rs.getString("role");
                return new User(id, name, password, role);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password, String role, String fullName, String email,
            String major) {
        try {
            // Check if user exists
            ResultSet check = userDAO.findByUsername(username);
            if (check.next()) {
                return false; // User exists
            }

            String userId = java.util.UUID.randomUUID().toString();
            userDAO.createUser(userId, username, password, role.toLowerCase());

            if ("student".equalsIgnoreCase(role)) {
                profileDAO.createProfile(userId, fullName, email, major);
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}