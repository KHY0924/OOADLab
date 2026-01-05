package database;

import java.sql.*;

public class SessionDAO {

    public void createSession(String sessionId, String location, Timestamp date) throws SQLException {
    }

    public ResultSet findBySessionId(String sessionId) throws SQLException {
        return null;
    }

    public ResultSet findSessionsByStudent(String studentId) throws SQLException {
        return null;
    }

    public void addStudentToSession(String sessionId, String studentId) throws SQLException {
    }

    public void deleteSession(String sessionId) throws SQLException {
    }
}
