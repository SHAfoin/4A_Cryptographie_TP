package com.cryptotpmail.controllers;

import java.io.IOException;

import com.cryptotpmail.client.Client;
import com.cryptotpmail.client.ClientSessionKey;


import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartController {

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
    @FXML
    private Label resultPasswdLabel;



    @FXML
    public void login(ActionEvent event) throws IOException {

        // Récupération données utilisateurs
        String username = "";
        String password = "";
        if (idUser.getText() != null & !(idUser.getText().trim().isEmpty())) {
            username = idUser.getText();
        }
        if (passwdUser.getText() != null & !(passwdUser.getText().trim().isEmpty())) {
            password = passwdUser.getText();
            resultPasswdLabel.setText("Password empty...");
        }
        ClientSessionKey sessionKey = Client.sessionParameters();
        boolean auth = Client.authentification(username, password, sessionKey);
        if (!auth) {
            System.out.println("Erreur d'authentification");
            resultPasswdLabel.setText("Password incorrect...");

        } else {
            System.out.println("Utilisateur : " + username + "\nMot de passe : " + password);
            System.out.println("Utilisateur connecté...");

            // Charge seconde scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/Fascene.fxml"));
            root = loader.load();

            // Appel de l'écran de 2FA
            FACheckingController faCheckingController = loader.getController();
            faCheckingController.setUsername(username);
            faCheckingController.setPassword(password);
            faCheckingController.setSession(sessionKey);

            // Appel de la seconde scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            faCheckingController.setLogo(stage);
            stage.setTitle("Authentication");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }

}
