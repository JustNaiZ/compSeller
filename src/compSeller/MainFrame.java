package compSeller;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    private final BuyerLogin buyerLogin;
    private final WorkerLogin workerLogin;
    private final Connection connection;

    private final Map<String, JDialog> dialogs = new HashMap<>(); // Сохранение диалогов по их названию

    public MainFrame(Connection connection) {
        this.connection = connection;

        /*setTitle("Информационная система учета и продаж компьютерной техники");*/
        setTitle("СomputerSeller");
        setSize(800, 370);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Устанавливаем иконку
        setAppIcon();

        buyerLogin = new BuyerLogin(this, connection);
        workerLogin = new WorkerLogin(this, connection);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        mainPanel.add(createWelcomePanel(), "Welcome");

        getContentPane().add(mainPanel);
    }

    private void setAppIcon() {
        // Путь к иконке
        try {
            // Путь иконки в ресурсах проекта
            URL iconUrl = getClass().getResource("/resources/32.png");
            if (iconUrl != null) {
                Image icon = Toolkit.getDefaultToolkit().getImage(iconUrl);
                setIconImage(icon);
            } else {
                System.out.println("Иконка не найдена!");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке иконки: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Добро пожаловать в магазин комплектующих и компьютеров!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(100));
        panel.add(welcomeLabel);

        JButton workerButton = new JButton("Войти как Работник");
        workerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        workerButton.addActionListener(e -> workerLogin.showLoginScreen());

        JButton buyerButton = new JButton("Войти как Покупатель");
        buyerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyerButton.addActionListener(e -> buyerLogin.showLoginScreen());

        panel.add(Box.createVerticalStrut(20));
        panel.add(workerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buyerButton);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    public void showPanel(String panelName, JPanel panel) {
        mainPanel.add(panel, panelName);
        cardLayout.show(mainPanel, panelName);
    }

    // Метод для получения диалога по его названию
    public JDialog getDialog(String dialogName) {
        return dialogs.get(dialogName);
    }

    // Метод для добавления диалога в карту
    public void addDialog(String dialogName, JDialog dialog) {
        dialogs.put(dialogName, dialog);
    }
}
