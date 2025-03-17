package com.cryptotpmail;

import java.io.IOException;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class StartController{

    @FXML
    private TextField idUser;
    @FXML
    private PasswordField passwdUser;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;

    private Image image = new Image(getClass().getResourceAsStream("logoChatGpt.png"));

    @FXML
    public void login(ActionEvent event) throws IOException {
        //Récupération données utilisateurs
        String username = "titi"; String password = "toto";
        if  (idUser.getText() != null & !(idUser.getText().trim().isEmpty())){
            username = idUser.getText();
        }
        else{
            username = "titi2";
        }
        System.out.println("Utilisateur : "+username+"\nMot de passe : "+password);
        System.out.println("Utilisateur connecté...");

        //Charge seconde scène 
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/mainscene.fxml"));
        root = loader.load();

        //Appel du controller MainController
        MainController mainController = loader.getController();
        mainController.displayWelcomeLabel(username);
        mainController.setUsername(username);
        mainController.displayLogo(image);

        //Appel de la seconde scene
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menu");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


        
    }

    

}

