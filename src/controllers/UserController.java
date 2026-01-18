package controllers;

import database.UserDAO;
import database.StudentProfileDAO;
import java.sql.SQLException;

public class UserController {
    private UserDAO userDAO = new UserDAO();
    private StudentProfileDAO profileDAO = new StudentProfileDAO();

    public void login(String username, String password) {

        System.out.println("Login check for: " + username);
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
