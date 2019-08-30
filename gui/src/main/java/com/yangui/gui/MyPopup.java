package com.yangui.gui;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;



public class MyPopup extends Popup {

    private final VBox vbox;
    
    ListView<String> list;
    
    EventStream<?> disappearanceTriggers1;


    public MyPopup() {
        super();
        list = new ListView<>();
        ObservableList<String> items =FXCollections.observableArrayList ("module", "container", "leaf", "list");
        list.setItems(items);
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        	
        vbox = new VBox(list);
        //vbox = new VBox(new Label("ssd"));
        vbox.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
        vbox.setPadding(new Insets(5));
        getContent().add(vbox);
        
        disappearanceTriggers1 = EventStreams.eventsOf(this, KeyEvent.KEY_PRESSED).filter(key -> key.getCode() == KeyCode.ESCAPE);
 	   disappearanceTriggers1.subscribe(event -> System.out.println("ESCAPE myPopup pressed!"));
    }
}
