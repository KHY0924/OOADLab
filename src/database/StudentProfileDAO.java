package models;

public class StudentProfile {
    private String userId;
    private String fullName;
    private String email;
    private String major;

    public StudentProfile(String userId) {
        this.userId = userId;
    }

    public static StudentProfile loadStudentProfile(String userId) {
        return null;
    }

    public void updateProfile(String fullName, String email, String major) {
        this.fullName = fullName;
        this.email = email;
        this.major = major;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMajor() {
        return major;
    }

    @Override
    public String toString() {
        return "Profile [Name=" + fullName + ", Major=" + major + "]";
    }
}
