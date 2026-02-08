package database;

import models.Material;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaterialDAO {

    public MaterialDAO() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS materials (" +
                "material_id UUID PRIMARY KEY, " +
                "submission_id UUID NOT NULL, " +
                "file_name VARCHAR(255), " +
                "file_type VARCHAR(50), " +
                "upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "file_path VARCHAR(500), " +
                "FOREIGN KEY (submission_id) REFERENCES submissions(submission_id))";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating materials table.");
        }
    }

    public void addMaterial(String submissionId, String fileName, String fileType, String filePath)
            throws SQLException {
        String sql = "INSERT INTO materials (material_id, submission_id, file_name, file_type, file_path) VALUES (?, ?::uuid, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, submissionId);
            stmt.setString(3, fileName);
            stmt.setString(4, fileType);
            stmt.setString(5, filePath);
            stmt.executeUpdate();
        }
    }

    public List<Material> getMaterialsBySubmissionId(String submissionId) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE submission_id = ?::uuid ORDER BY upload_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(new Material(
                            rs.getString("material_id"),
                            rs.getString("submission_id"),
                            rs.getString("file_name"),
                            rs.getString("file_type"),
                            rs.getTimestamp("upload_date"),
                            rs.getString("file_path")));
                }
            }
            System.out.println("Error retrieving materials.");
        }
        return materials;
    }
}
