import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class MigrationAddBoardCols {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Add session_id column
            try {
                stmt.execute("ALTER TABLE presentation_boards ADD COLUMN session_id UUID");
                System.out.println("Added session_id column.");
            } catch (SQLException e) {
                System.out.println("session_id column might already exist: " + e.getMessage());
            }

            // Add presentation_type column
            try {
                stmt.execute("ALTER TABLE presentation_boards ADD COLUMN presentation_type VARCHAR(50)");
                System.out.println("Added presentation_type column.");
            } catch (SQLException e) {
                System.out.println("presentation_type column might already exist: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
