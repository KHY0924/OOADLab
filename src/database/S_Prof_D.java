package database;

import java.sql.*;

public class S_Prof_D {

    public void createStudentProfile(String userId, String name, String email, String major) throws SQLException {

        String query = "INSERT INTO student_profiles (user_id, full_name, email, major) VALUES (?::uuid, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, major);

            preparedStatement.executeUpdate();
            System.out.println("student profile created " + name);
        }
    }

    public ResultSet getProfileById(String userId) throws SQLException {
        String query = "SELECT * FROM student_profiles WHERE user_id = ?::uuid";
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setString(1, userId);
        return preparedStatement.executeQuery();
    }

    public void updateStudentProfile(String userId, String name, String email, String major) throws SQLException {
        String query = "UPDATE student_profiles SET full_name = ?, email = ?, major = ? WHERE user_id = ?::uuid";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, major);
            preparedStatement.setString(4, userId);

            preparedStatement.executeUpdate();
            System.out.println("student profile updated " + userId);
        }
    }
}
