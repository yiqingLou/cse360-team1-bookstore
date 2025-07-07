package bookstore;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminView {
    TableView<UserRecord> userTable = new TableView<>();
    TableView<BookRecord> bookTable = new TableView<>();
    ToggleButton accounts = makeToggle();
    ToggleButton listings = makeToggle();
    ToggleButton purchases = makeToggle();
    Label statusLight = new Label("●");
    Label timeLabel = new Label();
    Label userCountLabel = new Label();
    LineChart<String, Number> salesChart;
    BarChart<String, Number> txChart;

    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #8D2231;");

        HBox top = new HBox(10);
        top.setPadding(new Insets(5));
        top.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(new Image("file:pngegg.png"));
        logo.setFitHeight(30);
        logo.setPreserveRatio(true);
        Label title = new Label("SUN DEVIL TextBooks");
        title.setFont(Font.font("Arial", 16));
        title.setTextFill(Color.GOLD);
        Label dateLabel = new Label("Date Range:");
        dateLabel.setTextFill(Color.GOLD);
        DatePicker picker = new DatePicker();
        Button logout = new Button("Logout");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        top.getChildren().addAll(logo, title, spacer, dateLabel, picker, logout);
        root.setTop(top);

        initUsers();
        initBooks();

        VBox userBox = new VBox(new Label("User Accounts"), userTable);
        VBox bookBox = new VBox(new Label("Available Books"), bookTable);
        userBox.setStyle("-fx-background-color: #FFD700;");
        bookBox.setStyle("-fx-background-color: #FFD700;");
        userBox.setPrefWidth(300);
        bookBox.setPrefWidth(400);
        userTable.setPrefHeight(150);
        bookTable.setPrefHeight(150);

        salesChart = chartLine("Books Sold");
        txChart = chartBar("Sales by Price");
        salesChart.setPrefSize(320, 150);
        txChart.setPrefSize(320, 150);

        HBox chartsRow = new HBox(10, salesChart, txChart);
        chartsRow.setAlignment(Pos.CENTER);

        HBox tablesRow = new HBox(10, userBox, bookBox);
        tablesRow.setAlignment(Pos.CENTER);

        VBox midLayout = new VBox(10, tablesRow, chartsRow);
        midLayout.setPadding(new Insets(10));
        midLayout.setAlignment(Pos.CENTER);
        root.setCenter(midLayout);

        HBox toggles = new HBox(20);
        toggles.setAlignment(Pos.CENTER);
        toggles.setPadding(new Insets(10));
        toggles.getChildren().addAll(toggleBox("Allow New Accounts", accounts),
                                     toggleBox("Allow New Listings", listings),
                                     toggleBox("Allow Purchases", purchases));
        toggles.setStyle("-fx-background-color: #B0B0B0; -fx-border-color: black; -fx-border-width: 1px;");

        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(5));
        bottom.setAlignment(Pos.CENTER_LEFT);

        timeLabel.setTextFill(Color.WHITE);
        userCountLabel.setTextFill(Color.WHITE);
        statusLight.setTextFill(Color.LIME);

        Label sysStatus = new Label("System Status:");
        sysStatus.setTextFill(Color.WHITE);
        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
        Label userDisplay = new Label();
        userDisplay.setTextFill(Color.WHITE);
        userDisplay.textProperty().bind(userCountLabel.textProperty());

        bottom.getChildren().addAll(timeLabel, bottomSpacer, userDisplay, sysStatus, statusLight);

        VBox footer = new VBox(toggles, bottom);
        root.setBottom(footer);

        pullTxtFiles();
        
        salesChart.lookupAll(".axis-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
        txChart.lookupAll(".axis-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
        salesChart.lookupAll(".tick-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
        txChart.lookupAll(".tick-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));


        Scene scene = new Scene(root, 800, 560);
        stage.setScene(scene);
        stage.setTitle("Admin View");
        stage.show();
    }

    void initUsers() {
        TableColumn<UserRecord, String> name = new TableColumn<>("Username");
        name.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<UserRecord, String> pass = new TableColumn<>("Password");
        pass.setCellValueFactory(new PropertyValueFactory<>("password"));
        userTable.getColumns().addAll(name, pass);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    void initBooks() {
        TableColumn<BookRecord, String> title = new TableColumn<>("Title");
        TableColumn<BookRecord, String> author = new TableColumn<>("Author");
        TableColumn<BookRecord, String> price = new TableColumn<>("Price");
        TableColumn<BookRecord, String> condition = new TableColumn<>("Condition");
        TableColumn<BookRecord, String> course = new TableColumn<>("Course");
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        condition.setCellValueFactory(new PropertyValueFactory<>("condition"));
        course.setCellValueFactory(new PropertyValueFactory<>("course"));
        bookTable.getColumns().addAll(title, author, price, condition, course);
    }

    HBox toggleBox(String text, ToggleButton toggle) {
        Label label = new Label(text);
        label.setTextFill(Color.BLACK);
        toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) toggle.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            else toggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        });
        toggle.setSelected(false);
        HBox row = new HBox(5, label, toggle);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    static ToggleButton makeToggle() {
        ToggleButton b = new ToggleButton("OFF");
        b.selectedProperty().addListener((obs, o, n) -> b.setText(n ? "ON" : "OFF"));
        return b;
    }

    LineChart<String, Number> chartLine(String title) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis(0, 10, 1);
        LineChart<String, Number> line = new LineChart<>(x, y);
        line.setTitle(title);
        line.setAnimated(false);
        return line;
    }

    BarChart<String, Number> chartBar(String title) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis(0, 10, 1);
        BarChart<String, Number> bar = new BarChart<>(x, y);
        bar.setTitle(title);
        bar.setAnimated(false);
        return bar;
    }

    void pullTxtFiles() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/usernames.txt"));
            List<UserRecord> users = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) users.add(new UserRecord(parts[0], parts[1]));
            }
            reader.close();
            userTable.getItems().setAll(users);
            userTable.refresh();
            userCountLabel.setText("Active Users: " + users.size());

            BufferedReader bookReader = new BufferedReader(new FileReader("src/books.txt"));
            List<BookRecord> books = new ArrayList<>();
            while ((line = bookReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) books.add(new BookRecord(parts[0], parts[1], parts[2], parts[3], parts[4]));
            }
            bookReader.close();
            bookTable.getItems().setAll(books);

            BufferedReader txReader = new BufferedReader(new FileReader("src/transactions.txt"));
            Map<String, Integer> soldCount = new HashMap<>();
            Map<String, Integer> priceRanges = new TreeMap<>();
            while ((line = txReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    soldCount.put(title, soldCount.getOrDefault(title, 0) + 1);
                    String range = "$" + (int)(price / 10) * 10 + "+";
                    priceRanges.put(range, priceRanges.getOrDefault(range, 0) + 1);
                }
            }
            txReader.close();

            salesChart.getData().clear();
            txChart.getData().clear();

            XYChart.Series<String, Number> soldSeries = new XYChart.Series<>();
            soldSeries.setName("Sold Titles");
            for (Map.Entry<String, Integer> e : soldCount.entrySet()) {
                soldSeries.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
            }
            salesChart.getData().add(soldSeries);

            XYChart.Series<String, Number> priceSeries = new XYChart.Series<>();
            priceSeries.setName("Price Brackets");
            for (Map.Entry<String, Integer> e : priceRanges.entrySet()) {
                priceSeries.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
            }
            txChart.getData().add(priceSeries);
            
            salesChart.lookupAll(".chart-title").forEach(n -> n.setStyle("-fx-text-fill: white;"));
            txChart.lookupAll(".chart-title").forEach(n -> n.setStyle("-fx-text-fill: white;"));
            salesChart.lookupAll(".axis-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
            txChart.lookupAll(".axis-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
            salesChart.lookupAll(".tick-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));
            txChart.lookupAll(".tick-label").forEach(n -> n.setStyle("-fx-text-fill: white;"));


            timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static class UserRecord {
        private final String username;
        private final String password;
        public UserRecord(String u, String p) { username = u; password = p; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }

    public static class BookRecord {
        private final String title, author, price, condition, course;
        public BookRecord(String t, String a, String p, String c, String co) {
            title = t; author = a; price = p; condition = c; course = co;
        }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getPrice() { return price; }
        public String getCondition() { return condition; }
        public String getCourse() { return course; }
    }
}
