//
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class App extends Application {
    private static String DB_URL = "jdbc:mysql://localhost:3306/Batch";
    private static String DB_USER = "root";
    private static String DB_PASSWORD = "root";

    private static  int records = 1000;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Batch Update Comparison");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Database Connection Panel
        Label driverLabel = new Label("JDBC Driver:");
        ComboBox<String> driverComboBox = new ComboBox<>();
        driverComboBox.getItems().addAll("com.mysql.cj.jdbc.Driver"); 

        Label urlLabel = new Label("Database URL:");
        TextField urlTextField = new TextField(DB_URL);

        Label usernameLabel = new Label("Username:");
        TextField usernameTextField = new TextField(DB_USER);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setText(DB_PASSWORD);

        Label seconds = new Label("Batch Time:");
        Label secondss = new Label("non-batch time:");


        Button connectButton = new Button("Connect");
        connectButton.setOnAction(e -> {
            String selectedDriver = driverComboBox.getValue();
            String dbUrl = urlTextField.getText();
            String username = usernameTextField.getText();
            String password = passwordField.getText();

            // Connect to the database using the provided credentials
            try {
                Class.forName(selectedDriver);
                Connection connection = DriverManager.getConnection(dbUrl, username, password);
                connection.setAutoCommit(false);

                // Perform batch updates and measure time
                long start = System.currentTimeMillis();
                performBatchInsert(connection);
                long end = System.currentTimeMillis();
                long elapsedTime = end - start;
                seconds.setText("Batch Time: " + elapsedTime + " ms");
                

                // Close the connection
                connection.close();
            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            }
        });

        grid.add(driverLabel, 0, 0);
        grid.add(driverComboBox, 1, 0);
        grid.add(urlLabel, 0, 1);
        grid.add(urlTextField, 1, 1);
        grid.add(usernameLabel, 0, 2);
        grid.add(usernameTextField, 1, 2);
        grid.add(passwordLabel, 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(connectButton, 0, 4, 2, 1);
        grid.add(seconds, 2, 3);
        grid.add(secondss, 3, 3);

        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void performBatchInsert(Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

        Random random = new Random();

        for (int i = 0; i < records; i++) {
            preparedStatement.setDouble(1, random.nextDouble());
            preparedStatement.setDouble(2, random.nextDouble());
            preparedStatement.setDouble(3, random.nextDouble());
            preparedStatement.addBatch();
        }

        // Execute the batch update
        preparedStatement.executeBatch();
        connection.commit();
        preparedStatement.close();
    }
        public static void main(String[] args) {
        launch(args);
    }
}
