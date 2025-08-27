//скорее всего это для работника и его самостоятельное обновление статуса заказа
/*package compSeller;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderManager {
    private final Connection connection;

    public OrderManager(Connection connection) {
        this.connection = connection;
    }

    public void updateOrderStatus(int clientId, String newStatus) {
        String query = "UPDATE orders SET status = ? WHERE client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, clientId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Статус заказа обновлен!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при обновлении статуса заказа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void placeOrder(int productId, String cardNumber, String address) {
        String query = "INSERT INTO purchases (product_id, card_number, address) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setString(2, cardNumber);
            stmt.setString(3, address);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Заказ успешно оформлен!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при оформлении заказа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
*/