package controllers;

import database.UserDAO;
import database.StudentProfileDAO;
import services.AuthService;
import java.sql.SQLException;

public class UserController {
    private UserDAO userDAO = new UserDAO();
    private StudentProfileDAO profileDAO = new StudentProfileDAO();

    public void login(String username, String password) {
        AuthService authService = new AuthService();
        models.User user = authService.login(username, password);
        if (user != null) {
            System.out.println("Login success in Controller for: " + username);
        } else {
            System.out.println("Login failure in Controller for: " + username);
        }
    }

    public void registerUser(String username, String password, String fullName, String email, String major) {
        try {

            String userId = java.util.UUID.randomUUID().toString();

            userDAO.createUser(userId, username, password, "student");

            profileDAO.createProfile(userId, fullName, email, major);

            System.out.println("User registered successfully: " + username);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Registration failed.");
        }
    }

    private void fillStudentProfile(String userId, String fullName, String email, String major) {

    }
}
