import java.sql.*;

public class DatabaseOverview {
    public static int count(Connection conn, String table) {
        int count = -1;
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT COUNT(*) FROM " + table + ";";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException se) {
            // se.printStackTrace();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
