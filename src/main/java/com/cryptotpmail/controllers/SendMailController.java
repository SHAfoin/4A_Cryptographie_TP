package com.cryptotpmail.controllers;

import java.io.IOException;
import java.util.ArrayList;

import com.cryptotpmail.client.ClientIBEParams;
import com.cryptotpmail.mail.SendAttachmentInEmail;

import it.unisa.dia.gas.jpbc.Pairing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;

public class SendMailController {

    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private TextField recipientTextField;
    @FXML
    private TextField subjectTextField;
    @FXML
    private TextArea bodyTextArea;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label fileLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private Pane pane;

    private Color color;
    private Image image;
    private String recipient, subject, body, username = " ", password = " ";
    private ArrayList<File> listFile = new ArrayList<>();
    private ClientIBEParams clientIBE;
    private Pairing pairingIBE;

    // Fonctions
    public void setLogo(Stage stage) {
        try {
            Image logo = image;
            stage.getIcons().add(logo);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setClientIBE(ClientIBEParams clientIBE) {
        this.clientIBE = clientIBE;
    }

    public void setPairingIBE(Pairing pairingIBE) {
        this.pairingIBE = pairingIBE;
    }

    // Récupère le nom de l'utilisateur connecté
    public void setUser(String user) {
        username = user;
    }

    public String getUser() {
        return username;
    }

    // Récupère la couleur du background
    public void setColorBackground(Color color) {

        this.color = color;
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    // Récupère le logo de l'interface
    public void setImage(Image image) {
        this.image = image;
    }

    // Fonction qui évite les doublons
    private boolean checkListFile(File file) {
        if (listFile.contains(file)) {
            return true;
        } else {
            return false;
        }
    }

    // FOnctions qui affiche tous les fichiers de la liste
    private String printListFile(ArrayList<File> listFile) {
        String print = "";
        for (File i : listFile) {
            print += i.getName() + " ";
        }
        return print;
    }

    @FXML
    public void chargeFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();

        // Ouvrir la boîte de dialogue
        Stage stage = new Stage(); // Remplace par une référence existante si possible
        File fichier = fileChooser.showOpenDialog(stage);

        if (fichier != null) {
            System.out.println("Fichier sélectionné : " + fichier.getAbsolutePath());
            // Evite les doublons
            if (!checkListFile(fichier) == true) {
                listFile.add(fichier);
            } else {
                System.out.println("Fichier déjà ajouté !");
            }
            fileLabel.setText(printListFile(listFile));
        }

    }

    @FXML
    public void sendMail(ActionEvent event) throws IOException {
        recipient = recipientTextField.getText();
        subject = subjectTextField.getText();
        body = bodyTextArea.getText();
        username = getUser();
        password = getPassword();
        if ((recipient.isBlank()) || (subject.isBlank()) || (body.isBlank())) {
            if (recipient.isBlank()) {
                System.out.println("Destinataire vide");
            }
            if (subject.isBlank()) {
                System.out.println("Sujet vide...");
            } else {
                System.out.println("Message vide...");
            }
        } else {
            // Envoie le mail encrypté
            SendAttachmentInEmail.sendMail(username, recipient, subject, body, listFile,
                    password, pairingIBE, clientIBE);
            this.goMenu(event);
        }
    }

    @FXML
    public void goMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/mainscene.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setColorBackground(color);
        mainController.setUsername(username);
        mainController.setPassword(password);
        mainController.setClientIBE(clientIBE);
        mainController.setPairingIBE(pairingIBE);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menu");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
