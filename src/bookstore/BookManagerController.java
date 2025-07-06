package bookstore;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;

public class BookManagerController {
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private ComboBox<String> categoryBox;
    @FXML
    private ComboBox<String> conditionBox;
    @FXML
    private ListView<String> bookListView;

    private ObservableList<String> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        categoryBox.setItems(FXCollections.observableArrayList("Textbook", "Novel", "Magazine", "Others"));
        conditionBox.setItems(FXCollections.observableArrayList("New", "Like New", "Used", "Worn"));
        loadBooks();
    }

    @FXML
    public void handleAddBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryBox.getValue();
        String condition = conditionBox.getValue();

        if (title.isEmpty() || author.isEmpty() || category == null || condition == null) {
            showAlert("Missing info", "Please fill all fields.");
            return;
        }

        String record = title + "," + author + "," + category + "," + condition;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt", true))) {
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bookList.add(record);
        clearFields();
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        categoryBox.getSelectionModel().clearSelection();
        conditionBox.getSelectionModel().clearSelection();
    }

    private void loadBooks() {
        bookList.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                bookList.add(line);
            }
        } catch (IOException e) {
        }
        bookListView.setItems(bookList);
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
