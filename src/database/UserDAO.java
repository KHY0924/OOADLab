package database;

import java.sql.*;

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
}
