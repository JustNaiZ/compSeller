package compSeller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT category_name FROM product_categories";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        }
        return categories;
    }

    public List<Product> getProductsByCategory(String categoryName) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.buying_price, p.selling_price, p.stock_quantity " +
                "FROM products p " +
                "JOIN product_categories c ON p.category_id = c.category_id " +
                "WHERE c.category_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryName); // Устанавливаем название категории
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("product_id");
                    String name = rs.getString("product_name");
                    double buyingPrice = rs.getDouble("buying_price");
                    double sellingPrice = rs.getDouble("selling_price");
                    int stock = rs.getInt("stock_quantity");

                    products.add(new Product(id, name, buyingPrice, sellingPrice, stock));
                }
            }
        }
        return products;
    }

    public void reduceStockQuantity(int productId, int quantity) throws Exception {
        String updateQuery = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new Exception("Товар с ID " + productId + " не найден или недостаточно на складе.");
            }
        }
    }

    public void updateStockQuantity(int productId, int quantityToAdd) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantityToAdd);
            stmt.setInt(2, productId);

            int affectedRows = stmt.executeUpdate(); // Возвращает количество изменённых строк
            if (affectedRows == 0) {
                throw new SQLException("Не удалось обновить stock_quantity для товара с ID: " + productId);
            }
        }
    }

}
