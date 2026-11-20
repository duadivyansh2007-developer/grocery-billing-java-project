package com.piet.grocery.data;

import com.piet.grocery.model.BillItem;
import com.piet.grocery.model.Customer;
import com.piet.grocery.model.Product;
import com.piet.grocery.model.Supplier;
import com.piet.grocery.model.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.awt.Component;
import javax.swing.JOptionPane;

public class DummyStore {
    private static final DummyStore INSTANCE = new DummyStore();
    private final List<Product> products = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<UserAccount> users = new ArrayList<>();
    private int nextProductId = 1;
    private int nextCustomerId = 1;
    private int nextSupplierId = 1;
    private int nextUserId = 1;

    private DummyStore() {
    }

    public static void initialize() throws SQLException {
        Database.initializeSchema();
        INSTANCE.seedInitialData();
        INSTANCE.loadFromDatabase();
    }

    public static DummyStore get() { return INSTANCE; }
    public List<Product> getProducts() { return products; }
    public List<Customer> getCustomers() { return customers; }
    public List<Supplier> getSuppliers() { return suppliers; }
    public List<UserAccount> getUsers() { return users; }
    public int nextProductId() { return nextProductId++; }
    public int nextCustomerId() { return nextCustomerId++; }
    public int nextSupplierId() { return nextSupplierId++; }
    public int nextUserId() { return nextUserId++; }

    public UserAccount findUser(String username, String password, String role) {
        for (UserAccount user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)
                    && user.getRole().equalsIgnoreCase(role)) return user;
        }
        return null;
    }

    public UserAccount findByUsername(String username) {
        for (UserAccount user : users) if (user.getUsername().equalsIgnoreCase(username)) return user;
        return null;
    }

    public Product findProduct(int id) {
        for (Product product : products) if (product.getId() == id) return product;
        return null;
    }

    public boolean persistChanges(Component parent) {
        try {
            saveChanges();
            return true;
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(parent, "Could not save changes to MySQL.\n" + exception.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public synchronized void saveChanges() throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                clear(connection, "users");
                clear(connection, "suppliers");
                clear(connection, "customers");
                clear(connection, "products");
                saveProducts(connection);
                saveCustomers(connection);
                saveSuppliers(connection);
                saveUsers(connection);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public synchronized void recordBill(Customer customer, double[] totals, List<BillItem> items)
            throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long billId;
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO bills (customer_name, subtotal, gst, discount, total) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, customer == null ? "Walk-in Customer" : customer.getName());
                    statement.setDouble(2, totals[0]);
                    statement.setDouble(3, totals[1]);
                    statement.setDouble(4, totals[2]);
                    statement.setDouble(5, totals[3]);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("MySQL did not return a bill number.");
                        billId = keys.getLong(1);
                    }
                }
                try (PreparedStatement line = connection.prepareStatement(
                        "INSERT INTO bill_items (bill_id, product_name, quantity, unit_price, gst_percent, line_total) VALUES (?, ?, ?, ?, ?, ?)");
                     PreparedStatement stock = connection.prepareStatement(
                             "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?")) {
                    for (BillItem item : items) {
                        Product product = item.getProduct();
                        line.setLong(1, billId);
                        line.setString(2, product.getName());
                        line.setInt(3, item.getQuantity());
                        line.setDouble(4, product.getUnitPrice());
                        line.setDouble(5, product.getGstPercent());
                        line.setDouble(6, item.getLineTotal());
                        line.executeUpdate();
                        stock.setInt(1, item.getQuantity());
                        stock.setInt(2, product.getId());
                        stock.setInt(3, item.getQuantity());
                        if (stock.executeUpdate() != 1) throw new SQLException("Insufficient stock for " + product.getName());
                    }
                }
                connection.commit();
                for (BillItem item : items) {
                    Product product = findProduct(item.getProduct().getId());
                    if (product != null) product.setQuantity(product.getQuantity() - item.getQuantity());
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void seedInitialData() throws SQLException {
        try (Connection connection = Database.getConnection()) {
            if (empty(connection, "products")) {
                try (PreparedStatement s = connection.prepareStatement("INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    Object[][] rows = {{1,"Aashirvaad Atta 5kg","Grains",40,285.0,5,"pkt"},{2,"Amul Toned Milk 1L","Dairy",80,56.0,5,"ltr"},{3,"Tata Salt 1kg","Spices",60,28.0,5,"pkt"},{4,"Fortune Sunflower Oil 1L","Oils",35,145.0,5,"ltr"},{5,"Maggi Noodles 70g","Snacks",120,14.0,12,"pkt"}};
                    for (Object[] row : rows) { for (int i=0;i<row.length;i++) s.setObject(i+1,row[i]); s.addBatch(); }
                    s.executeBatch();
                }
            }
            if (empty(connection, "customers")) {
                try (PreparedStatement s = connection.prepareStatement("INSERT INTO customers VALUES (?, ?, ?, ?)")) {
                    Object[][] rows = {{1,"Walk-in Customer","0000000000","Counter"},{2,"Rahul Sharma","9876543210","Jaipur"},{3,"Neha Gupta","9123456780","Mansarovar"}};
                    for (Object[] row : rows) { for (int i=0;i<row.length;i++) s.setObject(i+1,row[i]); s.addBatch(); }
                    s.executeBatch();
                }
            }
            if (empty(connection, "suppliers")) {
                try (PreparedStatement s = connection.prepareStatement("INSERT INTO suppliers VALUES (?, ?, ?, ?, ?)")) {
                    Object[][] rows = {{1,"Rajasthan Wholesale Mart","0141-2200111","Jaipur","Grains, Oils"},{2,"Amul Distributor","0141-3344556","Jaipur","Dairy"},{3,"Local Kirana Supply","9829012345","Sitapura","Snacks, Spices"}};
                    for (Object[] row : rows) { for (int i=0;i<row.length;i++) s.setObject(i+1,row[i]); s.addBatch(); }
                    s.executeBatch();
                }
            }
            if (empty(connection, "users")) {
                try (PreparedStatement s = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?)")) {
                    Object[][] rows = {{1,"Divyansh Dua","admin","admin123","Admin"},{2,"Store Cashier","cashier","cashier123","Cashier"}};
                    for (Object[] row : rows) { for (int i=0;i<row.length;i++) s.setObject(i+1,row[i]); s.addBatch(); }
                    s.executeBatch();
                }
            }
        }
    }

    private void loadFromDatabase() throws SQLException {
        products.clear(); customers.clear(); suppliers.clear(); users.clear();
        try (Connection c = Database.getConnection(); Statement s = c.createStatement()) {
            try (ResultSet r = s.executeQuery("SELECT * FROM products ORDER BY id")) {
                while (r.next()) products.add(new Product(r.getInt("id"),r.getString("name"),r.getString("category"),r.getInt("quantity"),r.getDouble("unit_price"),r.getDouble("gst_percent"),r.getString("unit")));
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM customers ORDER BY id")) {
                while (r.next()) customers.add(new Customer(r.getInt("id"),r.getString("name"),r.getString("phone"),r.getString("address")));
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM suppliers ORDER BY id")) {
                while (r.next()) suppliers.add(new Supplier(r.getInt("id"),r.getString("name"),r.getString("phone"),r.getString("city"),r.getString("items_supplied")));
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM users ORDER BY id")) {
                while (r.next()) users.add(new UserAccount(r.getInt("id"),r.getString("full_name"),r.getString("username"),r.getString("password"),r.getString("role")));
            }
        }
        nextProductId=products.stream().mapToInt(Product::getId).max().orElse(0)+1;
        nextCustomerId=customers.stream().mapToInt(Customer::getId).max().orElse(0)+1;
        nextSupplierId=suppliers.stream().mapToInt(Supplier::getId).max().orElse(0)+1;
        nextUserId=users.stream().mapToInt(UserAccount::getId).max().orElse(0)+1;
    }

    private void saveProducts(Connection c) throws SQLException {
        try (PreparedStatement s=c.prepareStatement("INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            for(Product p:products){s.setInt(1,p.getId());s.setString(2,p.getName());s.setString(3,p.getCategory());s.setInt(4,p.getQuantity());s.setDouble(5,p.getUnitPrice());s.setDouble(6,p.getGstPercent());s.setString(7,p.getUnit());s.addBatch();} s.executeBatch();
        }
    }
    private void saveCustomers(Connection c) throws SQLException {
        try (PreparedStatement s=c.prepareStatement("INSERT INTO customers VALUES (?, ?, ?, ?)")) {
            for(Customer x:customers){s.setInt(1,x.getId());s.setString(2,x.getName());s.setString(3,x.getPhone());s.setString(4,x.getAddress());s.addBatch();} s.executeBatch();
        }
    }
    private void saveSuppliers(Connection c) throws SQLException {
        try (PreparedStatement s=c.prepareStatement("INSERT INTO suppliers VALUES (?, ?, ?, ?, ?)")) {
            for(Supplier x:suppliers){s.setInt(1,x.getId());s.setString(2,x.getName());s.setString(3,x.getPhone());s.setString(4,x.getCity());s.setString(5,x.getItemsSupplied());s.addBatch();} s.executeBatch();
        }
    }
    private void saveUsers(Connection c) throws SQLException {
        try (PreparedStatement s=c.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?)")) {
            for(UserAccount x:users){s.setInt(1,x.getId());s.setString(2,x.getFullName());s.setString(3,x.getUsername());s.setString(4,x.getPassword());s.setString(5,x.getRole());s.addBatch();} s.executeBatch();
        }
    }
    private boolean empty(Connection c,String table) throws SQLException {
        try(Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM "+table)){r.next();return r.getInt(1)==0;}
    }
    private void clear(Connection c,String table) throws SQLException {
        try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM "+table);}
    }
}
