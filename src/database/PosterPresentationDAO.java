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
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentation.getBoardId());
            stmt.setString(2, presentation.getSubmissionId());
            stmt.setString(3, presentation.getTitle());
            stmt.setString(4, presentation.getDescription());
            stmt.setString(5, presentation.getSessionId());
            stmt.setString(6, presentation.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating poster presentation.");
            return false;
        }
    }

    public PosterPresentation getPresentationById(int presentationId) {
        String sql = "SELECT * FROM poster_presentations WHERE presentation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PosterPresentation(rs.getInt("presentation_id"), rs.getInt("board_id"), rs.getString("submission_id"), rs.getString("title"), rs.getString("description"), rs.getString("session_id"), rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving presentation by ID.");
        }
        return null;
    }

    public List<PosterPresentation> getPresentationsByBoardId(int boardId) {
        String sql = "SELECT * FROM poster_presentations WHERE board_id = ?";
        List<PosterPresentation> presentations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                presentations.add(new PosterPresentation(rs.getInt("presentation_id"), rs.getInt("board_id"), rs.getString("submission_id"), rs.getString("title"), rs.getString("description"), rs.getString("session_id"), rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving presentations for board.");
        }
        return presentations;
    }

    public PosterPresentation getPresentationByBoardId(int boardId) {
        String sql = "SELECT * FROM poster_presentations WHERE board_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PosterPresentation(rs.getInt("presentation_id"), rs.getInt("board_id"), rs.getString("submission_id"), rs.getString("title"), rs.getString("description"), rs.getString("session_id"), rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving presentation for board.");
        }
        return null;
    }

    public boolean updatePresentation(PosterPresentation presentation) {
        String sql = "UPDATE poster_presentations SET board_id = ?, submission_id = ?::uuid, title = ?, " +
        "description = ?, session_id = ?::uuid, status = ? WHERE presentation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentation.getBoardId());
            stmt.setString(2, presentation.getSubmissionId());
            stmt.setString(3, presentation.getTitle());
            stmt.setString(4, presentation.getDescription());
            stmt.setString(5, presentation.getSessionId());
            stmt.setString(6, presentation.getStatus());
            stmt.setInt(7, presentation.getPresentationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating poster presentation.");
            return false;
        }
    }

    public String getStudentNameForBoard(int boardId) {
        String sql = "SELECT u.username FROM poster_presentations pp " +
        "JOIN submissions s ON pp.submission_id = s.submission_id " +
        "JOIN users u ON s.student_id = u.user_id " +
        "WHERE pp.board_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving student name for board.");
        }
        return "Unassigned";
    }

    public boolean isSubmissionAssigned(String submissionId) {
        String sql = "SELECT 1 FROM poster_presentations WHERE submission_id = ?::uuid LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, submissionId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking submission assignment.");
            return false;
        }
    }

    public boolean deletePresentation(int presentationId) {
        String sql = "DELETE FROM poster_presentations WHERE presentation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presentationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting presentation.");
            return false;
        }
    }

    public String getBoardNameForSubmission(String submissionId) {
        String sql = "SELECT pb.board_name FROM presentation_boards pb " +
        "JOIN poster_presentations pp ON pb.board_id = pp.board_id " +
        "WHERE pp.submission_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, submissionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("board_name");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving board name.");
        }
        return "Not Assigned";
    }
}
