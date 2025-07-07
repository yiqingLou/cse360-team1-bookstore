package bookstore;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class loginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;


    @FXML
    public void handleSubmit(ActionEvent event) throws IOException {
        String line = null;
        String promptUsername = usernameField.getText();
        String promptPassword = passwordField.getText();
        boolean successfulLogin = false;

        try(BufferedReader readInfo = new BufferedReader(new FileReader("usernames.txt"))){
            while((line = readInfo.readLine()) != null){
                if(line.equals(promptUsername + ":" + promptPassword)){
                    successfulLogin = true;
                    break;
                }
            }
            if(successfulLogin == true){
                loadBookstore();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Wrong username or password.");
                alert.showAndWait();
            }
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }

    public void loadBookstore() throws IOException{
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BuyerView.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        Buyer_View buyerView = new Buyer_View();
        buyerView.start(stage);
    }

    public void handleRegister(ActionEvent event) {
        try {
            FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("registerView.fxml"));
            Scene scene = new Scene(registerLoader.load(), 320, 240);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Create Account");
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
