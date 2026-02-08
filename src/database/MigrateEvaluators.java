package database;

import java.sql.Connection;
import java.sql.Statement;

public class MigrateEvaluators {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS session_evaluators (" +
                    "session_id UUID REFERENCES sessions(session_id) ON DELETE CASCADE, " +
                    "evaluator_id UUID REFERENCES users(user_id) ON DELETE CASCADE, " +
                    "PRIMARY KEY (session_id, evaluator_id));";
            stmt.execute(sql);
            System.out.println("Table session_evaluators created successfully.");

             
            String migrateSql = "INSERT INTO session_evaluators (session_id, evaluator_id) " +
                    "SELECT session_id, evaluator_id FROM sessions WHERE evaluator_id IS NOT NULL " +
                    "ON CONFLICT DO NOTHING;";
            stmt.execute(migrateSql);
            System.out.println("Migrated existing evaluator data.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
