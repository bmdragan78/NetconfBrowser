package com.yangui.gui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.StateMachine;
import org.reactfx.Subscription;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
 


public class TextCodeCompletion extends Application {
 
	private static Pattern filterPattern = Pattern.compile("\\s*?(\\S+?)\\s{0}?", Pattern.DOTALL);
	
   @Override
   public void start(Stage stage) {
       Group root = new Group();
       Scene scene = new Scene(root, 1200, 800, Color.BLACK);
       
       MyPopup myPopup = new MyPopup();
       ContextMenu entriesPopup = new ContextMenu();
       
       List<String> entriesList = Arrays.asList("module", "rpc", "notification", "container", "choice", "case", "leaf", "leaf-list", "list", "type", "typedef", "bits", "enum");
       
       CodeArea codeArea = new CodeArea();
       codeArea.setPrefWidth(1200);
       codeArea.setPrefHeight(800);
       setUp(codeArea, entriesPopup, entriesList);
       
       BorderPane container = new BorderPane();
       container.setCenter(codeArea);
       root.getChildren().add(container);
 
       stage.setTitle("Code Completion");
       stage.setScene(scene);
       stage.show();
   }
 
   public static void main(String[] args) {
       launch(args);
   }
   
   
   private void setUp(CodeArea codeArea, ContextMenu entriesPopup, List<String> entriesList) {
	   
	   KeyCombination ctrlSpace = new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN);
	   EventStream<?> appearanceTriggers = EventStreams.eventsOf(codeArea, KeyEvent.KEY_PRESSED).filter(ctrlSpace::match);
	   
	   EventStream<PlainTextChange> textModifications = codeArea.plainTextChanges().conditionOn(entriesPopup.showingProperty());
	   EventSource<PlainTextChange> textModifications2 = new EventSource<>();//generated manually from SHOW state
	   
	   //because popup steals key events use an event filter to detect ESCAPE then manually set EventSource
	   EventSource<String> insertionTriggers = new EventSource<>();
	   EventSource<Integer> disappearanceTriggers = new EventSource<>();
	   entriesPopup.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {//WORKING
		   public void handle(KeyEvent ev) { 
			   if(ev.getCode() == KeyCode.ESCAPE) {
				   System.out.println("ESCAPE Key Filter ");
				   disappearanceTriggers.push(0); 
			   }
           };
       });
	   
	   //make popup follow caret location
	   EventStream<Optional<Bounds>> caretBoundsEvt = EventStreams.nonNullValuesOf(codeArea.caretBoundsProperty()).conditionOn(entriesPopup.showingProperty());
	   caretBoundsEvt.subscribe(opt -> {
		   //System.out.println("caretBounds " + opt.get().toString());
		   if (opt.isPresent()) {
			   Bounds b = opt.get();
			   entriesPopup.setX(b.getMaxX()-20);
			   entriesPopup.setY(b.getMaxY());
		   }
	   	});
	   
	   //build state machine
	   EventStream<AutocompleteState> boxEvents = StateMachine
	       .init(AutocompleteState.initial())
	       .on(appearanceTriggers).transition((state, appearance) -> {state.showBox(); return state;})
	       .on(disappearanceTriggers).transition((state, ignore) -> {state.hideBox(); return state;})
	       .on(textModifications).transition((state, textChange) -> {state.updateFilter(textChange); return state;})
	       .on(textModifications2).transition((state, textChange) -> {state.updateFilter(textChange); return state;})
	       .on(insertionTriggers).transition((state, textUpdate) -> {state.insertSelected(textUpdate); return state;}).toStateStream();
	   
	   Subscription sub = boxEvents.subscribe(state -> {
		    switch (state.getState()) {
		        case SHOW:
		        	System.out.println("SHOW");
		        	Optional<Bounds> caretBounds = codeArea.getCaretBounds();
		        	if(caretBounds.isPresent()) {
		        		
		        		String line = codeArea.getParagraph( codeArea.getCurrentParagraph() ).getText();
		        		boolean isWhiteSpace = true;
		        		if(line != null && line.trim().length() > 0) {
		        			int caretColumn = codeArea.getCaretColumn();
			        		char leftChar = line.charAt(caretColumn-1);
			        		isWhiteSpace = Character.isWhitespace(leftChar);
		        		}
		        		if(!isWhiteSpace) {
		        			String segment = codeArea.getParagraph( codeArea.getCurrentParagraph() ).getSegments().get( 0 );
		        			if(segment.length() > 0){
		        			    int index=segment.lastIndexOf(" ") + 1;
		        			    String lastWord = segment.substring(index);
		        			    System.out.println(" segment " + segment + " lastWord " + lastWord);
		        			    textModifications2.push(new PlainTextChange(0, null, lastWord));
		        			}
		        		}else {
		        			//populatePopup(entriesPopup, entriesList, insertionTriggers);
		        			textModifications2.push(new PlainTextChange(0, null, ""));
		        		}
		        		entriesPopup.show(codeArea, Side.RIGHT, caretBounds.get().getMaxX(), caretBounds.get().getMaxY());
		        		codeArea.requestFocus();
		        		codeArea.requestFollowCaret();
		        	}
		            break;
		            
		        case REFILTER:
		        	String filterText = state.getFilterText();
		        	System.out.println("REFILTER " + filterText);
		        	if(filterText.trim().length() == 0) {
		        		populatePopup(entriesPopup, entriesList, "", insertionTriggers);
		        	}else {
			        	List<String> searchResult = entriesList.stream().filter(x-> x.startsWith(filterText)).collect(Collectors.toList());
			        	populatePopup(entriesPopup, searchResult, filterText, insertionTriggers);
			        }
		        	break;
		        	
		        case INSERTION:
		        	entriesPopup.hide();
		        	String insertText = state.getInsertText();
		        	System.out.println("INSERTION " + insertText);
		        	
		        	String line = codeArea.getParagraph( codeArea.getCurrentParagraph() ).getText();
	        		boolean isWhiteSpace = true;
	        		if(line != null && line.trim().length() > 0) {
	        			int caretColumn = codeArea.getCaretColumn();
		        		char leftChar = line.charAt(caretColumn-1);
		        		isWhiteSpace = Character.isWhitespace(leftChar);
	        		}
	        		if(!isWhiteSpace) {
	        			String segment = codeArea.getParagraph( codeArea.getCurrentParagraph() ).getSegments().get( 0 );
	        			if (segment.length() >0)
	        			{
	        			    int index=segment.lastIndexOf(" ") + 1;
	        			    String lastWord = segment.substring(index);
	        			    System.out.println(" segment " + segment + " lastWord " + lastWord);
	        			    codeArea.replaceText(codeArea.getCurrentParagraph(), index, codeArea.getCurrentParagraph(), index + lastWord.length(), insertText);
	        			}
	        		}else {
	        			int offset = codeArea.getCaretColumn();
	        			codeArea.insertText(offset, insertText);
	        		}
		        	break;
		        	
		        case HIDE:
		        	System.out.println("HIDE");
		        	entriesPopup.hide();
		            break;
		        default:
		        	System.out.println("default");
		        	entriesPopup.hide();
		    }
		});
   }
   
   
   private void populatePopup(ContextMenu entriesPopup, List<String> searchResult, String filterText, EventSource<String> insertionTriggers) {
	    List<CustomMenuItem> menuItems = new LinkedList<>();
	    int maxEntries = 10;
	    int count = Math.min(searchResult.size(), maxEntries);
	    for (int i = 0; i < count; i++)
	    {
	      final String result = searchResult.get(i);
	      int occurence = result.length();
	      if(filterText.trim().length() > 0) 
	    	  occurence = result.indexOf(filterText);
	      
	      Text pre = new Text(result.substring(0, occurence));
	      Text in = new Text(result.substring(occurence,occurence + filterText.length()));
	      in.setStyle("-fx-font-weight: bold;");
	      Text post = new Text(result.substring(occurence + filterText.length(), result.length()));

	      TextFlow entryFlow = new TextFlow(pre, in, post);
	      CustomMenuItem item = new CustomMenuItem(entryFlow, true);
		  item.setOnAction(new EventHandler<ActionEvent>(){
			  @Override
		      public void handle(ActionEvent actionEvent) {
				TextFlow selectedTextFlow = ((TextFlow)((CustomMenuItem)actionEvent.getTarget()).getContent());
		        String selectedValue = getStringFromTextFlow(selectedTextFlow);
		        insertionTriggers.push(selectedValue); 
		        entriesPopup.hide();
		     }
		  });
		  menuItems.add(item);
	    }
	    if(count == 0) {
	    	 Label entryLabel = new Label("No Results");
		     CustomMenuItem item = new CustomMenuItem(entryLabel, true);
		     item.setOnAction(new EventHandler<ActionEvent>()
		      {
		        @Override
		        public void handle(ActionEvent actionEvent) {
		        	entriesPopup.hide();
		        }
		      });
		      menuItems.add(item);
	    }
	    	
	    entriesPopup.getItems().clear();
	    entriesPopup.getItems().addAll(menuItems);
	  }
   
   
   public static String getStringFromTextFlow(TextFlow tf) {
	    StringBuilder sb = new StringBuilder();
	    tf.getChildren().stream()
	            .filter(t -> Text.class.equals(t.getClass()))
	            .forEach(t -> sb.append(((Text) t).getText()));
	    return sb.toString();
	}
}