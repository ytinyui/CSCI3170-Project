# Project Report

## Deployment

Run the following lines in MySQL:

```sql
CREATE USER 'user'@'localhost' IDENTIFIED BY 'user';
GRANT ALL PRIVILEGES ON *.* TO 'user'@'localhost';
CREATE DATABASE book_ordering_system;
```

Open cmd on the directory of the project and run the program.

## Functions

### Database Initialization

1. Initialize the database\
    The program creates all the necessary tables by executing the following SQL statement:

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

   Before initialization:\
   <img src="img/db_init/1.1a.png" width="50%">\
   After initialization:\
   <img src="img/db_init/1.1b.png" width="50%">

2. Load Init Records\
   The program loads the records in the tsv files to the corresponding tables.

   ```sql
   INSERT INTO Customer (uid,name,address) VALUES (?, ?, ?);
   INSERT INTO Book (isbn, title, price, inventory_quantity) VALUES (?, ?, ?, ?);
   INSERT INTO Orders (oid, uid, isbn, order_date, order_quantity, shipping_status) VALUES (?, ?, ?, ?, ?, ?);
   INSERT INTO Author (aid, aname) VALUES (?, ?);
   INSERT INTO Writes (isbn, aid) VALUES (?, ?);
   ```

   <img src="img/db_init/1.2a.png" width="50%">
   <img src="img/db_init/1.2b.png" width="50%">

   The records are successfully inserted as shown.
   Note: the files are placed at ./tsv/, make sure the terminal is at the correct directory.

3. Reset Database\
   The program resets the database by dropping all the tables then creating the tables again.

   ```sql
   DROP TABLE Orders;
   DROP TABLE Writes;
   DROP TABLE Author;
   DROP TABLE Book;
   DROP TABLE Customer;
   ```

   <img src="img/db_init/1.3.png" width="50%">

### Customer Operation

1. Book Search\
   The program allows users to search books by ISBN, book title or author name.  
   <img height="150" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.1.1.JPG"/>  
   <img height="350" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.1.2.JPG"/>

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

2. Place an Order\
   User can add different books with different quantity to the order, submit the order or cancel the order.  
   <img height="250" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.2.1.JPG"/>  
   <img height="250" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.2.2.JPG"/>  
   <img height="350" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.2.3.JPG"/>

   ```sql
   SELECT * FROM Customer C WHERE C.uid = uid;
   SELECT * FROM Book B WHERE B.isbn = isbn;
   SELECT B.inventory_quantity FROM Book B WHERE B.isbn = isbn;
   SELECT DISTINCT O.oid FROM Orders O;
   INSERT INTO Orders (oid, uid, isbn, order_date, order_quantity, shipping_status)
   VALUES (?, ?, ?, ?, ?, ?);
   ```

3. Check History Orders\
   The program allows users to check their history orders by entering their uid.  
   <img height="150" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.3.1.JPG"/>  
   <img height="250" src="https://raw.githubusercontent.com/ytinyui/CSCI3170-Project/master/img/customer_operations/2.3.2.JPG"/>

   ```sql
   SELECT * FROM Orders O WHERE O.uid = uid;
   ```

### Bookstore Operation

1. Order Update\
   The program allows users to update the shipping status of an order.
   <img src="img/BookStoreOperation/3.1.1.png" width="50%">
   <img src="img/BookStoreOperation/3.1.2.png" width="50%">

   ```sql
   UPDATE Orders
   SET shipping_status = status
   WHERE O.oid = oid;
   ```

2. Order Query\
   The program allows users to query all the order grouped by shipping status.
   <img src="img/BookStoreOperation/3.2.png" width="70%">

   ```sql
   SELECT *
   FROM Orders O
   WHERE shipping_status = status;
   ```

3. N Most Popular Books\
   The program allows users to check the most popular book by entering the number that you want to show.\
   <img src="img/BookStoreOperation/3.3.png">

   ```sql
   SELECT B.isbn, B.title, B.price, COUNT(O.oid) AS num
   FROM Book B, Orders O
   WHERE B.isbn = O.isbn
   GROUP BY B.isbn
   ORDER BY num DESC
   LIMIT N;
   ```

### Other Utilities

1. Print Current DateTime\
   The system datetime is shown on the main menu.

2. Print Database Overview
   The system displays the number of records in the Book, Customer, Orders table respectively.

   ```sql
   SELECT COUNT(*) FROM table_name;
   ```

   <img src="img/db_init/1.1b.png" width="50%">

   -1 is displayed if such table does not exist.\
   <img src="img/db_init/1.1a.png" width="50%">

3. Control and Navigation\
   The user should input '4' to navigate to the previous page or quit the program.

4. Change shipping status\
   The system changes the shipping status of the orders from "ordered" to "shipped" every 30 seconds.
