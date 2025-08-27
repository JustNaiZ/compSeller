package compSeller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class WorkerInterface {
    private final MainFrame mainFrame;
    private final Connection connection;
    private final int workerId; // Добавлена переменная для ID работника

    public WorkerInterface(MainFrame mainFrame, Connection connection, int workerId) {
        this.mainFrame = mainFrame;
        this.connection = connection;
        this.workerId = workerId; // Инициализация ID работника
    }

    public void showWorkerInterface() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(50));
        JLabel label = new JLabel("                             Интерфейс работника");
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));

        /*JButton manageClientsButton = new JButton("Управление клиентами");
        manageClientsButton.addActionListener(e -> manageClients());
        panel.add(manageClientsButton);*/

        JButton orderSuppliesButton = new JButton("Заказ товаров у поставщиков");
        orderSuppliesButton.addActionListener(e -> handleOrderSupplies());
        panel.add(orderSuppliesButton);

        JButton viewProductsButton = new JButton("Товары на Складе");
        viewProductsButton.addActionListener(e -> showProducts());
        panel.add(viewProductsButton);

        JButton backButton = new JButton("Выход");
        backButton.addActionListener(e -> mainFrame.showPanel("Welcome", mainFrame.createWelcomePanel()));
        panel.add(Box.createVerticalStrut(20));
        panel.add(backButton);

        mainFrame.showPanel("WorkerInterface", panel);
    }

    /*private void manageClients() {
        JOptionPane.showMessageDialog(mainFrame, "Управление клиентами будет здесь.");
    }*/

    private void handleOrderSupplies() {
        // Открытие интерфейса для выбора товаров у поставщиков
        SupplierOrderInterface supplierOrderInterface = new SupplierOrderInterface(mainFrame, connection, workerId);
        supplierOrderInterface.showSupplierOrderInterface();
    }

    private void showProducts() {
        JDialog productsDialog = new JDialog(mainFrame, "Товары", true);
        JPanel productsPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Название продукта", "Количество на складе"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try {
            Statement stmt = mainFrame.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT product_id, product_name, stock_quantity FROM products");

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                int stockQuantity = rs.getInt("stock_quantity");
                tableModel.addRow(new Object[]{id, productName, stockQuantity});
            }

            JTable productsTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(productsTable);
            productsPanel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при получении данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        productsDialog.add(productsPanel);
        productsDialog.pack();
        productsDialog.setLocationRelativeTo(mainFrame);
        productsDialog.setVisible(true);
    }

    public void updateOrderStatus(int clientId, String newStatus) {
        String query = "UPDATE orders SET status = ? WHERE client_id = ?";
        try (PreparedStatement stmt = mainFrame.getConnection().prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, clientId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(mainFrame, "Статус заказа обновлен!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при обновлении статуса заказа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
