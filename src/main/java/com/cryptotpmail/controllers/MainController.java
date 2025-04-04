package com.cryptotpmail.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import com.cryptotpmail.client.Client;
import com.cryptotpmail.client.ClientIBEParams;
import com.cryptotpmail.mail.Email;
import com.cryptotpmail.mail.FetchMail;

import it.unisa.dia.gas.jpbc.Pairing;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MainController implements Initializable {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label printMailLabel;
    @FXML
    private ListView<Email> listViewMail;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox vBox;
    @FXML
    private ColorPicker colorPickerBtn;
    @FXML
    private Pane pane;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;

    private Color color;
    private String username;
    private String password;
    private Image image;
    private Email mailSelectionne;
    private ClientIBEParams clientIBE;
    private Pairing pairingIBE;

    @FXML
    public void setLogo(Stage stage) {
        try {
            Image logo = image;
            this.stage = stage;
            stage.getIcons().add(logo);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @FXML
    // Change la couleur du thème
    public void changeColor(ActionEvent event) {
        color = colorPickerBtn.getValue();
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    @FXML
    public void setColorBackground(Color color) {
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    public void setClientIBE(ClientIBEParams clientIBE) {
        this.clientIBE = clientIBE;
    }

    public void setPairingIBE(Pairing pairingIBE) {
        this.pairingIBE = pairingIBE;
    }

    // Affiche l'id de l'utilisateur
    @FXML
    public void displayWelcomeLabel(String username) {
        welcomeLabel.setText("Welcome : " + username);
    }

    // Configure le nom de l'utilisateur
    public void setUsername(String user) {
        this.username = user;
    }

    // Obtenir et afficher les mails
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Création du ScrollPane et ajout du label
        scrollPane = new ScrollPane();

        // Afficher les mails dans la liste de mail
        listViewMail.setCellFactory(param -> new ListCell<Email>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);

                if (empty || email == null) {
                    setText(null);
                } else {
                    setWrapText(true); // Permet le retour à la ligne
                    setMaxHeight(50); // Limite la hauteur de chaque cellule
                    setText(email.toString());
                    setPrefWidth(listViewMail.getWidth() - 20);
                }
            }
        });
        // Afficher les mails dans le label
        listViewMail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
            @Override
            public void changed(ObservableValue<? extends Email> observable, Email oldValue, Email newValue) {
                String attachment = "";
                if (newValue != null) {
                    mailSelectionne = newValue;
                    if (!mailSelectionne.getAttachment().equals("")) {
                        attachment = "Attachment : " + mailSelectionne.getAttachment();
                    }
                    printMailLabel.setText(
                            "From: " + mailSelectionne.getSender() + "\n"
                                    + "To: " + username + "\n"
                                    + attachment
                                    + "\nSubject: " + mailSelectionne.getSubject() + "\n"
                                    + "\n" + mailSelectionne.getBody());
                }
            }
        });

        scrollPane.setFitToWidth(true);
        printMailLabel.setWrapText(true);
        scrollPane.setContent(printMailLabel);
    }

    // Télécharge la pièce jointe sans décryption
    public void downloadAttachment(ActionEvent event) throws IOException, MessagingException {
        if ((mailSelectionne != null) && (!mailSelectionne.getAttachment().equals(""))) {
            Message message = FetchMail.fetchMail(mailSelectionne.getId(), username, password);
            if (message.isMimeType("multipart/MIXED")) {
                Multipart multipart = (Multipart) message.getContent();
                // Parcours les pièces jointes
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart body = multipart.getBodyPart(i);
                    // Si c'est du corps de texte, on ignore
                    if (body.isMimeType("TEXT/PLAIN")) {
                        continue;
                    }

                    // Boîte de dialogue pour choisir l'emplacement de sauvegarde
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save attachment");
                    fileChooser.setInitialFileName(body.getFileName()); // Nom du fichier par défaut

                    // Filtre pour afficher uniquement les fichiers du même type
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"));

                    // OUvertur de la fenêtre pour sauvegarder le fichier
                    Window stage = ((Node) event.getSource()).getScene().getWindow();
                    File selectedFile = fileChooser.showSaveDialog(stage);

                    if (selectedFile != null) {
                        InputStream inputStream = body.getInputStream();
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        byte[] data = new byte[1024];
                        int byteLu;
                        while ((byteLu = inputStream.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, byteLu);
                        }
                        byte[] fichierEnByte = buffer.toByteArray();
                        FileOutputStream fichier = new FileOutputStream(new File(selectedFile.getAbsolutePath()));
                        fichier.write(fichierEnByte);
                        fichier.close();
                        inputStream.close();
                        System.out.println("Pièce jointe téléchargée avec succès : " + selectedFile.getAbsolutePath());
                    } else {
                        System.out.println("Téléchargement annulé par l'utilisateur.");
                    }
                }
            }
        } else {
            System.out.println("Aucune pièce jointe sélectionnée");
        }
    }

    // Télécharge les pièces jointes en la décryptant
    public void uncrypt(ActionEvent event) throws IOException, MessagingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        if ((mailSelectionne != null) && (!mailSelectionne.getAttachment().equals(""))) {
            Message message = FetchMail.fetchMail(mailSelectionne.getId(), username, password);
            if (message.isMimeType("multipart/MIXED")) {
                Multipart multipart = (Multipart) message.getContent();
                // Parcours les pièces jointes
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart body = multipart.getBodyPart(i);
                    System.out.println(body.getContentType());
                    // Si cest du corps de texte, on ignore
                    if (body.isMimeType("TEXT/PLAIN")) {
                        continue;
                    }

                    // Boîte de dialogue pour choisir l'emplacement de sauvegarde
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Enregistrer la pièce jointe");
                    fileChooser.setInitialFileName(body.getFileName()); // Nom du fichier par défaut

                    // Filtre pour afficher uniquement les fichiers du même type
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"));

                    // OUvertur de la fenêtre pour sauvegarder le fichier
                    Window stage = ((Node) event.getSource()).getScene().getWindow();
                    File selectedFile = fileChooser.showSaveDialog(stage);

                    if (selectedFile != null) {
                        InputStream inputStream = body.getInputStream();

                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        byte[] data = new byte[1024];
                        int byteLu;
                        while ((byteLu = inputStream.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, byteLu);
                        }
                        byte[] fichierEnByte = buffer.toByteArray();
                        byte[] message_decrypte = Client.decrypt_file_IBE(pairingIBE, clientIBE.getP(),
                                clientIBE.getP_pub(),
                                selectedFile.getAbsolutePath(), clientIBE.getSk(),
                                fichierEnByte);
                        FileOutputStream fichier = new FileOutputStream(new File(selectedFile.getAbsolutePath()));
                        fichier.write(message_decrypte);
                        fichier.close();
                        inputStream.close();
                        System.out.println("Pièce jointe téléchargée avec succès : " + selectedFile.getAbsolutePath());
                    } else {
                        System.out.println("Téléchargement annulé par l'utilisateur.");
                    }
                }
            }
        } else {
            System.out.println("Aucune pièce jointe sélectionnée");
        }
    }

    // Retourne au menu principal
    @FXML
    public void logOut(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/startscene.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Login");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Affiche la scene pour envoyer un mail
    @FXML
    public void sendMail(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/sendmailscene.fxml"));
        root = loader.load();
        SendMailController sendMailController = loader.getController();
        if (sendMailController == null) {
            System.out.println("controlleur null");
        }
        System.out.println("client : " + clientIBE);
        System.out.println("pairing : " + pairingIBE);
        // Envoie à la classe SendMailController le nom de l'utilisateur
        sendMailController.setUser(username);
        sendMailController.setPassword(password);
        // Change la couleur du background
        sendMailController.setColorBackground(color);
        // Envoi du logo
        sendMailController.setImage(image);
        sendMailController.setClientIBE(clientIBE);
        sendMailController.setPairingIBE(pairingIBE);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        sendMailController.setLogo(stage);
        stage.setTitle("Send Email");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void fetchFromServer(ActionEvent event) {
        ArrayList<Email> mailList = FetchMail.fetchAllMails(username, password);
        if (mailList != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listViewMail.getItems().clear();
                    for (Email email : mailList) {
                        listViewMail.getItems().add(email);
                    }
                }
            });
        }
    }

}
