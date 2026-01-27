package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.PosterPresentation;

public class PosterPresentationDAO {
    private DatabaseConnection dbConnection;

    public PosterPresentationDAO() {
        this.dbConnection = new DatabaseConnection();
    }

    public boolean createPresentation(PosterPresentation presentation) {
        String sql = "INSERT INTO poster_presentations (board_id, submission_id, title, description, session_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentation.getBoardId());
            stmt.setInt(2, presentation.getSubmissionId());
            stmt.setString(3, presentation.getTitle());
            stmt.setString(4, presentation.getDescription());
            stmt.setInt(5, presentation.getSessionId());
            stmt.setString(6, presentation.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PosterPresentation getPresentationById(int presentationId) {
        String sql = "SELECT * FROM poster_presentations WHERE presentation_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PosterPresentation(
                    rs.getInt("presentation_id"),
                    rs.getInt("board_id"),
                    rs.getInt("submission_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("session_id"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PosterPresentation> getPresentationsByBoardId(int boardId) {
        String sql = "SELECT * FROM poster_presentations WHERE board_id = ?";
        List<PosterPresentation> presentations = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                presentations.add(new PosterPresentation(
                    rs.getInt("presentation_id"),
                    rs.getInt("board_id"),
                    rs.getInt("submission_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("session_id"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presentations;
    }

    public boolean updatePresentation(PosterPresentation presentation) {
        String sql = "UPDATE poster_presentations SET board_id = ?, submission_id = ?, title = ?, " +
                     "description = ?, session_id = ?, status = ? WHERE presentation_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentation.getBoardId());
            stmt.setInt(2, presentation.getSubmissionId());
            stmt.setString(3, presentation.getTitle());
            stmt.setString(4, presentation.getDescription());
            stmt.setInt(5, presentation.getSessionId());
            stmt.setString(6, presentation.getStatus());
            stmt.setInt(7, presentation.getPresentationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePresentation(int presentationId) {
        String sql = "DELETE FROM poster_presentations WHERE presentation_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}