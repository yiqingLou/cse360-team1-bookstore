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
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminView {
    private TableView<UserRecord> userTable = new TableView<>();
    private TableView<TransactionRecord> transactionTable = new TableView<>();
    private ToggleButton accountsToggle = makeToggle();
    private ToggleButton listingsToggle = makeToggle();
    private ToggleButton purchasesToggle = makeToggle();
    private Label statusLight = new Label("●");
    private Label clockLabel = new Label();
    private Label activeUserCountLabel = new Label();
    private LineChart<String, Number> salesLineChart;
    private BarChart<String, Number> barChart;

    public void start(Stage window) {
        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: #8D2231;");

        // top bar
        HBox top = new HBox(10);
        top.setPadding(new Insets(5));
        top.setAlignment(Pos.CENTER_LEFT);

        ImageView forkLogo = new ImageView(new Image("file:pngegg.png"));
        forkLogo.setFitHeight(30);
        forkLogo.setPreserveRatio(true);

        Label appTitle = new Label("SUN DEVIL TextBooks");
        appTitle.setFont(Font.font("Arial", 16));
        appTitle.setTextFill(Color.GOLD);

        Label dateLabel = new Label("Date Range:");
        dateLabel.setTextFill(Color.GOLD);
        DatePicker picker = new DatePicker();
        Button logoutBtn = new Button("Logout");

        Region growSpacer = new Region();
        HBox.setHgrow(growSpacer, Priority.ALWAYS);

        top.getChildren().addAll(forkLogo, appTitle, growSpacer, dateLabel, picker, logoutBtn);
        mainPane.setTop(top);

        // middle section
        GridPane mid = new GridPane();
        mid.setPadding(new Insets(5));
        mid.setHgap(5);
        mid.setVgap(5);

        initUserTable();
        initTransactionTable();

        VBox userBox = new VBox(new Label("User Accounts"), userTable);
        VBox txBox = new VBox(new Label("Transactions"), transactionTable);
        userBox.setStyle("-fx-background-color: #FFD700;");
        txBox.setStyle("-fx-background-color: #FFD700;");
        userBox.setPrefWidth(250);
        txBox.setPrefWidth(250);

        VBox configPanel = new VBox(5);
        configPanel.getChildren().addAll(makeToggleBox("Allow New Accounts", accountsToggle),
                                         makeToggleBox("Allow New Listings", listingsToggle),
                                         makeToggleBox("Allow Purchases", purchasesToggle));
        configPanel.setPadding(new Insets(5));
        configPanel.setStyle("-fx-background-color: #B0B0B0; -fx-border-color: black; -fx-border-width: 1px;");

        salesLineChart = makeLineChart("Buying vs. Selling");
        barChart = makeBarChart("Transactions Per Day");
        salesLineChart.setPrefSize(300, 120);
        barChart.setPrefSize(300, 120);

        mid.add(userBox, 0, 0);
        mid.add(txBox, 1, 0);
        mid.add(configPanel, 0, 1);
        mid.add(salesLineChart, 1, 1);
        mid.add(barChart, 1, 2);

        // footer
        HBox footer = new HBox(10);
        footer.setPadding(new Insets(5));
        footer.setAlignment(Pos.CENTER_LEFT);

        clockLabel.setTextFill(Color.WHITE);
        activeUserCountLabel.setTextFill(Color.WHITE);
        statusLight.setTextFill(Color.LIME);

        Label statusText = new Label("System Status:");
        statusText.setTextFill(Color.WHITE);
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        Label activeUsersDisplay = new Label();
        activeUsersDisplay.setTextFill(Color.WHITE);
        activeUsersDisplay.textProperty().bind(activeUserCountLabel.textProperty());

        footer.getChildren().addAll(clockLabel, footerSpacer, activeUsersDisplay, statusText, statusLight);

        mainPane.setCenter(mid);
        mainPane.setBottom(footer);

        loadData();

        Scene scene = new Scene(mainPane, 700, 450);
        window.setScene(scene);
        window.setTitle("Admin View");
        window.show();
    }

    private void initUserTable() {
        TableColumn<UserRecord, String> nameCol = new TableColumn<>("Username");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userTable.getColumns().add(nameCol);
        userTable.setPrefHeight(120);
    }

    private void initTransactionTable() {
        TableColumn<TransactionRecord, String> isbn = new TableColumn<>("ISBN");
        TableColumn<TransactionRecord, String> email = new TableColumn<>("Email");
        TableColumn<TransactionRecord, String> title = new TableColumn<>("Textbook Name");

        isbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));

        transactionTable.getColumns().addAll(isbn, email, title);
        transactionTable.setPrefHeight(120);
    }

    private HBox makeToggleBox(String labelText, ToggleButton toggle) {
        Label label = new Label(labelText);
        label.setTextFill(Color.BLACK);
        toggle.selectedProperty().addListener((obs, was, now) -> {
            if (now) toggle.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            else toggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        });
        toggle.setSelected(false);
        HBox box = new HBox(5, label, toggle);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private static ToggleButton makeToggle() {
        ToggleButton btn = new ToggleButton("OFF");
        btn.selectedProperty().addListener((obs, oldV, newV) -> btn.setText(newV ? "ON" : "OFF"));
        return btn;
    }

    private LineChart<String, Number> makeLineChart(String title) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(x, y);
        chart.setTitle(title);
        return chart;
    }

    private BarChart<String, Number> makeBarChart(String title) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(title);
        return chart;
    }

    private void loadData() {
        try {
            List<String> userLines = Files.readAllLines(Paths.get("usernames.txt"));
            List<UserRecord> users = userLines.stream().map(UserRecord::new).collect(Collectors.toList());
            userTable.getItems().setAll(users);
            activeUserCountLabel.setText("Active Users: " + users.size());

            List<String> txLines = Files.readAllLines(Paths.get("transactions.txt"));
            List<TransactionRecord> tx = txLines.stream().map(TransactionRecord::fromLine).filter(Objects::nonNull).collect(Collectors.toList());
            transactionTable.getItems().setAll(tx);

            clockLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class UserRecord {
        private final String username;
        public UserRecord(String u) { this.username = u; }
        public String getUsername() { return username; }
    }

    public static class TransactionRecord {
        private final String isbn, email, title;
        public TransactionRecord(String isbn, String email, String title) {
            this.isbn = isbn;
            this.email = email;
            this.title = title;
        }

        public static TransactionRecord fromLine(String line) {
            String[] parts = line.split(",");
            if (parts.length < 3) return null;
            return new TransactionRecord(parts[0], parts[1], parts[2]);
        }

        public String getIsbn() { return isbn; }
        public String getEmail() { return email; }
        public String getTitle() { return title; }
    }
}
