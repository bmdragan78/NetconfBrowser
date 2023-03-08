package com.yang.ui;

import com.jfoenix.controls.JFXDecorator;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;



public class AppPreloader extends Preloader{
	 
	private Scene scene;
		
	private ImageView imageView;
	    
	    
	public void start(Stage stage) throws Exception {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
	    	public void handle(WindowEvent t) {
	    	    Platform.exit();
	    	    System.exit(0);
	    	}
		});
	    	
	    App.MAIN_STAGE = stage;
	    App.WEB_BROWSER = this.getHostServices();
	    	
	    //build main window
	    Image image = new Image(getClass().getResourceAsStream("splash.gif"), 200, 200, false, false);
	    imageView = new ImageView(image);
	    BorderPane imageContainer = new BorderPane();
	    imageContainer.setCenter(imageView);
	    	
	    JFXDecorator decorator = new JFXDecorator(stage , imageContainer);
	    decorator.setCustomMaximize(true);  
	    //decorator.setTitle("YangUi");
	        
	    scene = new Scene(decorator, App.SCENE_WIDTH, App.SCENE_HEIGHT);
	    final String uri = getClass().getResource("app.css").toExternalForm();
	    scene.getStylesheets().add(uri);
	        
	    stage.setScene(scene);
	    stage.getIcons().add(image);
	    stage.setResizable(true);
	    stage.show();
	}
	    
	    
	@Override
	public void handleStateChangeNotification(StateChangeNotification evt) {
		if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
			//its time to start fading into application ...
	        SharedScene appScene = (SharedScene) evt.getApplication();
	        fadeInTo(appScene.getParentNode());            
	    }
	}  
	    
	    
	private void fadeInTo(Parent parent) {
		FadeTransition ft = new FadeTransition(Duration.millis(2000),  imageView);
	    ft.setFromValue(1.0);
	    ft.setToValue(0.4);
	    ft.setOnFinished(new EventHandler<ActionEvent>() {
	    	public void handle(ActionEvent t) {
	    		JFXDecorator decorator = (JFXDecorator) scene.getRoot();
	            decorator.setContent(parent);
	        }            
	    });
	    ft.play();
	}    
}
