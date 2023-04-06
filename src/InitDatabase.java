import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class InitDatabase {

    public static void init_tables(Connection conn) {

        try {
            Statement stmt = conn.createStatement();
            String[] create_tables = {
                    "CREATE TABLE Customer " +
                            "(uid CHAR(10) not NULL, " +
                            " name CHAR(50) not NULL, " +
                            " address CHAR(200) not NULL, " +
                            " PRIMARY KEY ( uid ));",
                    "CREATE TABLE Book " +
                            "(isbn CHAR(13) not NULL, " +
                            " title CHAR(100) not NULL, " +
                            " price INTEGER, " +
                            " inventory_quantity INTEGER, " +
                            " PRIMARY KEY ( isbn ));",
                    "CREATE TABLE Orders " +
                            "(oid CHAR(8) not NULL, " +
                            " uid CHAR(10) not NULL, " +
                            " isbn CHAR(13) not NULL, " +
                            " order_date DATE, " +
                            " order_quantity INTEGER, " +
                            " shipping_status CHAR(8), " +
                            " FOREIGN KEY ( uid ) REFERENCES Customer( uid ), " +
                            " FOREIGN KEY ( isbn ) REFERENCES Book( isbn ), " +
                            " PRIMARY KEY ( oid, uid, isbn ));",
                    "CREATE TABLE Author " +
                            "(aid CHAR(10) not NULL, " +
                            " aname CHAR(50) not NULL, " +
                            " PRIMARY KEY ( aid ));",
                    "CREATE TABLE Writes " +
                            "(isbn CHAR(13) not NULL, " +
                            " aid CHAR(10) not NULL, " +
                            " FOREIGN KEY ( isbn ) REFERENCES Book( isbn ), " +
                            " FOREIGN KEY ( aid ) REFERENCES Author( aid ), " +
                            " PRIMARY KEY ( isbn, aid ));"
            };

            for (String sql : create_tables) {
                stmt.executeUpdate(sql);
            }
            System.out.println("Tables created successfully.");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load_records(Connection conn) {

        String line = "";
        String sql = "";
        // Load Customer.tsv
        try (BufferedReader br = new BufferedReader(new FileReader("tsv/Customer.tsv"))) {
            sql = "INSERT INTO Customer (uid,name,address) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] values = line.split("  ");
                values[2] = values[2].replaceAll(" ", ",");
                values[2] = values[2].replaceAll("-", " ");

                pstmt.setString(1, values[0]);
                pstmt.setString(2, values[1]);
                pstmt.setString(3, values[2]);

                pstmt.executeUpdate();
            }
            System.out.println("Records in Customer.tsv loaded successfully.");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Load Book.tsv
        try (BufferedReader br = new BufferedReader(new FileReader("tsv/Book.tsv"))) {
            sql = "INSERT INTO Book (isbn, title, price, inventory_quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] values = line.split("  ");

                pstmt.setString(1, values[0]);
                pstmt.setString(2, values[1]);
                pstmt.setInt(3, Integer.parseInt(values[2]));
                pstmt.setInt(4, Integer.parseInt(values[3]));

                pstmt.executeUpdate();
            }
            System.out.println("Records in Book.tsv loaded successfully.");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Load Orders.tsv
        try (BufferedReader br = new BufferedReader(new FileReader("tsv/Orders.tsv"))) {
            sql = "INSERT INTO Orders (oid, uid, isbn, order_date, order_quantity, shipping_status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] values = line.split("  ");

                pstmt.setString(1, values[0]);
                pstmt.setString(2, values[1]);
                pstmt.setString(3, values[2]);
                pstmt.setDate(4, Date.valueOf(values[3]));
                pstmt.setInt(5, Integer.parseInt(values[4]));
                pstmt.setString(6, values[5]);

                pstmt.executeUpdate();
            }
            System.out.println("Records in Orders.tsv loaded successfully.");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader("tsv/Author.tsv"))) {
            sql = "INSERT INTO Author (aid, aname) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] values = line.split("  ");

                pstmt.setString(1, values[0]);
                pstmt.setString(2, values[1]);

                pstmt.executeUpdate();
            }
            System.out.println("Records in Author.tsv loaded successfully.");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader("tsv/Writes.tsv"))) {
            sql = "INSERT INTO Writes (isbn, aid) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] values = line.split("  ");

                pstmt.setString(1, values[0]);
                pstmt.setString(2, values[1]);

                pstmt.executeUpdate();
            }
            System.out.println("Records in Writes.tsv loaded successfully.");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reset_database(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            String[] drop_tables = {
                    "DROP TABLE Orders;",
                    "DROP TABLE Writes;",
                    "DROP TABLE Author;",
                    "DROP TABLE Book;",
                    "DROP TABLE Customer;" };
            for (String sql : drop_tables) {
                stmt.executeUpdate(sql);
            }
            System.out.println("All tables removed.");
            init_tables(conn);
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}