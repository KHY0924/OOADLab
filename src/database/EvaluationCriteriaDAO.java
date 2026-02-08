package database;

import models.EvaluationCriteria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationCriteriaDAO {
    private DatabaseConnection dbConnection;

    public EvaluationCriteriaDAO() {
        this.dbConnection = new DatabaseConnection();
    }

    public boolean createCriteria(EvaluationCriteria criteria) {
        String sql = "INSERT INTO evaluation_criteria (presentation_id, criteria_name, description, max_score, weight) "
                +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, criteria.getPresentationId());
            stmt.setString(2, criteria.getCriteriaName());
            stmt.setString(3, criteria.getDescription());
            stmt.setInt(4, criteria.getMaxScore());
            stmt.setInt(5, criteria.getWeight());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating evaluation criteria.");
            return false;
        }
    }

    public EvaluationCriteria getCriteriaById(int criteriaId) {
        String sql = "SELECT * FROM evaluation_criteria WHERE criteria_id = ?";
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, criteriaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new EvaluationCriteria(
                        rs.getInt("criteria_id"),
                        rs.getInt("presentation_id"),
                        rs.getString("criteria_name"),
                        rs.getString("description"),
                        rs.getInt("max_score"),
                        rs.getInt("weight"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving criteria by ID.");
        }
        return null;
    }

    public List<EvaluationCriteria> getCriteriaByPresentationId(int presentationId) {
        String sql = "SELECT * FROM evaluation_criteria WHERE presentation_id = ?";
        List<EvaluationCriteria> criteriaList = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                criteriaList.add(new EvaluationCriteria(
                        rs.getInt("criteria_id"),
                        rs.getInt("presentation_id"),
                        rs.getString("criteria_name"),
                        rs.getString("description"),
                        rs.getInt("max_score"),
                        rs.getInt("weight")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving criteria list.");
        }
        return criteriaList;
    }

    public boolean updateCriteria(EvaluationCriteria criteria) {
        String sql = "UPDATE evaluation_criteria SET presentation_id = ?, criteria_name = ?, " +
                "description = ?, max_score = ?, weight = ? WHERE criteria_id = ?";
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, criteria.getPresentationId());
            stmt.setString(2, criteria.getCriteriaName());
            stmt.setString(3, criteria.getDescription());
            stmt.setInt(4, criteria.getMaxScore());
            stmt.setInt(5, criteria.getWeight());
            stmt.setInt(6, criteria.getCriteriaId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating evaluation criteria.");
            return false;
        }
    }

    public boolean deleteCriteria(int criteriaId) {
        String sql = "DELETE FROM evaluation_criteria WHERE criteria_id = ?";
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, criteriaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting evaluation criteria.");
            return false;
        }
    }
}