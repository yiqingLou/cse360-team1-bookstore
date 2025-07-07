package application;


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
import javafx.geometry.Pos;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class Checkout_View extends Application{
	
	private ObservableList<Book> cart;
	
	Book[] history;
	
	public Checkout_View(ObservableList<Book> cart1) {
		this.cart = cart1;
		history = new Book[100];
		int purchasedcount = 0;
	}
	

		private Stage primaryStage;
		@Override
	
		
		
			public void start(Stage primaryStage){
			this.primaryStage = primaryStage;
				VBox Screen2 = new VBox(10);
				Screen2.setAlignment(Pos.CENTER);
				Screen2.getStyleClass().add("checkout");
				double total = 0;
				Label cout = new Label("Summary of Purchase");
				cout.getStyleClass().add("cout");
				
				VBox books = new VBox(10);
				books.getStyleClass().add("checkbooks");
				
				for(Book book : cart) {
					Label newBook = new Label(book.getTitle() + ": $ " + book.getPrice());
					newBook.getStyleClass().add("cartItem");
					books.getChildren().add(newBook);
					total = total + book.getPrice();
				}
				
				Label total2 = new Label("Total Owed - $ " + String.format("%.2f",total));
				total2.getStyleClass().add("total2");
				
				Button purch = new Button("Purchase");
				purch.getStyleClass().add("checkoutbutton");
				purch.setOnAction(e -> {
					
					//FIXME
					for(Book book : cart) {
						int i = 0;
						while (i < cart.size()){
							history[i] = cart.get(i);
							i++;
						}
					}
					
					cart.clear();
					total2.setText("Thank you for your Purchase!");
				});
			
				Screen2.getChildren().addAll(cout, books, total2, purch);
			
	
				Scene scene = new Scene(Screen2, 1000,800);	
			
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

				primaryStage.setTitle("Checkout");
				primaryStage.setScene(scene);
				primaryStage.show();
		}
		
		public Book[] purchaseHistory(){
			return history;
		}
			
}
