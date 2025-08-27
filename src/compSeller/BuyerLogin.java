package compSeller;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BuyerLogin {
    private final MainFrame mainFrame;
    private final Connection connection;
    private final ProductDAO productDAO; // новое поле для хранения объекта ProductDAO

    public BuyerLogin(MainFrame mainFrame, Connection connection) {
        this.mainFrame = mainFrame;
        this.connection = connection;
        this.productDAO = new ProductDAO(connection); // инициализируем ProductDAO с помощью connection
    }

    public void showLoginScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(20));
        JLabel formLabel = new JLabel("Введите свои данные:");
        formLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        formLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(formLabel);

        panel.add(Box.createVerticalStrut(20));

        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField patronymicField = new JTextField();

        nameField.setMaximumSize(new Dimension(200, 30));
        surnameField.setMaximumSize(new Dimension(200, 30));
        patronymicField.setMaximumSize(new Dimension(200, 30));

        panel.add(new JLabel("Имя:"));
        panel.add(nameField);
        panel.add(new JLabel("Фамилия:"));
        panel.add(surnameField);
        panel.add(new JLabel("Отчество:"));
        panel.add(patronymicField);

        JButton submitButton = new JButton("Войти");
        submitButton.addActionListener(e -> handleLogin(nameField, surnameField, patronymicField));

        JButton backButton = new JButton("Назад");
        backButton.addActionListener(e -> mainFrame.showPanel("Welcome", mainFrame.createWelcomePanel()));

        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Горизонтальное размещение с отступом 10px
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        // Добавляем панель кнопок в основной panel
        panel.add(Box.createVerticalStrut(20)); // Отступ сверху
        panel.add(buttonPanel);

        mainFrame.showPanel("BuyerLogin", panel);
    }

    private void handleLogin(JTextField nameField, JTextField surnameField, JTextField patronymicField) {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String patronymic = patronymicField.getText().trim();

        if (name.isEmpty() || surname.isEmpty() || patronymic.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Пожалуйста, заполните все поля.", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clientId = handleBuyerLogin(name, surname, patronymic);

        if (clientId != -1) {
            JOptionPane.showMessageDialog(mainFrame, "Добро пожаловать, " + name + "!");
            BuyerInterface buyerInterface = new BuyerInterface(mainFrame, productDAO, clientId);
            buyerInterface.showBuyerInterface();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при входе или регистрации.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int handleBuyerLogin(String clientName, String clientSurname, String clientMidname) {
        String checkClientSql = "SELECT client_id FROM clients WHERE client_name = ? AND client_surname = ? AND client_midname = ?";
        String insertClientSql = "INSERT INTO clients (client_name, client_surname, client_midname, number_card, address_name) VALUES (?, ?, ?, NULL, '')";
        int clientId = -1;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkClientSql)) {
            checkStmt.setString(1, clientName);
            checkStmt.setString(2, clientSurname);
            checkStmt.setString(3, clientMidname);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) { // Клиент найден
                clientId = rs.getInt("client_id");
                System.out.println("Existing buyer ID: " + clientId);
            } else { // Клиент не найден, создаем нового
                try (PreparedStatement insertStmt = connection.prepareStatement(insertClientSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, clientName);
                    insertStmt.setString(2, clientSurname);
                    insertStmt.setString(3, clientMidname);
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        clientId = generatedKeys.getInt(1);
                        System.out.println("New buyer added with ID: " + clientId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientId;
    }
}


