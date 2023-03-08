package com.yang.ui.codearea.base;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.yang.ui.codearea.CodeareaView;


public class CodeareaDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	CodeareaView codeView = new CodeareaView(); 
        Scene scene = new Scene(new StackPane( codeView.getView()), 1200, 800);
        //codeView.getCodeArea().initYang(true);
        codeView.getCodeArea().initXml(true);
        
        //Add content of these css files to the codearea css.
        //scene.getStylesheets().add(CodeareaDemo.class.getResource("java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Codearea  Demo");
        primaryStage.show();
    }
    
}
