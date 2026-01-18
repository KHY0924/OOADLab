package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.PresentationBoard;

public class PresentationBoardDAO {
    private DatabaseConnection dbConnection;

    public PresentationBoardDAO() {
        this.dbConnection = new DatabaseConnection();
    }

    public boolean createBoard(PresentationBoard board) {
        String sql = "INSERT INTO presentation_boards (board_name, location, max_presentations, current_presentations) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, board.getBoardName());
            stmt.setString(2, board.getLocation());
            stmt.setInt(3, board.getMaxPresentations());
            stmt.setInt(4, board.getCurrentPresentations());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PresentationBoard getBoardById(int boardId) {
        String sql = "SELECT * FROM presentation_boards WHERE board_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PresentationBoard(
                    rs.getInt("board_id"),
                    rs.getString("board_name"),
                    rs.getString("location"),
                    rs.getInt("max_presentations"),
                    rs.getInt("current_presentations")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PresentationBoard> getAllBoards() {
        String sql = "SELECT * FROM presentation_boards";
        List<PresentationBoard> boards = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                boards.add(new PresentationBoard(
                    rs.getInt("board_id"),
                    rs.getString("board_name"),
                    rs.getString("location"),
                    rs.getInt("max_presentations"),
                    rs.getInt("current_presentations")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boards;
    }

    public boolean updateBoard(PresentationBoard board) {
        String sql = "UPDATE presentation_boards SET board_name = ?, location = ?, " +
                     "max_presentations = ?, current_presentations = ? WHERE board_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, board.getBoardName());
            stmt.setString(2, board.getLocation());
            stmt.setInt(3, board.getMaxPresentations());
            stmt.setInt(4, board.getCurrentPresentations());
            stmt.setInt(5, board.getBoardId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBoard(int boardId) {
        String sql = "DELETE FROM presentation_boards WHERE board_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}