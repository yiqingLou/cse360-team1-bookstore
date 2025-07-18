package bookstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BookManagerView.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Book Management");
        primaryStage.show();
    } */
    
    public class Main extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loginView.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();
        }

    public static void main(String[] args) {
        launch(args);
    }
}

/*package bookstore;

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
*/
