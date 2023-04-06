import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomerOperation {
    
    public static void book_search(Connection conn, String keyword) {
        try{
            // Search the keyword by matching it to ISBN, book title and author name and return distinct ISBN
            Statement stmt = conn.createStatement();
            String sql_str = "SELECT DISTINCT B.isbn, B.title, B.price, B.inventory_quantity FROM Author A, Writes W, Book B WHERE W.aid = A.aid AND W.isbn = B.isbn AND A.aname = \"" + keyword +"\"" + "UNION "
                                + "SELECT DISTINCT B.isbn, B.title, B.price, B.inventory_quantity FROM Author A, Writes W, Book B WHERE W.aid = A.aid AND W.isbn = B.isbn AND B.title = \"" + keyword +"\"" + "UNION "
                                + "SELECT DISTINCT B.isbn, B.title, B.price, B.inventory_quantity FROM Author A, Writes W, Book B WHERE W.aid = A.aid AND W.isbn = B.isbn AND B.isbn = \"" + keyword +"\";";
            ResultSet rs = stmt.executeQuery(sql_str);
            if(!rs.isBeforeFirst()){
                //if no search result
                System.out.println("--------------------------------");
                System.out.println("No Result Found");
                System.out.println("--------------------------------");
            }else{
                while(rs.next()){
                    //print search result
                    String isbn = rs.getString(1);
                    String title = rs.getString(2);
                    String price = rs.getString(3);
                    String inventory_quantity = rs.getString(4);
                    System.out.println("--------------------------------");
                    System.out.println("ISBN: " + isbn);
                    System.out.println("Title: " + title);
                    //search for authors
                    Statement a_Statement = conn.createStatement();
                    String sql_4_author_name = "SELECT DISTINCT A.aname FROM Author A, Writes W WHERE A.aid = W.aid AND W.isbn = \"" + isbn + "\";"; 
                    ResultSet author_set = a_Statement.executeQuery(sql_4_author_name);
                    System.out.print("Author(s): ");
                    author_set.next();
                    while(true){
                        System.out.print(author_set.getString(1));
                        if(author_set.next()){
                            System.out.print(", ");
                        }else{
                            break;
                        }
                    }
                    System.out.print("\n");
                    System.out.println("Price: " + price);
                    System.out.println("Inventory Quantity: " + inventory_quantity);
                }
                System.out.println("--------------------------------");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void place_an_order(Connection conn){
        Scanner scanner = new Scanner(System.in);
        // Uid checking
        System.out.print("Please Input Your UID: ");
        String uid = scanner.nextLine();
        if(!check_user(conn, uid)){
            System.out.println("User Does Not Exist, Order Ended.");
            // scanner.close();
            return;
        }
        
        // Allocate a new oid
        String oid = allocate_an_oid(conn);
        // Add item, finish order or cancel the order
        int choice = 0;
        List<String> isbn_list = new ArrayList<String>();
        List<Integer> quantity_list = new ArrayList<Integer>();

        while(true){
            System.out.println("-----Order Operations-----");
            System.out.println("> 1. Add Items to the Order.");
            System.out.println("> 2. Submit the Order.");
            System.out.println("> 3. Cancel the Order.");
            System.out.print(">>> Please Input Your Query: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    System.out.println("-----Add Items to the Order-----");
                    System.out.print(">>> Please Input ISBN: ");
                    String isbn = scanner.nextLine();
                    // Check if the book exists
                    if(!check_isbn(conn, isbn)){
                        System.out.println("Book Not Found, Returned to the Last Page.");
                        break;
                    }
                    System.out.print(">>> Please Input the Quantity You Wish to Purchase: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();
                    // Check if the quantity is valid
                    if(quantity <= 0){
                        System.out.println("Invalid Quantity, Returned to the Last Page");
                        break;
                    }
                    // Check if the storage is larger than purchase quantity
                    if(!check_quantity(conn, isbn, quantity)){
                        System.out.println("Order Failed Caused by Inventory Shortage, Returned to the Last Page.");
                        break;
                    }
                    isbn_list.add(isbn);
                    quantity_list.add(quantity);
                    System.out.println("Successfully Added Item to Order.");
                    break;
                case 2:
                    if(isbn_list.isEmpty()){
                        System.out.println("You Have No Item in Your Order Yet.");
                        // scanner.close();
                        return;
                    }
                    for(int i=0;i<isbn_list.size();i++){
                        process_order(conn, uid, oid, isbn_list.get(i), quantity_list.get(i));
                    }
                    System.out.println("You Have Placed Your Order Successfully, the ID of Your Order is " + oid);
                    // scanner.close();
                    return;
                case 3:
                    System.out.println("Order Canceled.");
                    // scanner.close();
                    return;
                default:
                    System.out.println("Invalid Choice.");
            }
        }
    }

    public static void process_order(Connection conn, String uid, String oid, String isbn, int quantity){
        try{
            // Insert a new item to the database
            String sql = "INSERT INTO Orders (oid, uid, isbn, order_date, order_quantity, shipping_status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, oid);
            pstmt.setString(2, uid);
            pstmt.setString(3, isbn);
            pstmt.setDate(4, Date.valueOf(LocalDate.now()));
            pstmt.setInt(5, quantity);
            pstmt.setString(6, "ordered");
            pstmt.executeUpdate();
            // Get and update the inventory
            Statement i_stmt = conn.createStatement();
            String i_check_str = "SELECT B.inventory_quantity FROM Book B WHERE B.isbn = \"" + isbn + "\";";
            ResultSet i_rs = i_stmt.executeQuery(i_check_str);
            i_rs.next();
            int inventory_quantity = i_rs.getInt(1);
            String u_str = "UPDATE Book SET inventory_quantity = " + Integer.toString(inventory_quantity - quantity) + " WHERE isbn = \"" + isbn + "\";";
            Statement u_stmt = conn.createStatement();
            u_stmt.execute(u_str);
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean check_user(Connection conn, String uid){
        try{
            // Check if the user exist
            Statement u_stmt = conn.createStatement();
            String u_check_str = "SELECT * FROM Customer C WHERE C.uid = \"" + uid + "\";";
            ResultSet u_rs = u_stmt.executeQuery(u_check_str);
            if(!u_rs.isBeforeFirst()){
                return false;
            }else{
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean check_isbn(Connection conn, String isbn){
        try{
            // Check if the book exist
            Statement b_stmt = conn.createStatement();
            String b_check_str = "SELECT * FROM Book B WHERE B.isbn = \"" + isbn + "\";";
            ResultSet b_rs = b_stmt.executeQuery(b_check_str);
            if(!b_rs.isBeforeFirst()){
                return false;
            }else{
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean check_quantity(Connection conn, String isbn, int quantity){
        try{
            // Check if the purchase quantity exceeds the inventory quantity
            Statement i_stmt = conn.createStatement();
            String i_check_str = "SELECT B.inventory_quantity FROM Book B WHERE B.isbn = \"" + isbn + "\";";
            ResultSet i_rs = i_stmt.executeQuery(i_check_str);
            i_rs.next();
            int inventory_quantity = i_rs.getInt(1);
            if(inventory_quantity < quantity){
                return false;
            }else{
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String allocate_an_oid(Connection conn){
        try{
            Statement o_stmt = conn.createStatement();
            String o_str = "SELECT DISTINCT O.oid FROM Orders O;";
            ResultSet o_rs = o_stmt.executeQuery(o_str);
            int max_oid = 0;
            while(o_rs.next()){
                String oid = o_rs.getString(1);
                if(Integer.parseInt(oid.substring(1)) > max_oid){
                    max_oid = Integer.parseInt(oid.substring(1));
                }
            }
            return "o" + String.format("%03d", max_oid + 1);
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void check_history_orders(Connection conn, String uid){
        // Check user
        if(!check_user(conn, uid)){
            System.out.println("User Does Not Exist.");
            return;
        }
        // Check history order
        try{
            Statement h_stmt = conn.createStatement();
            String h_str = "SELECT * FROM Orders O WHERE O.uid = \"" + uid + "\";";
            ResultSet h_rs = h_stmt.executeQuery(h_str);
            ResultSetMetaData metaData = h_rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            int[] columnWidths = new int[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnWidths[i - 1] = metaData.getColumnDisplaySize(i);
            }
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-" + columnWidths[i - 1] + "s \t", metaData.getColumnLabel(i));
            }
            System.out.println();
            while (h_rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-" + columnWidths[i - 1] + "s \t", h_rs.getString(i));
                }
                System.out.println();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
