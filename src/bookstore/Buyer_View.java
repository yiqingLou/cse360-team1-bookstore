package bookstore;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import java.io.BufferedReader;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;



public class Buyer_View extends Application{

private Stage primaryStage;
	
	
	private ObservableList<Book> books = javafx.collections.FXCollections.observableArrayList();
	private ObservableList<Book> cart = javafx.collections.FXCollections.observableArrayList();
	
	private FlowPane bookpane = new FlowPane();
	private FilteredList<Book> filteredBooks;
	private CheckBox Bnew, Bacceptable, Bused, Bworn;
	private Slider priceline;
	private TextField searchbar;
	private TextField dep;
	private ChoiceBox<String> sortbyops;
	private Label status;
	
	@Override
	public void start(Stage primaryStage){
		
		this.primaryStage = primaryStage;
		BView();
	}
	
	
	private void BView(){
		
		BorderPane Screen = new BorderPane();
		Screen.setTop(Navigation());
		//Screen.setBottom();
		Screen.setLeft(Filters());
		//Screen.setRight();
		Screen.setCenter(BookList());
		
		Scene Scene1 = new Scene(Screen, 1000, 800);
		Scene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("Buyer View");
		primaryStage.setScene(Scene1);
		primaryStage.show();
		filteredBooks = new FilteredList<>(books, e -> true);
		bookfileloader("books.txt");
		
		
	}
	
	
	private VBox Navigation(){
		
		
		searchbar = new TextField();
		searchbar.setPromptText("Search: ");;
		searchbar.getStyleClass().add("searchbar");
		
		Button search = new Button("Search");
		Button sell = new Button("Sell");
		Button buy = new Button("Buy");
		Button logout = new Button("Logout");
		Button checkout1 = new Button("Checkout");
		
		search.getStyleClass().add("buttons");
		sell.getStyleClass().add("buttons");
		buy.getStyleClass().add("buttons");
		logout.getStyleClass().add("buttons");
		checkout1.getStyleClass().add("buttons");
		
		buy.setOnAction(e ->{
			BView();	
		});
		
		sell.setOnAction(e ->{
			SellerView sellerView = new SellerView();
			sellerView.show(primaryStage, null);
			
		});
		
		checkout1.setOnAction(e ->{
			Checkout_View checkoutView = new Checkout_View(cart);
			checkoutView.start(new Stage());
			
		});
		logout.setOnAction(e ->{
			primaryStage.close();
			
		}); 
		
		search.setOnAction(e ->{
			filteredBooks.setPredicate(book ->{
				
				//FIXME
				
				boolean conditionsbool = (!Bnew.isSelected() && !Bacceptable.isSelected() && !Bused.isSelected() && !Bworn.isSelected()) ||
					    (Bnew.isSelected() && book.getCondition().equalsIgnoreCase("New")) ||
					    (Bacceptable.isSelected() && book.getCondition().equalsIgnoreCase("Acceptable")) ||
					    (Bused.isSelected() && book.getCondition().equalsIgnoreCase("Used")) ||
					    (Bworn.isSelected() && book.getCondition().equalsIgnoreCase("Worn"));
				
				boolean pricebool = book.getPrice() <= priceline.getValue();
				String deptselect = dep.getText();
				boolean deptbool = deptselect.isEmpty() || book.getDepartment().equalsIgnoreCase(deptselect);
				
				String text = searchbar.getText().toLowerCase();
				boolean searchbool = text.isEmpty() || book.getTitle().toLowerCase().contains(text);
				
				return conditionsbool && pricebool && deptbool && searchbool;
			});
			//sort();
			listbookssorted();
			
		}); 
		
		HBox navbar = new HBox(100, buy, sell, logout, checkout1);
		navbar.setStyle("-fx-alignment: center");
		navbar.getStyleClass().add("navbar");
		
		HBox SearchSection = new HBox(10, searchbar, search);
		SearchSection.setStyle("-fx-alignment: center");
		
		VBox navigation = new VBox(navbar, SearchSection);
		return navigation;
	}
	
	private VBox Filters(){
		
		Label sortby = new Label("Sort By: ");
		sortby.setStyle("-fx-alignment: center");
		sortbyops = new ChoiceBox<>();
		sortbyops.getItems().addAll("Price: High to Low", "Price: Low to High");
		sortbyops.setValue("Price: Low to High");
		sortbyops.setStyle("-fx-alignment: center");
		sortbyops.getStyleClass().add("sortbyops");
		
		Label condition = new Label("Condition:");
		condition.setStyle("-fx-alignment: center");
		 Bnew = new CheckBox("New");
		 Bacceptable = new CheckBox("Acceptable");
		 Bused = new CheckBox("Used");
		 Bworn = new CheckBox("Worn");
		
		HBox conditions = new HBox(5, condition, Bnew, Bacceptable, Bused, Bworn);
		
		
		 priceline = new Slider(0,100,20);
		priceline.setShowTickLabels(true);
		priceline.setShowTickMarks(true);
		priceline.setMajorTickUnit(10);
		priceline.setBlockIncrement(5);
		
		Label price = new Label("Price:");
		
		Label department = new Label("Department");
		 dep = new TextField();
		dep.setPromptText("Ex: CSE360");
		dep.setPrefWidth(200);
		
		Label logo = new Label("SUN DEVIL TEXTBOOKS");
		logo.getStyleClass().add("logo");
		
		//FIXME
		
		status = new Label();
		status.getStyleClass().add("status");
				
		
		Button apply = new Button("Apply");
		
		apply.setOnAction(e ->{
			filteredBooks.setPredicate(book ->{
				
				//FIXME
				
				boolean conditionsbool = (!Bnew.isSelected() && !Bacceptable.isSelected() && !Bused.isSelected() && !Bworn.isSelected()) ||
					    (Bnew.isSelected() && book.getCondition().equalsIgnoreCase("New")) ||
					    (Bacceptable.isSelected() && book.getCondition().equalsIgnoreCase("Acceptable")) ||
					    (Bused.isSelected() && book.getCondition().equalsIgnoreCase("Used")) ||
					    (Bworn.isSelected() && book.getCondition().equalsIgnoreCase("Worn"));
				
				boolean pricebool = book.getPrice() <= priceline.getValue();
				String deptselect = dep.getText();
				boolean deptbool = deptselect.isEmpty() || book.getDepartment().equalsIgnoreCase(deptselect);
				
				return conditionsbool && pricebool && deptbool;
			});
			//sort();
			listbookssorted();
		});
		
		VBox filters = new VBox(10, sortby, sortbyops,
				condition, conditions, 
				price, priceline, department, dep, apply, logo, status);
		filters.setPrefWidth(300);
		filters.setPrefHeight(800);
		filters.getStyleClass().add("filters");
		return filters;
	}

	private ScrollPane BookList(){
		bookpane.setHgap(20);
		bookpane.setVgap(20);
		bookpane.setPadding(new Insets(15));
		

		ScrollPane list = new ScrollPane(bookpane);
		list.setFitToWidth(true);
		list.setStyle("-fx-background-color: transparent;");
		return list;
		
	}
	
	private void bookfileloader(String filename){
		File file = new File(filename);
		if(!file.exists()) {return;}
		
		try(BufferedReader r = new BufferedReader(new FileReader(file))){
			String l;
			while((l = r.readLine())!= null) {
				String[] data = l.split(",");
				String title = data[0];
				String author = data[1];
				double price = Double.parseDouble(data[2]);
				String condition = data[3];
				String department = data[4];
				
				Book book = new Book(title, author, price, condition, department);
				books.add(book);
			}
		}
		catch(IOException e) {
			
		}
		
		listbooks();
		
	}
	
	private void listbooks(){
		
		bookpane.getChildren().clear();
		
		for(Book book : filteredBooks) {
			VBox infocard = new VBox(5);
			infocard.getStyleClass().add("bookcard");
			
			Label title = new Label("Title: " + book.getTitle());
			Label author = new Label("Author: " + book.getAuthor());
			Label price = new Label("Price: " + book.getPrice());
			Label condition = new Label("Condition: " + book.getCondition());
			Label dept = new Label("Department: " + book.getDepartment());
			Button addCart = new Button("Add to Cart");
			
			addCart.setOnAction(e ->{
				
				if(!cart.contains(book)) {
					cart.add(book);
					status.setText(book.getTitle() + " added to Cart");
				}
				else if (cart.contains(book)) {
					status.setText(book.getTitle() + " already in cart");
				}
				
			});
			
			infocard.getChildren().addAll(title, author, price, condition, dept, addCart);
			bookpane.getChildren().add(infocard);
			
		}
		
	}
	
private void listbookssorted(){
		
		bookpane.getChildren().clear();
		
		ObservableList<Book> sortedbooks = javafx.collections.FXCollections.observableArrayList();
		
		String option = sortbyops.getValue();
		
		if(option.equals("Price: High to Low")){
			for(Book b : filteredBooks) {
				int i = 0;
				while(i < sortedbooks.size() && b.getPrice() < sortedbooks.get(i).getPrice()) {
					i++;
				}
				sortedbooks.add(i,b);
			}
		}
		else if(option.equals("Price: Low to High")){
			for(Book b : filteredBooks) {
				int i = 0;
				while(i < sortedbooks.size() && b.getPrice() > sortedbooks.get(i).getPrice()) {
					i++;
				}
				sortedbooks.add(i,b);
			}
		}
		/*else if(option.equals("Condition: Best to Worst")){
	
		}
		else if(option.equals("Condition: Worst to Best")){
	
		}*/
		
	
		
		for(Book book : sortedbooks) {
			VBox infocard = new VBox(5);
			infocard.getStyleClass().add("bookcard");
			
			Label title = new Label("Title: " + book.getTitle());
			Label author = new Label("Author: " + book.getAuthor());
			Label price = new Label("Price: " + book.getPrice());
			Label condition = new Label("Condition: " + book.getCondition());
			Label dept = new Label("Department: " + book.getDepartment());
			Button addCart = new Button("Add to Cart");
			
			addCart.setOnAction(e ->{
				
				if(!cart.contains(book)) {
					cart.add(book);
					status.setText(book.getTitle() + " added to Cart");
				} 
				else if (cart.contains(book)) {
					status.setText(book.getTitle() + " already in cart");
				}
				
			});
			
			infocard.getChildren().addAll(title, author, price, condition, dept, addCart);
			bookpane.getChildren().add(infocard);
			
		}
		
	}
	
	/*private SortedList<Book> sort(){
		
		SortedList<Book> sorted = new SortedList<>(filteredBooks);
		
		String option = sortbyops.getValue();
		if(option.equals("Price: High to Low")){
		
		}
		else if(option.equals("Price: Low to High")){
			
		}
		else if(option.equals("Condition: Best to Worst")){
	
		}
		else if(option.equals("Condition: Worst to Best")){
	
		}
		
	}
	*/
	
	
	
	public static void main(String[] args){ 
		launch(args);
		}
	
}
