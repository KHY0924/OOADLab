package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

public class ListTables {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] types = { "TABLE" };
            ResultSet rs = dbmd.getTables(null, "public", "%", types);
            System.out.println("Tables in public schema:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
