package com.cryptotpmail;


import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.*;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Démarre l'application avec le menu de connexion
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cryptotpmail/startscene.fxml"));
        Parent root  = loader.load();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/cryptotpmail/logo.png")));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    public static void main(String[] args){
        launch(args);
    }
}