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
            System.out.println("===== Welcome to Book Ordering Management System =====");
            System.out.printf(" + System Date: %s%n", LocalDate.now().toString());
            System.out.println(" + Database Records: Books (999), Customers (999), Orders (999)");
            System.out.println("--------------------------");
            System.out.println(" > 1. Database Initialization");
            System.out.println(" > 2. Customer Operation");
            System.out.println(" > 3. Bookstore Operation");
            System.out.println(" > 4. Quit");
            System.out.print(">>> Please Enter Your Query: ");

            // Read the user's choice
            choice = scanner.nextInt();

            switch (choice) {
               case 1:
                  // sub-menu for database init
                  subChoice = 0;
                  System.out.println("Database Initialization selected.");
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
                     System.out.println(" > 1. Book Search");
                     System.out.println(" > 2. Place an Order");
                     System.out.println(" > 3. Check History Orders");
                     System.out.println(" > 4. Back to Main Menu");
                     System.out.print(">>> Please Enter Your Query: ");

                     // Read the user's choice
                     subChoice = scanner.nextInt();

                     switch (subChoice) {
                        case 1:
                           System.out.println("Book search selected.");
                           break;
                        case 2:
                           System.out.println("Place an order selected.");
                           break;
                        case 3:
                           System.out.println("Check history orders selected.");
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
                           break;
                        case 2:
                           System.out.println("Order Query selected.");
                           break;
                        case 3:
                           System.out.println("N Most Popular Books selected.");
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
   }
}
