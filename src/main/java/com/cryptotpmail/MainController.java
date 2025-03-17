package com.cryptotpmail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

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
    private ImageView imageView;
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
    private Image image;
    private File attachment = new File("D:\\Cours\\4a\\Cryptographie avancée\\Projet\\4A_Cryptographie_TP\\cesar.py");
    private Email emailTest = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test");
    private Email emailTest2 = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test", new File("D:\\Cours\\4a\\Cryptographie avancée\\Projet\\4A_Cryptographie_TP\\cesar2.py"));
    private Email emailTest3 = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test");
    private Email emailTest4 = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test");
    private Email emailTest5 = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test");
    private Email emailTest6 = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test", new File("D:\\Cours\\4a\\Cryptographie avancée\\Projet\\4A_Cryptographie_TP\\cesar2.py"));
    private Email emailTest7 = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test");
    private Email emailTest8 = new Email("titi@gmail", "toto@gmail", "testMail", "Hellow wolrd ! it's a test", attachment);
    private Email emailTest9 = new Email("juju@gmail", "titi@gmail", "testMail2", "Hellow wolrd ! it's a test");
    private Email emailTest10 = new Email("titi@gmail", "baba@gmail", "testMail3", "package com.cryptotpmail;\r\n" + //
                "\r\n" + //
                "import java.io.IOException;\r\n" + //
                "import java.net.URL;\r\n" + //
                "import java.util.ResourceBundle;\r\n" + //
                "import javafx.beans.value.ChangeListener;\r\n" + //
                "import javafx.beans.value.ObservableValue;\r\n" + //
                "import javafx.event.ActionEvent;\r\n" + //
                "import javafx.fxml.FXML;\r\n" + //
                "import javafx.fxml.FXMLLoader;\r\n" + //
                "import javafx.fxml.Initializable;\r\n" + //
                "import javafx.scene.Node;\r\n" + //
                "import javafx.scene.Parent;\r\n" + //
                "import javafx.scene.Scene;\r\n" + //
                "import javafx.scene.control.Label;\r\n" + //
                "import javafx.scene.control.ListView;\r\n" + //
                "import javafx.stage.Stage;\r\n" + //
                "\r\n" + //
                "public class MainController implements Initializable {\r\n" + //
                "\r\n" + //
                "    @FXML\r\n" + //
                "    private Label welcomeLabel;\r\n" + //
                "    private ListView<Email> listViewMail;\r\n" + //
                "\r\n" + //
                "    @FXML\r\n" + //
                "    private Label printMailLabel;\r\n" + //
                "    private Stage stage;\r\n" + //
                "    private Scene scene;\r\n" + //
                "    private Parent root;\r\n" + //
                "\r\n" + //
                "    Email emailTest = new Email(\"titi@gmail\", \"toto@gmail\", \"testMail\", \"Hellow wolrd ! it's a test\");\r\n" + //
                "    Email emailTest2 = new Email(\"juju@gmail\", \"titi@gmail\", \"testMail2\", \"Hellow wolrd ! it's a test\");\r\n" + //
                "    Email emailTest3 = new Email(\"titi@gmail\", \"baba@gmail\", \"testMail3\", \"Hellow wolrd ! it's a test\");\r\n" + //
                "    Email[] listEmail = {emailTest, emailTest2, emailTest3};\r\n" + //
                "    Email mailSelectionne;\r\n" + //
                "    \r\n" + //
                "\r\n" + //
                "    //Affiche l'id de l'utilisateur\r\n" + //
                "    @FXML\r\n" + //
                "    public void displayWelcomeLabel(String username){\r\n" + //
                "        // System.out.println(\"Nom utilisateur recupéré : \"+username);\r\n" + //
                "        welcomeLabel.setText(\"Welcome : \"+username);\r\n" + //
                "    }\r\n" + //
                "");
    Email[] listEmail = {emailTest, emailTest2, emailTest3, emailTest4, emailTest5, emailTest6, emailTest7, emailTest8, emailTest9, emailTest10};
    Email mailSelectionne;


    public void setLogo(Stage stage) {
        try {
            Image logo = image;
            stage.getIcons().add(logo);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }
    
    // Affiche le logo de l'interface
    public void displayLogo(Image image){
        this.image = image;
        imageView.setImage(image);
    }

    // Change la couleur du thème
    public void changeColor(ActionEvent event){
        color = colorPickerBtn.getValue();
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
    }
    public void setColorBackground(Color color){
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    //Affiche l'id de l'utilisateur
    @FXML
    public void displayWelcomeLabel(String username){
        welcomeLabel.setText("Welcome : "+username);
    }

    // Configure le nom de l'utilisateur
    public void setUsername(String user){
        this.username = user;
    }

    // Obtenir et afficher les mails
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    // Création du ScrollPane et ajout du label
    scrollPane = new ScrollPane();
    // vBox = new VBox();
    listViewMail.getItems().addAll(listEmail);

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
    //Afficher les mails dans le label
    listViewMail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
        @Override
        public void changed(ObservableValue<? extends Email> observable, Email oldValue, Email newValue) {
            String attachment = "";
            if (newValue != null) {
                mailSelectionne = newValue;
                if (mailSelectionne.getAttachment()!=null){
                    attachment = "Attachment : "+mailSelectionne.getAttachment().getName()+"\n";
                }
                printMailLabel.setText(
                        "From: " + mailSelectionne.getSender() + "\n"
                        + "To: " + username+ "\n"
                        + attachment
                        + "\nSubject: " + mailSelectionne.getSubject() + "\n"
                        + "\n" + mailSelectionne.getBody()
                );
            }
        }
    });
    // if (!vBox.getChildren().contains(printMailLabel)) {
        // vBox.getChildren().add(printMailLabel);
    // }
    // VBox vbox = new VBox(printMailLabel);
    // vbox.setPrefWidth(scrollPane.getPrefWidth()); 
    // scrollPane.setContent(vbox);
    scrollPane.setFitToWidth(true);
    printMailLabel.setWrapText(true);
    scrollPane.setContent(printMailLabel);
}

    // Téléchargement des pièces jointes
    public void downloadAttachment(ActionEvent event) throws IOException{
        if ((mailSelectionne!=null)&&(mailSelectionne.getAttachment()!=null)){
            System.out.println("Téléchargement pièce jointe...");
            File attachment = mailSelectionne.getAttachment();
        
        // Boîte de dialogue pour choisir l'emplacement de sauvegarde
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la pièce jointe");
        fileChooser.setInitialFileName(attachment.getName()); // Nom du fichier par défaut
        
        // Filtre pour afficher uniquement les fichiers du même type
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"));
        
        // OUvertur de la fenêtre pour sauvegarder le fichier 
        Window stage = ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(stage);
        
        if (selectedFile != null) {
            // Permet de copier le fichier dans l'emplacement choisi par l'utilisateur
            Files.copy(attachment.toPath(), selectedFile.toPath());
            System.out.println("Pièce jointe téléchargée avec succès : " + selectedFile.getAbsolutePath());
        } 
        else {
            System.out.println("Téléchargement annulé par l'utilisateur.");
        }
        }
        else{
            System.out.println("Aucune pièce jointe sélectionnée");
        }
    }


    //Retourne au menu principal
    @FXML
    public void logOut(ActionEvent event) throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/startscene.fxml")); 
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setTitle("Login");
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    }


    //Affiche la scene pour envoyer un mail 
    @FXML
    public void sendMail(ActionEvent event) throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/sendmailscene.fxml")); 
    root = loader.load();
    SendMailController sendMailController= loader.getController();
    if (sendMailController==null){
        System.out.println("controlleur null");
    }
    // Envoie à la classe SendMailController le nom de l'utilisateur
    sendMailController.getUser(username);
    // Change la couleur du background
    sendMailController.setColorBackground(color);
    // Envoi du logo 
    sendMailController.setImage(image);
    sendMailController.setLogo(stage);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setTitle("Send Email");
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    }
}
