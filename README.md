## Getting Started

After installing MySQL Server, run the following lines of code in MySQL:

```sql
CREATE USER 'user'@'localhost' IDENTIFIED BY 'user';
GRANT ALL PRIVILEGES ON *.* TO 'user'@'localhost';
CREATE DATABASE book_ordering_system;
```

## Development

Please implement Customer Operation, Bookstore Operation, Print Database Overview and Change shipping status.
Debug the application with different user inputs.
You should write your code in a separate Java file and call it from the main program (BookOrderingSystem.java).

## Report

Please complete the report below. Describe the functions implemented briefly.
You may want to include the SQL statements and screenshots.<br>

---

## Relational Schema

Customer(<ins>uid, cname, address</ins>)\
Book(<ins>isbn</ins>, title, price, inventory_quantity)\
Orders(<ins>oid, uid, isbn</ins>, order_date, order_quantity, shipping_status)\
Author(<ins>aid</ins>, aname)\
Writes(<ins>isbn, aid</ins>)

- We assume the same customer can place multiple orders at the same time,
  where each order contains one isbn, and they all have the same oid.

- The customer cannot place multiple orders of the same isbn at the sam time.

- Every author has an id, therefore a book may have authors with the same name (with different aid's).

## Functions

### Database Initialization

1. Initialize the database\
    The program creates all the necessary tables by executing the following SQL statement:
   <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   CREATE TABLE Customer
           (uid CHAR(10) not NULL,
            name CHAR(50) not NULL,
            address CHAR(200) not NULL,
            PRIMARY KEY ( uid ));
   CREATE TABLE Book
           (isbn CHAR(13) not NULL,
            title CHAR(100) not NULL,
            price INTEGER,
            inventory_quantity INTEGER,
            PRIMARY KEY ( isbn ));
   CREATE TABLE Orders
           (oid CHAR(8) not NULL,
            uid CHAR(10) not NULL,
            isbn CHAR(13) not NULL,
            order_date DATE,
            order_quantity INTEGER,
            shipping_status CHAR(8),
            FOREIGN KEY ( uid ) REFERENCES Customer( uid ),
            FOREIGN KEY ( isbn ) REFERENCES Book( isbn ),
            PRIMARY KEY ( oid, uid, isbn ));
   CREATE TABLE Author
           (aid CHAR(10) not NULL,
            aname CHAR(50) not NULL,
            PRIMARY KEY ( aid ));
   CREATE TABLE Writes
           (isbn CHAR(13) not NULL,
            aid CHAR(10) not NULL,
            FOREIGN KEY ( isbn ) REFERENCES Book( isbn ),
            FOREIGN KEY ( aid ) REFERENCES Author( aid ),
            PRIMARY KEY ( isbn, aid ))
   ```

    </details>

2. Load Init Records\
   The program loads the records in the tsv files to the corresponding tables.

3. Reset Database\
   The program resets the database by dropping all the tables then creating the tables again.
    <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   DROP TABLE Orders;
   DROP TABLE Writes;
   DROP TABLE Author;
   DROP TABLE Book;
   DROP TABLE Customer;
   ```

    </details>

### Customer Operation

1. Book Search\
   The program allows users to search books by ISBN, book title or author name.  
   <img height="150" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.1.1.JPG"/>  
   <img height="350" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.1.2.JPG"/>  
   <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   SELECT DISTINCT B.isbn, B.title, B.price, B.inventory_quantity 
   FROM Author A, Writes W, Book B 
   WHERE W.aid = A.aid AND W.isbn = B.isbn AND A.aname = keyword
   UNION
   SELECT DISTINCT B.isbn, B.title, B.price, B.inventory_quantity 
   FROM Author A, Writes W, Book B 
   WHERE W.aid = A.aid AND W.isbn = B.isbn AND B.title = keyword
   UNION
   SELECT DISTINCT B.isbn, B.title, B.price, B.inventory_quantity 
   FROM Author A, Writes W, Book B 
   WHERE W.aid = A.aid AND W.isbn = B.isbn AND B.isbn = keyword;
   SELECT DISTINCT A.aname FROM Author A, Writes W WHERE A.aid = W.aid AND W.isbn = isbn;
   ```

   </details>

2. Place an Order\
   User can add different books with different quantity to the order, submit the order or cancel the order.  
   <img height="250" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.2.1.JPG"/>  
   <img height="250" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.2.2.JPG"/>  
   <img height="350" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.2.3.JPG"/>  
   <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   SELECT * FROM Customer C WHERE C.uid = uid;
   SELECT * FROM Book B WHERE B.isbn = isbn;
   SELECT B.inventory_quantity FROM Book B WHERE B.isbn = isbn;
   SELECT DISTINCT O.oid FROM Orders O;
   INSERT INTO Orders (oid, uid, isbn, order_date, order_quantity, shipping_status) 
   VALUES (?, ?, ?, ?, ?, ?);
   ```

   </details>
3. Check History Orders\
   The program allows users to check their history orders by entering their uid.  
   <img height="150" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.3.1.JPG"/>  
   <img height="350" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.3.2.JPG"/>  
   <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   SELECT * FROM Orders O WHERE O.uid = uid;
   ```

   </details>

### Bookstore Operation

1. Order Update\
   The program allows users to update the shipping status of an order.
   <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   UPDATE Orders 
   SET shipping_status = status 
   WHERE O.oid = oid;
   ```

   </details>
2. Order Query\
   The program allows users to query all the order grouped by shipping status.
   <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   SELECT * 
   FROM Orders O 
   WHERE shipping_status = status;
   ```

   </details>
3. N Most Popular Books\
   The program allows users to check the most popular book by entering the number that you want to show.
   <details>
   <summary>Click to show SQL statement</summary>

   ```sql
   SELECT B.isbn, B.title, B.price, COUNT(O.oid) AS num 
   FROM Book B, Orders O 
   WHERE B.isbn = O.isbn 
   GROUP BY B.isbn 
   ORDER BY num DESC 
   LIMIT N;
   ```

   </details>

### Other Utilities

1. Print Current DateTime\
   The system datetime is shown on the main menu.

2. Print Database Overview
3. Control and Navigation\
   The user should input '4' to navigate to the previous page or quit the program.

4. Change shipping status
