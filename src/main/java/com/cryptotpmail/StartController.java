package com.cryptotpmail;

import java.io.IOException;

import com.cryptotpmail.client.Client;
import com.cryptotpmail.client.ClientIBEParams;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
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

    private Image image = new Image(getClass().getResourceAsStream("logo.png"));

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
    public void login(ActionEvent event) throws IOException {
        Pairing pairingIBE = PairingFactory.getPairing("curves\\a.properties");

        // Récupération données utilisateurs
        String username = "";
        String password = "";
        if (idUser.getText() != null & !(idUser.getText().trim().isEmpty())) {
            username = idUser.getText();
        }
        if (passwdUser.getText() != null & !(passwdUser.getText().trim().isEmpty())) {
            password = passwdUser.getText();
        }

        ClientIBEParams client = Client.mailEncryptionParameters(pairingIBE, username, password);
        if (client != null) {
            // Charge seconde scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/mainscene.fxml"));
            root = loader.load();

            // Appel du controller MainController
            MainController mainController = loader.getController();
            mainController.setLogo(stage);
            mainController.displayWelcomeLabel(username);
            mainController.setUsername(username);
            mainController.setPassword(password);
            mainController.setPairingIBE(pairingIBE);
            mainController.setClientIBE(client);
            mainController.displayLogo(image);

            // Appel de la seconde scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Menu");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        }

    }

}
