import java.sql.*;
import java.util.Scanner;

public class BookStoreOperation {

    public static boolean check_order(Connection conn, String oid) {
        try {
            // Check if the order exist
            Statement o_stmt = conn.createStatement();
            String o_check_str = "SELECT * FROM Orders O WHERE O.oid = \"" + oid + "\";";
            ResultSet o_rs = o_stmt.executeQuery(o_check_str);
            if (!o_rs.isBeforeFirst()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void OrderUpdate(Connection conn) {

        Scanner scanner = new Scanner(System.in);

        System.out.print(">>>Please input order ID:");
        String oid = scanner.nextLine();

        if (!check_order(conn, oid)) {
            System.out.println("Order Does Not Exist.");
            return;
        }

        try {
            String currentStatus = null;
            Statement stmt = conn.createStatement();
            String sql1 = "SELECT * FROM Orders O WHERE O.oid = \"" + oid + "\";";
            ResultSet result = stmt.executeQuery(sql1);
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i) + "\t");
            }
            System.out.println();
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(result.getString(i) + "\t");
                }
                currentStatus = result.getString(6);
                System.out.println();
            }
            System.out.println(">>>1. ordered  2. shipped  3. received");
            int choice = scanner.nextInt();
            if (currentStatus.equals("shipped") && choice == 1) {
                System.out.println("Order has already been shipped.");
                return;
            } else if (currentStatus.equals("received") && (choice == 1 || choice == 2)) {
                System.out.println("Order has already been received.");
                return;
            }
            String status = null;
            if (choice == 1)
                status = "ordered";
            else if (choice == 2)
                status = "shipped";
            else if (choice == 3)
                status = "received";
            else
                return;
            String sql2 = "UPDATE Orders SET shipping_status = \"" + status + "\" WHERE oid = \"" + oid + "\";";
            PreparedStatement preparedStmt = conn.prepareStatement(sql2);
            preparedStmt.executeUpdate();
            System.out.println("Success.");
            System.out.println("-----------------------------------");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void OrderQuery(Connection conn) {

        Scanner scanner = new Scanner(System.in);

        try {
            Statement stmt = conn.createStatement();
            System.out.println(">>> 1. ordered  2. shipped  3. received");
            int choice = scanner.nextInt();
            String status = null;
            if (choice == 1)
                status = "ordered";
            else if (choice == 2)
                status = "shipped";
            else if (choice == 3)
                status = "received";
            String sql = "SELECT * FROM Orders O WHERE shipping_status = \"" + status + "\";";
            ResultSet result = stmt.executeQuery(sql);
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i) + "\t");
            }
            System.out.println();
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(result.getString(i) + "\t");
                }
                System.out.println();
            }
            System.out.println("-----------------------------------");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MostPopular(Connection conn, int N) {
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT B.isbn, B.title, B.price, B.inventory_quantity ,COUNT(O.oid) AS num FROM Book B, Orders O WHERE B.isbn = O.isbn GROUP BY B.isbn ORDER BY num DESC LIMIT "
                    + N + ";";
            ResultSet result = stmt.executeQuery(sql);
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i) + "\t");
            }
            System.out.println();
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(result.getString(i) + "\t");
                }
                System.out.println();
            }
            System.out.println("--------------------------------");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}