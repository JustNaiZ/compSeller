package compSeller;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuyerInterface {
    private final MainFrame mainFrame;
    private final ProductDAO productDAO;
    private JPanel productPanel;
    private final int currentClientId;
    private final List<CartItem> cart;

    public BuyerInterface(MainFrame mainFrame, ProductDAO productDAO, int clientId) {
        this.mainFrame = mainFrame;
        this.productDAO = productDAO;
        this.currentClientId = clientId;
        this.cart = new ArrayList<>();
    }

    public void showBuyerInterface() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель с выбором категории
        JPanel topPanel = createTopPanel();
        panel.add(topPanel, BorderLayout.NORTH);

        // Панель для отображения товаров в виде квадратов
        productPanel = createProductPanel();
        panel.add(new JScrollPane(productPanel), BorderLayout.CENTER);

        // Кнопка для возврата в главное меню
        JButton backButton = new JButton("Назад");
        backButton.addActionListener(e -> mainFrame.showPanel("Welcome", mainFrame.createWelcomePanel()));
        panel.add(backButton, BorderLayout.SOUTH);

        mainFrame.showPanel("BuyerInterface", panel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel categoryLabel = new JLabel("Выберите категорию:");
        JComboBox<String> categoryComboBox = new JComboBox<>();
        JButton refreshButton = new JButton("Обновить");
        JButton viewCartButton = new JButton("Посмотреть корзину");

        // Заполнение категорий
        try {
            List<String> categories = productDAO.getAllCategories();
            for (String category : categories) {
                categoryComboBox.addItem(category);
            }
            if (categoryComboBox.getItemCount() == 0) {
                categoryComboBox.addItem("Нет доступных категорий");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при загрузке категорий.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        refreshButton.addActionListener(e -> updateProductPanel((String) categoryComboBox.getSelectedItem()));
        viewCartButton.addActionListener(e -> showCart());

        topPanel.add(categoryLabel);
        topPanel.add(categoryComboBox);
        topPanel.add(refreshButton);
        topPanel.add(viewCartButton);
        return topPanel;
    }

    private JPanel createProductPanel() {
        return new JPanel(new GridLayout(0, 4)); // отображение в 4 колонки
    }

    private void updateProductPanel(String category) {
        try {
            List<Product> products = productDAO.getProductsByCategory(category);
            productPanel.removeAll();
            for (Product product : products) {
                JPanel productCard = createProductCard(product);
                productPanel.add(productCard);
            }
            productPanel.revalidate();
            productPanel.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при загрузке товаров.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        /*card.setBorder(BorderFactory.createLineBorder(Color.BLACK));*/

        /*JLabel nameLabel = new JLabel(product.getProductName());
        JLabel priceLabel = new JLabel("Цена: " + product.getSellingPrice());*/
        JButton viewDetailsButton = /*new JButton("Подробнее");*/
        new JButton(
                "<html>" + product.getProductName() +
                        "<br>Цена: " + product.getSellingPrice()
                        + "</html>"
        );

        viewDetailsButton.addActionListener(e -> showProductDetails(product));

        /*card.add(nameLabel);
        card.add(priceLabel);*/
        card.add(viewDetailsButton);

        return card;
    }

    private void showProductDetails(Product product) {
        JDialog detailsDialog = new JDialog(mainFrame, product.getProductName(), true);
        JPanel detailsPanel = new JPanel(new GridLayout(3, 2));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Название: " + product.getProductName());
        JLabel priceLabel = new JLabel("    Цена: " + product.getSellingPrice());

        JTextField quantityField = new JTextField();
        quantityField.setMaximumSize(new Dimension(100, 20));

        JButton addToCartButton = new JButton("Добавить в корзину");
        addToCartButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity > 0) {
                    addToCart(product, quantity);
                    detailsDialog.dispose(); // закрываем окно деталей после добавления
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Количество должно быть больше 0.", "Ошибка", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Введите корректное количество.", "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        detailsPanel.add(nameLabel);
        detailsPanel.add(priceLabel);
        detailsPanel.add(new JLabel("Количество:"));
        detailsPanel.add(quantityField);
        detailsPanel.add(addToCartButton);

        detailsDialog.add(detailsPanel);
        detailsDialog.pack();
        detailsDialog.setLocationRelativeTo(mainFrame);
        detailsDialog.setVisible(true);
    }

    private void addToCart(Product product, int quantity) {
        // Проверка существующего товара в корзине по product_id
        CartItem existingItem = null;
        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cart.add(new CartItem(product, quantity));
        }
    }

    private void showCart() {
        JDialog cartDialog = new JDialog(mainFrame, "Корзина", true);
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));

        if (cart.isEmpty()) {
            JLabel emptyCartLabel = new JLabel("Корзина пуста.");
            cartPanel.add(emptyCartLabel);
        } else {
            double totalAmount = 0;

            // Визуальная часть старого метода
            for (CartItem item : cart) {
                JLabel itemLabel = new JLabel(item.getProduct().getProductName() + " | Количество: " + item.getQuantity() + " | Стоимость: " +
                        (item.getProduct().getSellingPrice() * item.getQuantity()) + " руб.");
                cartPanel.add(itemLabel);
                totalAmount += item.getProduct().getSellingPrice() * item.getQuantity();
            }

            // Визуальная часть нового метода
            JLabel totalLabel = new JLabel("Итоговая сумма: " + totalAmount + " руб.");
            JButton checkoutButton = new JButton("Оформить заказ");
            checkoutButton.addActionListener(e -> {
                if (cart.isEmpty()) {
                    JOptionPane.showMessageDialog(cartDialog, "Ваша корзина пуста. Пожалуйста, добавьте товары перед оформлением заказа.", "Предупреждение", JOptionPane.WARNING_MESSAGE);
                } else {
                    processCheckout();
                    cartDialog.dispose(); // закрыть окно корзины после оформления заказа
                }
            });

            cartPanel.add(totalLabel);
            cartPanel.add(checkoutButton);
        }

        cartDialog.add(new JScrollPane(cartPanel));
        cartDialog.pack();
        cartDialog.setLocationRelativeTo(mainFrame);
        cartDialog.setVisible(true);
    }

    private void processCheckout() {
        try {
            for (CartItem item : cart) {
                productDAO.reduceStockQuantity(item.getProduct().getProductId(), item.getQuantity());
            }

            JOptionPane.showMessageDialog(mainFrame, "Заказ успешно оформлен.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            cart.clear(); // очищаем корзину после оформления заказа
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при оформлении заказа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}