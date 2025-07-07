package bookstore;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SellerView {

    private List<Book> listedBooks = new ArrayList<>();
    private VBox bookListDisplay = new VBox(10);
    private Label earningsLabel = new Label("Earnings: $0.00");
    private double totalEarnings = 0.0;

    public void show(Stage stage, MainView mainView) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("Seller Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Input form
        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField priceField = new TextField();
        priceField.setPromptText("Original Price");

        ComboBox<String> conditionBox = new ComboBox<>();
        conditionBox.getItems().addAll("New", "Like New", "Good", "Acceptable");
        conditionBox.setPromptText("Condition");

        TextField departmentField = new TextField();
        departmentField.setPromptText("Department (e.g., CSE)");

        Button listButton = new Button("List Book");
        listButton.setOnAction(e -> {
            try {
                String titleText = titleField.getText();
                String authorText = authorField.getText();
                double originalPrice = Double.parseDouble(priceField.getText());
                String condition = conditionBox.getValue();
                String department = departmentField.getText();

                double finalPrice = calculateFinalPrice(originalPrice, condition);
                Book book = new Book(titleText, authorText, finalPrice, condition + " | Dept: " + department);
                listedBooks.add(book);
                updateBookList();
                updateEarnings();

                titleField.clear();
                authorField.clear();
                priceField.clear();
                conditionBox.setValue(null);
                departmentField.clear();
            } catch (Exception ex) {
                showAlert("Error", "Please fill all fields correctly.");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Title:"), 0, 0);
        form.add(titleField, 1, 0);
        form.add(new Label("Author:"), 0, 1);
        form.add(authorField, 1, 1);
        form.add(new Label("Original Price:"), 0, 2);
        form.add(priceField, 1, 2);
        form.add(new Label("Condition:"), 0, 3);
        form.add(conditionBox, 1, 3);
        form.add(new Label("Department:"), 0, 4);
        form.add(departmentField, 1, 4);
        form.add(listButton, 1, 5);

        // Book Listings Section
        ScrollPane bookScroll = new ScrollPane(bookListDisplay);
        bookScroll.setFitToWidth(true);
        updateBookList();

        // Navigation
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> mainView.show(stage));

        // Assemble everything
        root.getChildren().addAll(title, form, new Label("Your Listings:"), bookScroll, earningsLabel, backButton);

        Scene scene = new Scene(root, 600, 750);
        stage.setScene(scene);
        stage.setTitle("Seller View");
        stage.show();
    }

    private double calculateFinalPrice(double originalPrice, String condition) {
        switch (condition) {
            case "New": return originalPrice * 0.90;
            case "Like New": return originalPrice * 0.80;
            case "Good": return originalPrice * 0.70;
            case "Acceptable": return originalPrice * 0.50;
            default: return originalPrice;
        }
    }

    private void updateBookList() {
        bookListDisplay.getChildren().clear();
        for (Book book : listedBooks) {
            HBox bookCard = new HBox(10);
            bookCard.setPadding(new Insets(10));
            bookCard.setStyle("-fx-border-color: black; -fx-background-color: white;");

            VBox info = new VBox(5);
            info.getChildren().addAll(
                    new Label("Title: " + book.getTitle()),
                    new Label("Author: " + book.getAuthor()),
                    new Label("Price: $" + String.format("%.2f", book.getPrice())),
                    new Label("Condition: " + book.getCondition())
            );

            Button removeBtn = new Button("Unlist");
            removeBtn.setOnAction(e -> {
                listedBooks.remove(book);
                updateBookList();
                updateEarnings();
            });

            bookCard.getChildren().addAll(info, removeBtn);
            bookListDisplay.getChildren().add(bookCard);
        }
    }

    private void updateEarnings() {
        totalEarnings = listedBooks.stream().mapToDouble(Book::getPrice).sum();
        earningsLabel.setText("Earnings: $" + String.format("%.2f", totalEarnings));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
