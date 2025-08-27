package compSeller;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierOrderInterface {
    private final MainFrame mainFrame;
    private final ProductDAO productDAO;
    private final Connection connection;
    private final int workerId; // ID работника
    private JPanel productPanel;
    private final List<SupplierCartItem> supplierCartItems; // Используем новый класс

    public SupplierOrderInterface(MainFrame mainFrame, Connection connection, int workerId) {
        this.mainFrame = mainFrame;
        this.connection = connection;
        this.productDAO = new ProductDAO(connection);
        this.workerId = workerId; // Инициализация ID работника
        this.supplierCartItems = new ArrayList<>();
    }

    public void showSupplierOrderInterface() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель с выбором категории и кнопками
        JPanel topPanel = createTopPanel();
        panel.add(topPanel, BorderLayout.NORTH);

        // Панель для отображения товаров
        productPanel = createProductPanel();
        panel.add(new JScrollPane(productPanel), BorderLayout.CENTER);

        // Кнопка "Посмотреть корзину"
        JButton viewCartButton = new JButton("Посмотреть корзину");
        viewCartButton.addActionListener(e -> showCart());
        panel.add(viewCartButton, BorderLayout.SOUTH);

        // Кнопка "Назад" для возврата в главное меню
        JButton backButton = new JButton("Назад");
        backButton.addActionListener(e -> {
            WorkerInterface workerInterface = new WorkerInterface(mainFrame, connection, workerId);
            workerInterface.showWorkerInterface();
        });

        panel.add(backButton, BorderLayout.SOUTH);

        mainFrame.showPanel("SupplierOrderInterface", panel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel categoryLabel = new JLabel("Выберите категорию:");
        JComboBox<String> categoryComboBox = new JComboBox<>();
        JButton refreshButton = new JButton("Обновить");

        // Заполнение категорий
        try {
            List<String> categories = productDAO.getAllCategories();
            for (String category : categories) {
                categoryComboBox.addItem(category);
            }
            if (categoryComboBox.getItemCount() == 0) {
                categoryComboBox.addItem("Нет доступных категорий");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при загрузке категорий.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        // Кнопка "Обновить" обновляет товары
        refreshButton.addActionListener(e -> updateProductPanel(categoryComboBox));

        topPanel.add(categoryLabel);
        topPanel.add(categoryComboBox);
        topPanel.add(refreshButton);
        return topPanel;
    }

    private JPanel createProductPanel() {
        return new JPanel(new GridLayout(0, 4)); // отображение в 4 колонки
    }

    private void updateProductPanel(JComboBox<String> categoryComboBox) {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        try {
            List<Product> products = productDAO.getProductsByCategory(selectedCategory);
            productPanel.removeAll();
            for (Product product : products) {
                JButton productButton = new JButton(
                        "<html>" + product.getProductName() +
                                "<br>Закупочная цена: " + product.getBuyingPrice()
                                + "</html>"
                );
                productButton.setPreferredSize(new Dimension(100, 100));
                productButton.addActionListener(event -> addProductToOrder(product));
                productPanel.add(productButton);
            }
            productPanel.revalidate();
            productPanel.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при загрузке товаров.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProductToOrder(Product product) {
        // Диалог для выбора количества и добавления товара в заказ
        String quantityStr = JOptionPane.showInputDialog(mainFrame, "Введите количество товара:", "Добавить в заказ", JOptionPane.PLAIN_MESSAGE);
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(mainFrame, "Количество должно быть больше 0.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Логика добавления товара в заказ
            SupplierCartItem existingItem = findCartItem(product);
            if (existingItem != null) {
                existingItem.increaseQuantity(quantity);
            } else {
                supplierCartItems.add(new SupplierCartItem(product, quantity, workerId)); // Добавляем ID работника
            }

            // Обновление stock_quantity в базе данных
            try {
                productDAO.updateStockQuantity(product.getProductId(), quantity);
                JOptionPane.showMessageDialog(mainFrame, "Товар \"" + product.getProductName() + "\" в количестве " + quantity + " добавлен в заказ.", "Успешно", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainFrame, "Введите корректное количество.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            JOptionPane.showMessageDialog(mainFrame, "Количество на складе обновлено.", "Успешно", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка обновления склада: " + e.getMessage(),"Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCart() {
        JPanel cartPanel = new JPanel(new GridLayout(0, 1)); // отображение в 1 колонку
        for (SupplierCartItem item : supplierCartItems) {
            cartPanel.add(new JLabel(item.getProduct().getProductName() + " - Количество: " + item.getQuantity()));
        }
        JOptionPane.showMessageDialog(mainFrame, new JScrollPane(cartPanel), "Корзина", JOptionPane.INFORMATION_MESSAGE);
    }

    private SupplierCartItem findCartItem(Product product) {
        for (SupplierCartItem item : supplierCartItems) {
            if (item.getProduct().equals(product)) {
                return item;
            }
        }
        return null;
    }
}

class SupplierCartItem {
    private final Product product;
    private int quantity;

    public SupplierCartItem(Product product, int quantity, int ownerId) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity(int additionalQuantity) {
        this.quantity += additionalQuantity;
    }

}
