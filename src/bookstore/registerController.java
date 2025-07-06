package bookstore;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javafx.scene.control.Alert;

public class registerController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    public void handleCreate(){
        String usernamePrompt = usernameField.getText();
        String passwordPrompt = passwordField.getText();

        //writes info into text file called "info"
        try(BufferedWriter writeInfo = new BufferedWriter(new FileWriter("usernames.txt", true))){
            writeInfo.write(usernamePrompt + ":" + passwordPrompt);
            writeInfo.newLine();

            //informs user that account is created and goes back to login page
            Alert accountCreatedAlert = new Alert(Alert.AlertType.CONFIRMATION);
            accountCreatedAlert.setTitle("Account created");
            accountCreatedAlert.setContentText(("Account created"));
            accountCreatedAlert.showAndWait();
            goBack();
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }

    public void goBack(){
        try {
            FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
            Scene scene = new Scene(registerLoader.load(), 320, 240);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
