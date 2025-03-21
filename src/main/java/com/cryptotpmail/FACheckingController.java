package com.cryptotpmail;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FACheckingController {
    

    @FXML
    private PasswordField authenticationField;
    @FXML
    private Label resultFALabel;
    @FXML
    private Button FaSendBtn;
    @FXML
    private Pane pane;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;

    private Image image = new Image(getClass().getResourceAsStream("logo.png"));
    String username, Fafield;

    
    // Fonctions 
    // Personnalise le logo de l'interface
    public void setLogo(Stage stage) {
        try {
            Image logo = image;
            stage.getIcons().add(logo);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }
    @FXML
    public void setColorBackground(Color color) {
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    // Configure le nom de l'utilisateur
    public void setUsername(String user) {
        this.username = user;
    }

    public void FaCheck(ActionEvent event) throws IOException{
        Fafield = authenticationField.getText();
        if (Fafield.equals("toto")){
            System.out.println("Utilisateur connecté...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/mainscene.fxml"));
            Parent root  = loader.load();
            //Appel du controller MainController
            MainController mainController = loader.getController();
            mainController.displayWelcomeLabel(username);
            mainController.setUsername(username);
            mainController.displayLogo(image);

            //Appel de la seconde scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Menu");
            mainController.setLogo(stage);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else{
            authenticationField.setText("");
            resultFALabel.setText("Authentication failed..");
        }
    }
}
