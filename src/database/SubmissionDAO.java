package database;

import java.sql.*;

public class SubmissionDAO {

    public void createSubmission(String seminarId, String studentId, String title,
            String abstractText, String supervisor, String presentationType) throws SQLException {
    }

    public ResultSet findByStudentId(String studentId) throws SQLException {
        return null;
    }

    public void updateSubmission(String studentId, String title, String abstractText) throws SQLException {
    }

    public void saveFilePath(String studentId, String filePath) throws SQLException {
    }

    public void deleteSubmission(String studentId) throws SQLException {
    }
}
