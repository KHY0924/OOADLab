package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class CheckColumns {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM sessions LIMIT 1")) {

            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            System.out.println("Columns in sessions table:");
            for (int i = 1; i <= count; i++) {
                System.out.println("- " + meta.getColumnName(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
