package controllers;

import database.UserDAO;
import database.StudentProfileDAO;
import java.sql.SQLException;

public class UserController {
    private UserDAO userDAO = new UserDAO();
    private StudentProfileDAO profileDAO = new StudentProfileDAO();

    public void login(String username, String password) {
        // Simple stub for now, real authentication usually in AuthService or here
        // checking password hash
        System.out.println("Login check for: " + username);
    }

    public void registerUser(String username, String password, String fullName, String email, String major) {
        try {
            // Create User
            String userId = java.util.UUID.randomUUID().toString();
            // In a real app, hash the password!
            userDAO.createUser(userId, username, password, "student");

            // Create Profile
            profileDAO.createProfile(userId, fullName, email, major);

            System.out.println("User registered successfully: " + username);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Registration failed.");
        }
    }

    private void fillStudentProfile(String userId, String fullName, String email, String major) {
        // Redundant if covered by registerUser, but keeping for compatibility if needed
        // or removing.
        // The registerUser logic above covers it.
    }
}
