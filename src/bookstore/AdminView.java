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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminView {
    TableView<UserRecord> userTable = new TableView<>();
    TableView<TransactionRecord> txTable = new TableView<>();
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
        initTx();

        VBox userBox = new VBox(new Label("User Accounts"), userTable);
        VBox txBox = new VBox(new Label("Transactions"), txTable);
        userBox.setStyle("-fx-background-color: #FFD700;");
        txBox.setStyle("-fx-background-color: #FFD700;");
        userBox.setPrefWidth(300);
        txBox.setPrefWidth(300);
        userTable.setPrefHeight(150);
        txTable.setPrefHeight(150);

        salesChart = chartLine("Buying vs. Selling");
        txChart = chartBar("Transactions Per Day");
        salesChart.setPrefSize(320, 150);
        txChart.setPrefSize(320, 150);

        HBox chartsRow = new HBox(10, salesChart, txChart);
        chartsRow.setAlignment(Pos.CENTER);

        HBox tablesRow = new HBox(10, userBox, txBox);
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

        Scene scene = new Scene(root, 720, 540);
        stage.setScene(scene);
        stage.setTitle("Admin View");
        stage.show();
    }

    void initUsers() {
        TableColumn<UserRecord, String> name = new TableColumn<>("Username");
        name.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<UserRecord, String> type = new TableColumn<>("User Type");
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        userTable.getColumns().addAll(name, type);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    void initTx() {
        TableColumn<TransactionRecord, String> title = new TableColumn<>("Textbook Name");
        TableColumn<TransactionRecord, String> price = new TableColumn<>("Price");
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        txTable.getColumns().addAll(title, price);
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
        NumberAxis y = new NumberAxis();
        LineChart<String, Number> line = new LineChart<>(x, y);
        line.setTitle(title);
        return line;
    }

    BarChart<String, Number> chartBar(String title) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        BarChart<String, Number> bar = new BarChart<>(x, y);
        bar.setTitle(title);
        return bar;
    }

    void pullTxtFiles() {
        try {
            InputStream input = getClass().getResourceAsStream("/bookstore/usernames.txt");
            if (input == null) throw new FileNotFoundException("Could not find usernames.txt in resources.");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            List<UserRecord> users = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 3) users.add(new UserRecord(parts[0], parts[2]));
            }
            reader.close();
            userTable.getItems().setAll(users);
            userTable.refresh();
            userCountLabel.setText("Active Users: " + users.size());

            InputStream txInput = getClass().getResourceAsStream("/bookstore/transactions.txt");
            if (txInput == null) throw new FileNotFoundException("Could not find transactions.txt in resources.");
            BufferedReader txReader = new BufferedReader(new InputStreamReader(txInput));
            List<TransactionRecord> txs = new ArrayList<>();
            while ((line = txReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) txs.add(new TransactionRecord(parts[0], parts[1]));
            }
            txReader.close();
            txTable.getItems().setAll(txs);

            timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class UserRecord {
        private final String username;
        private final String type;
        public UserRecord(String u, String t) { username = u; type = t; }
        public String getUsername() { return username; }
        public String getType() { return type; }
    }

    public static class TransactionRecord {
        private final String title;
        private final String price;
        public TransactionRecord(String t, String p) {
            title = t;
            price = p;
        }
        public String getTitle() { return title; }
        public String getPrice() { return price; }
    }
}
