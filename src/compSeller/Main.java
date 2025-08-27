package compSeller;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {

            // Соединение с базой данных
            String url = "jdbc:postgresql://localhost:5432/compSeller";
            String user = "postgres";
            String password = "postgresql";
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful!");

            // Создаем и показываем главное окно
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(connection);
                mainFrame.setVisible(true);
            });

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
