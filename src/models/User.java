package models;

import java.util.UUID;

public class User {
    private String userId;
    private String username;
    private String password;
    private String role;

    public User(String userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public boolean validateCredentials(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public static User createUserAccount(String username, String password, String role) {
        String newId = UUID.randomUUID().toString();
        User newUser = new User(newId, username, password, role);
        return newUser;
    }

    public void createEmptyProfile() {
        StudentProfile profile = new StudentProfile(this.userId);
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}

