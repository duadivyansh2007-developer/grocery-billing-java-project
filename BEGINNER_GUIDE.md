# Beginner Guide — Grocery Billing System

Student: **DIVYANSH DUA**
Roll No: **PIET25CS050**
Project: **Grocery Billing & Inventory Management System**

This Java Swing application uses MySQL through JDBC. Products, customers, suppliers, users, and generated bills are saved in MySQL. The application creates the tables and demo rows after its first successful database connection.

## 1. What you need

1. JDK 17 or newer. JDK 24 is installed on the development computer. Download: https://adoptium.net/
2. IntelliJ IDEA Community Edition. Download: https://www.jetbrains.com/idea/download/
3. MySQL Community Server and MySQL Workbench. During installation, keep port `3306`, remember your MySQL root password, and ensure the MySQL service is started.

## 2. Create the database

1. Open MySQL Workbench and connect to your local MySQL server.
2. Open a new SQL tab and execute:

```sql
CREATE DATABASE IF NOT EXISTS grocery_billing CHARACTER SET utf8mb4;
```

The Java app creates the tables and initial data on first launch. This SQL command creates the database container only.

## 3. Set your local MySQL password

1. In the project folder, copy `database.properties.example` and name the copy `database.properties`.
2. Edit `database.properties`. Replace `YOUR_MYSQL_PASSWORD` with the root password you chose during MySQL installation. Keep the URL and username unchanged unless you changed the MySQL port or user.
3. Do not share or submit `database.properties`. It contains your local database password and is excluded from Git.

## 4. Open in IntelliJ

1. Start IntelliJ IDEA and choose **Open**.
2. Select `C:\Users\dell\Projects\GroceryBillingSystem` and click **Trust** if prompted.
3. IntelliJ should detect `pom.xml`. Click **Load** if prompted. Otherwise open the Maven tool window and click **Reload All Maven Projects**. This downloads MySQL Connector/J.
4. Open **File → Project Structure → Project** and select a JDK 17 or newer.

## 5. Run the application

1. In the Project view, open `src` → `com.piet.grocery` → `Main.java`.
2. Right-click `Main.java` and choose **Run 'Main.main()'**.
3. When the database connection works, the first run creates tables and sample rows, then opens Login.

Demo accounts:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Cashier | `cashier` | `cashier123` |

Select the matching Admin or Cashier option before logging in.
New accounts created through Register are Cashier accounts. Admin accounts can only be created from User Management by an existing Admin.

## 6. Screenshots for your submission

1. Login: show the Admin/Cashier options and demo hint.
2. Register: open Register and show the form.
3. Dashboard: show the header, count cards, and toolbar.
4. Inventory: show the products table and add a sample product.
5. Billing: add products to cart, generate a bill, and show the receipt.
6. Customers: show table and Add/Update/Delete controls.
7. Suppliers: show table and form.
8. Users: log in as Admin and show the user table.
9. Change Password: show the password form.
10. Logout: show that logout returns to Login.

On Windows, press `Win + Shift + S` to take screenshots.

## 7. Common problems

**Database Connection Failed / Unknown database**  
**Database Connection Failed / Unknown database**
Check that MySQL Server is running, execute the SQL in section 2, and verify that `database.properties` is beside `pom.xml` and has the correct password.

**Access denied for user `root`**  
**Access denied for user `root`**
The password in `database.properties` does not match your MySQL root password. Correct it and run `Main` again.

**Maven or Connector/J appears missing in IntelliJ**  
**Maven or Connector/J appears missing in IntelliJ**
Reload Maven projects and ensure the computer has internet access for the initial dependency download.

**Cannot resolve Java symbols**  
**Cannot resolve Java symbols**
Check **File → Project Structure → Project** and select an installed JDK.

**Users menu is missing**  
**Users menu is missing**
Only Admin sees User Management. Log in using the Admin role.

## 8. Database tables

The app creates `products`, `customers`, `suppliers`, `users`, `bills`, and `bill_items`. In Workbench, refresh **Schemas → grocery_billing → Tables** to inspect them. CRUD changes are saved in MySQL. Generating a bill saves the bill and its lines, and reduces inventory stock.

This is a classroom project, not production software. Demo passwords are stored as plain text in the beginner implementation; do not use real or reused passwords or real customer data.
