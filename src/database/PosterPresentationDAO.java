package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.PosterPresentation;

public class PosterPresentationDAO {

    public PosterPresentationDAO() {
    }

    public boolean createPresentation(PosterPresentation presentation) {
        String sql = "INSERT INTO poster_presentations (board_id, submission_id, title, description, session_id, status) "
                +
                "VALUES (?, ?::uuid, ?, ?, ?::uuid, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentation.getBoardId());
            stmt.setString(2, presentation.getSubmissionId());
            stmt.setString(3, presentation.getTitle());
            stmt.setString(4, presentation.getDescription());
            stmt.setString(5, presentation.getSessionId());
            stmt.setString(6, presentation.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PosterPresentation getPresentationById(int presentationId) {
        String sql = "SELECT * FROM poster_presentations WHERE presentation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PosterPresentation(
                        rs.getInt("presentation_id"),
                        rs.getInt("board_id"),
                        rs.getString("submission_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("session_id"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PosterPresentation> getPresentationsByBoardId(int boardId) {
        String sql = "SELECT * FROM poster_presentations WHERE board_id = ?";
        List<PosterPresentation> presentations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                presentations.add(new PosterPresentation(
                        rs.getInt("presentation_id"),
                        rs.getInt("board_id"),
                        rs.getString("submission_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("session_id"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presentations;
    }

    public PosterPresentation getPresentationByBoardId(int boardId) {
        String sql = "SELECT * FROM poster_presentations WHERE board_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PosterPresentation(
                        rs.getInt("presentation_id"),
                        rs.getInt("board_id"),
                        rs.getString("submission_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("session_id"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePresentation(PosterPresentation presentation) {
        String sql = "UPDATE poster_presentations SET board_id = ?, submission_id = ?::uuid, title = ?, " +
                "description = ?, session_id = ?::uuid, status = ? WHERE presentation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentation.getBoardId());
            stmt.setString(2, presentation.getSubmissionId());
            stmt.setString(3, presentation.getTitle());
            stmt.setString(4, presentation.getDescription());
            stmt.setString(5, presentation.getSessionId());
            stmt.setString(6, presentation.getStatus());
            stmt.setInt(7, presentation.getPresentationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getStudentNameForBoard(int boardId) {
        String sql = "SELECT u.username FROM poster_presentations pp " +
                "JOIN submissions s ON pp.submission_id = s.submission_id " +
                "JOIN users u ON s.student_id = u.user_id " +
                "WHERE pp.board_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unassigned";
    }

    public boolean deletePresentation(int presentationId) {
        String sql = "DELETE FROM poster_presentations WHERE presentation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}