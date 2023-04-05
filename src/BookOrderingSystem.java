import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class BookOrderingSystem {
   public static void main(String[] args) {

      Connection conn = null;
      Statement stmt = null;
      try {
         Class.forName("com.mysql.cj.jdbc.Driver");

         System.out.println("Connecting to database...");
         conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_ordering_system?autoReconnect=true",
               "user", "user");
         stmt = conn.createStatement();
         Scanner scanner = new Scanner(System.in);
         int choice = 0;
         int subChoice = 0;

         while (choice != 4) {
            // Print the main menu
            int book_count = DatabaseOverview.count(conn, "Book");
            int customer_count = DatabaseOverview.count(conn, "Customer");
            int order_count = DatabaseOverview.count(conn, "Orders");
            System.out.println("===== Welcome to Book Ordering Management System =====");
            System.out.printf(" + System Date: %s%n", LocalDate.now().toString());
            System.out.println(" + Database Records: Books (" + book_count +
                  "), Customers (" + customer_count +
                  "), Orders (" + order_count + ")");
            System.out.println("--------------------------");
            System.out.println(" > 1. Database Initialization");
            System.out.println(" > 2. Customer Operation");
            System.out.println(" > 3. Bookstore Operation");
            System.out.println(" > 4. Quit");
            System.out.print(">>> Please Enter Your Query: ");

            BookStoreOperation.updateShippingStatus(conn);

            // Read the user's choice
            choice = scanner.nextInt();

            switch (choice) {
               case 1:
                  // sub-menu for database init
                  subChoice = 0;
                  // System.out.println("Database Initialization selected.");
                  while (subChoice != 4) {
                     System.out.println(" > 1. Initialize Tables");
                     System.out.println(" > 2. Load Init Records");
                     System.out.println(" > 3. Reset Database");
                     System.out.println(" > 4. Back to Main Menu");
                     System.out.print(">>> Please Enter Your Query: ");

                     // Read the user's choice
                     subChoice = scanner.nextInt();

                     switch (subChoice) {
                        case 1:
                           // Init tables
                           InitDatabase.init_tables(conn);
                           break;
                        case 2:
                           // Load records
                           InitDatabase.load_records(conn);
                           break;
                        case 3:
                           // Reset database
                           InitDatabase.reset_database(conn);
                           break;
                        case 4:
                           // Back to main menu
                           break;
                        default:
                           System.out.println("Invalid choice.");
                     }
                  }
                  break;
               case 2:
                  // Show the sub-menu for customer operation
                  subChoice = 0;

                  while (subChoice != 4) {
                     System.out.println("----Customer Operations----");
                     System.out.println(" > 1. Book Search");
                     System.out.println(" > 2. Place an Order");
                     System.out.println(" > 3. Check History Orders");
                     System.out.println(" > 4. Back to Main Menu");
                     System.out.print(">>> Please Enter Your Query: ");

                     // Read the user's choice
                     subChoice = scanner.nextInt();
                     scanner.nextLine();

                     switch (subChoice) {
                        case 1:
                           // Ask the user to enter a keyword for searching
                           System.out.print(">>> Please Enter ISBN, Book Title or Author Name for Searching: ");
                           String keyword = scanner.nextLine();
                           CustomerOperation.book_search(conn, keyword);
                           break;
                        case 2:
                           CustomerOperation.place_an_order(conn);
                           break;
                        case 3:
                           System.out.print(">>> Please Enter Your UID: ");
                           String uid = scanner.nextLine();
                           CustomerOperation.check_history_orders(conn, uid);
                           break;
                        case 4:
                           // Back to main menu
                           break;
                        default:
                           System.out.println("Invalid choice.");
                     }
                  }
                  break;
               case 3:
                  // Show the sub-menu for bookstore operation
                  subChoice = 0;

                  while (subChoice != 4) {
                     System.out.println(" > 1. Order Update");
                     System.out.println(" > 2. Order Query");
                     System.out.println(" > 3. N Most Popular Books");
                     System.out.println(" > 4. Back to Main Menu");
                     System.out.print(">>> Please Enter Your Query: ");

                     // Read the user's choice
                     subChoice = scanner.nextInt();

                     switch (subChoice) {
                        case 1:
                           System.out.println("Order Update selected.");
                           BookStoreOperation.OrderUpdate(conn);
                           break;
                        case 2:
                           System.out.println("Order Query selected.");
                           BookStoreOperation.OrderQuery(conn);
                           break;
                        case 3:
                           System.out.println("N Most Popular Books selected.");
                           System.out.print(">>>Please input number: ");
                           int N = scanner.nextInt();
                           BookStoreOperation.MostPopular(conn, N);
                           break;
                        case 4:
                           // Back to main menu
                           break;
                        default:
                           System.out.println("Invalid choice.");
                     }
                  }
                  break;
               case 4:
                  System.out.println("Quitting the program.");
                  break;
               default:
                  System.out.println("Invalid choice.");
            }
         }

         scanner.close();

      } catch (SQLException se) {
         se.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (stmt != null)
               conn.close();
         } catch (SQLException se) {
         }
         try {
            if (conn != null)
               conn.close();
         } catch (SQLException se) {
            se.printStackTrace();
         }
      }
      System.out.println("Goodbye!");
      System.exit(0);
   }
}
