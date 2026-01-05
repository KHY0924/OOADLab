package controllers;

import models.User;
import models.StudentProfile;

public class UserController {

    public void login(String username, String password) {
        User user = null;

        if (user != null && user.validateCredentials(password)) {
            StudentProfile profile = StudentProfile.loadStudentProfile(user.getUserId());
        }
    }

    public void registerUser(String username, String password, String fullName, String email, String major) {
        User newUser = User.createUserAccount(username, password, "student");
        newUser.createEmptyProfile();
        fillStudentProfile(newUser.getUserId(), fullName, email, major);
    }

    private void fillStudentProfile(String userId, String fullName, String email, String major) {
        StudentProfile profile = StudentProfile.loadStudentProfile(userId);
        if (profile != null) {
            profile.updateProfile(fullName, email, major);
        }
    }
}
