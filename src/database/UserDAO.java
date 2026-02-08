package database;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import models.User;

public class UserDAO {

    public void createUser(String uid, String uname, String pwd, String role) throws SQLException {
        String q = "INSERT INTO users (user_id, username, password, role) VALUES (?::uuid, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(q)) {
            pst.setString(1, uid);
            pst.setString(2, uname);
            pst.setString(3, pwd);
            pst.setString(4, role);
            pst.executeUpdate();
        }
    }

    public ResultSet findByUsername(String uname) throws SQLException {
        String q = "SELECT * FROM users WHERE username = ?";
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(q);
        pst.setString(1, uname);
        return pst.executeQuery();
    }

    public ResultSet getUsersByRole(String role) throws SQLException {
        String q = "SELECT * FROM users WHERE role = ?";
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(q);
        pst.setString(1, role);
        return pst.executeQuery();
    }

    public ResultSet findByUserId(String userId) throws SQLException {
        String q = "SELECT * FROM users WHERE user_id = ?::uuid";
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(q);
        pst.setString(1, userId);
        return pst.executeQuery();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                users.add(new User(userId, username, password, role));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all users.");
        }
        return users;
    }
}
