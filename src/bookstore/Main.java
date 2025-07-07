package bookstore;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        Button adminBtn = new Button("Admin Login");

        adminBtn.setOnAction(e -> {
            AdminView admin = new AdminView();
            admin.start(new Stage());
        });

        root.getChildren().addAll(adminBtn); 
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bookstore Main Menu");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
