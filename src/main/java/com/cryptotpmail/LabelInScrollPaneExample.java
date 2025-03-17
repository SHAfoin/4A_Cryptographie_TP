package com.cryptotpmail;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LabelInScrollPaneExample extends Application {

    @Override
    public void start(Stage stage) {
        // Le label avec un texte trop long
        Label longTextLabel = new Label(
                "Voici un très long texte...\n" +
                "Qui continue sur plusieurs lignes...\n" +
                "Et encore plus de texte pour bien illustrer le scroll...\n" +
                "Ceci est un exemple JavaFX avec un Label dans un ScrollPane.\n" +
                "Avec ça, on peut lire tout le contenu sans souci !"
        );
        longTextLabel.setWrapText(true); // Pour ne pas avoir de scroll horizontal

        // Le ScrollPane avec le Label à l'intérieur
        ScrollPane scrollPane = new ScrollPane(longTextLabel);
        scrollPane.setFitToWidth(true); // Pour que le label prenne toute la largeur

        // Ajout au layout
        VBox layout = new VBox(scrollPane);
        Scene scene = new Scene(layout, 400, 200);

        stage.setTitle("Label dans un ScrollPane");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
