package com.cryptotpmail;

import java.io.IOException;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        // System.out.println(Class.forName("com.cryptotpmail.Controller"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/startscene.fxml"));
        Parent root  = loader.load();
        StartController startController = loader.getController();
        startController.setLogo(primaryStage);
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    public static void main(String[] args){
        launch(args);
    }
}