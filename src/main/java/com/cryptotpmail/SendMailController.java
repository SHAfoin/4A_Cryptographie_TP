package com.cryptotpmail;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
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
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private File fichierCheck;
    private ArrayList<File> listFile = new ArrayList<>();

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

        // // Optionnel : Définir une extension de fichier par défaut (ex: images, txt,
        // etc.)
        // fileChooser.getExtensionFilters().add(
        // new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt")
        // );

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
            fichierCheck = fichier;
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
            SendAttachmentInEmail.sendMail(username, recipient, subject, body, listFile, password, username);
            // System.out.println("Envoyé par : "+username);
            // System.out.println("Destinataire : "+recipient);
            // System.out.println("Sujet : "+subject);
            // if (!listFile.isEmpty()){
            // System.out.println("Pièce jointe : "+printListFile(listFile));
            // }
            // System.out.println("Message : "+body);
            // System.out.println("Mail envoyé...");
            this.cancelSending(event);
        }
    }

    @FXML
    public void cancelSending(ActionEvent event) throws IOException {
        System.out.println(">>>Retour au menu principal...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/mainscene.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setColorBackground(color);
        mainController.displayLogo(image);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menu");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
