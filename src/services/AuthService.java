package services;

import models.User;
import database.DatabaseConnection;
import database.UserDAO;
import database.StudentProfileDAO;
import java.sql.*;

public class AuthService {
    private UserDAO uDao = new UserDAO();
    private StudentProfileDAO pDao = new StudentProfileDAO();

    public User login(String username, String password) {
        String q = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(q);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                String uid = res.getString("user_id");
                String un = res.getString("username");
                String r = res.getString("role");
                return new User(uid, un, password, r);
            }
            res.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password, String role, String fullName, String email,
            String major) {
        try {
            ResultSet chk = uDao.findByUsername(username);
            if (chk.next()) {
                return false;
            }
            String uid = java.util.UUID.randomUUID().toString();
            uDao.createUser(uid, username, password, role.toLowerCase());
            if ("student".equalsIgnoreCase(role)) {
                pDao.createProfile(uid, fullName, email, major);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}