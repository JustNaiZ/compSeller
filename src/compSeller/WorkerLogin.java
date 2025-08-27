package compSeller;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkerLogin {
    private final MainFrame mainFrame;
    private final Connection connection;

    public WorkerLogin(MainFrame mainFrame, Connection connection) {
        this.mainFrame = mainFrame;
        this.connection = connection;
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
        JTextField idField = new JTextField();

        nameField.setMaximumSize(new Dimension(200, 30));
        surnameField.setMaximumSize(new Dimension(200, 30));
        patronymicField.setMaximumSize(new Dimension(200, 30));
        idField.setMaximumSize(new Dimension(200, 30));

        panel.add(new JLabel("Имя:"));
        panel.add(nameField);
        panel.add(new JLabel("Фамилия:"));
        panel.add(surnameField);
        panel.add(new JLabel("Отчество:"));
        panel.add(patronymicField);
        panel.add(new JLabel("ID работника:"));
        panel.add(idField);

        JButton submitButton = new JButton("Войти");
        submitButton.addActionListener(e -> handleLogin(nameField, surnameField, patronymicField, idField));

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

        mainFrame.showPanel("WorkerLogin", panel);
    }

    private void handleLogin(JTextField nameField, JTextField surnameField, JTextField patronymicField, JTextField idField) {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String patronymic = patronymicField.getText().trim();
        String workerIdStr = idField.getText().trim();

        if (name.isEmpty() || surname.isEmpty() || patronymic.isEmpty() || workerIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Пожалуйста, заполните все поля.", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int workerId = Integer.parseInt(workerIdStr); // Преобразование workerId в int

            if (isWorkerValid(name, surname, patronymic, workerId)) {
                JOptionPane.showMessageDialog(mainFrame, "Добро пожаловать, " + name + "!");
                WorkerInterface workerInterface = new WorkerInterface(mainFrame, connection, workerId); // Передача mainFrame
                workerInterface.showWorkerInterface();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Неверные данные для входа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Некорректный ID работника.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isWorkerValid(String name, String surname, String patronymic, int workerId) {
        String query = "SELECT * FROM workers WHERE worker_name = ? AND worker_surname = ? AND worker_midname = ? AND worker_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, patronymic);
            stmt.setInt(4, workerId); // Преобразование workerId в int

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
