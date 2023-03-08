package com.yang.ui.codearea.base;


import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;
import com.yang.ui.codearea.fxmisc.richtext.CodeArea;
import com.yang.ui.codearea.fxmisc.richtext.LineNumberFactory;
import com.yang.ui.codearea.fxmisc.richtext.NavigationActions.SelectionPolicy;
import com.yang.ui.codearea.base.YangHighlighter;
//import com.yang.ui.codearea.fxmisc.richtext.base.brackethighlighter.BracketHighlighterDemo;
//import com.yang.ui.codearea.fxmisc.richtext.demo.brackethighlighter.CustomCodeArea;
//import com.yang.ui.codearea.fxmisc.richtext.demo.richtext.RichTextDemo;
import com.yang.ui.codearea.base.MyTextStyle;
import com.yang.ui.codearea.fxmisc.richtext.model.Paragraph;
//import com.yang.ui.codearea.reactfx.collection.LiveList;

import com.google.common.base.CharMatcher;
import org.reactfx.Subscription;
import org.reactfx.SuspendableNo;
import org.reactfx.collection.LiveList;



public class JavaKeywordsDemo extends Application {


    private static final String sampleCode = String.join("\n", new String[] {
        "package com.example;",
        "",
        "import java.util.*;",
        "",
        "public class Foo extends Bar implements Baz {",
        "",
        "    /*",
        "     * multi-line comment",
        "     */",
        "    public static void main(String[] args) {",
        "        // single-line comment",
        "        for(String arg: args) {",
        "            if(arg.length() != 0){",
        "                System.out.println(arg);",
        "            }else{",
        "                System.err.println(\"Warning: empty string as argument\");",
        "           }",
        "        }",
        "    }",
        "",
        "}"
    });
    
    private CodeArea codeArea;
    //private CustomCodeArea codeArea;
    private YangHighlighter codeHighlighter;
    
    private final SuspendableNo updatingToolbar = new SuspendableNo();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        
    	 codeArea = new CodeArea();
         // highlight brackets
    	 codeHighlighter = new YangHighlighter(codeArea);

         codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
         codeArea.setContextMenu( new DefaultContextMenu() );
         

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->{
            if ( KE.getCode() == KeyCode.ENTER ) {
            	int caretPosition = codeArea.getCaretPosition();
            	int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
            }
        });
        codeArea.replaceText(0, 0, sampleCode);
        
        //add buttons
        Button undoBtn = createButton("undo", codeArea::undo, "Undo");
        Button redoBtn = createButton("redo", codeArea::redo, "Redo");
        Button cutBtn = createButton("cut", codeArea::cut, "Cut");
        Button copyBtn = createButton("copy", codeArea::copy, "Copy");
        Button pasteBtn = createButton("paste", codeArea::paste, "Paste");
        Button increaseIndentBtn = createButton("increaseIndent", this::increaseIndent, "Increase indent");
        Button decreaseIndentBtn = createButton("decreaseIndent", this::decreaseIndent, "Decrease indent");
        Button formatBtn = createButton("format", this::formatCode, "Format Code");
        
        List<Integer> fontSizeList = IntStream.rangeClosed(7, 56).boxed().collect(Collectors.toList());  
        ComboBox<Integer> sizeCombo = new ComboBox<>(FXCollections.observableArrayList(fontSizeList));
        sizeCombo.setTooltip(new Tooltip("Font size"));
        sizeCombo.setOnAction(evt -> updateFontSize(sizeCombo.getValue()));
        
        ComboBox<String> familyCombo = new ComboBox<>(FXCollections.observableList(Font.getFamilies()));
        //familyCombo.getSelectionModel().select("Serif");
        familyCombo.setTooltip(new Tooltip("Font family"));
        familyCombo.setOnAction(evt -> updateFontFamily(familyCombo.getValue()));
        
        ColorPicker paragraphBackgroundPicker = new ColorPicker();
        paragraphBackgroundPicker.setTooltip(new Tooltip("Paragraph background"));
        paragraphBackgroundPicker.valueProperty().addListener((o, old, color) -> updateParagraphBackground(color));
        
        undoBtn.disableProperty().bind(codeArea.undoAvailableProperty().map(x -> !x));
        redoBtn.disableProperty().bind(codeArea.redoAvailableProperty().map(x -> !x));

        BooleanBinding selectionEmpty = new BooleanBinding() {
            { bind(codeArea.selectionProperty()); }

            @Override
            protected boolean computeValue() {
                return codeArea.getSelection().getLength() == 0;
            }
        };

        cutBtn.disableProperty().bind(selectionEmpty);
        copyBtn.disableProperty().bind(selectionEmpty);
        
        
        Label lineNoLbl = new Label("Line:"); 
        Label lineNoTxt = new Label();//23/45
        lineNoTxt.textProperty().bind(
				Bindings.createStringBinding(() -> { 
					return "" + (codeArea.currentParagraphProperty().getValue() + 1);
		       }, codeArea.currentParagraphProperty()));
        
        
        
        Label colNoLbl = new Label("Col:"); 
        Label colNoTxt = new Label();//4
        colNoTxt.textProperty().bind(
				Bindings.createStringBinding(() -> { 
					return "" + codeArea.caretColumnProperty().getValue();
		       }, codeArea.caretColumnProperty()));
        
        Label charPosLbl = new Label("Pos:"); 
        Label charPosTxt = new Label();//345
        charPosTxt.textProperty().bind(
				Bindings.createStringBinding(() -> { 
					return "" + codeArea.caretPositionProperty().getValue();
		       }, codeArea.caretPositionProperty()));
        
        HBox locationBox = new HBox(lineNoLbl, lineNoTxt, colNoLbl, colNoTxt, charPosLbl, charPosTxt);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        locationBox.setPadding(new Insets(15, 12, 15, 12));
        locationBox.setSpacing(10);
        locationBox.setStyle("-fx-background-color: #ccffff;");
        
        
        ToolBar toolBar = new ToolBar(
                undoBtn, redoBtn, new Separator(Orientation.VERTICAL),
                cutBtn, copyBtn, pasteBtn, new Separator(Orientation.VERTICAL),
                increaseIndentBtn, decreaseIndentBtn, formatBtn, new Separator(Orientation.VERTICAL),
                sizeCombo, familyCombo, paragraphBackgroundPicker);
        
        VirtualizedScrollPane vsPane = new VirtualizedScrollPane<>(codeArea);
        VBox vbox = new VBox();
        VBox.setVgrow(vsPane, Priority.ALWAYS);
        vbox.getChildren().addAll(toolBar, vsPane, locationBox);
        
        Scene scene = new Scene(new StackPane(vbox), 1200, 800);
        
        //Add content of these css files to the codearea css.
        scene.getStylesheets().add(JavaKeywordsDemo.class.getResource("java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Keywords Demo");
        primaryStage.show();
        
        sizeCombo.getSelectionModel().select(7);//size 14
        familyCombo.getSelectionModel().select(130);//Serif
    }
    
    private void increaseIndent() {
        //updateParagraphStyleInSelection( ps -> ps.increaseIndent() );
    	IndexRange selection = codeArea.getSelection();
    	codeArea.insertText(selection.getStart() - 1, "    ");
    	codeArea.moveTo(codeArea.getCurrentParagraph(), SelectionPolicy.CLEAR); 
    	//codeArea.selectRange(codeArea.getCaretPosition(), codeArea.getCaretPosition() + 1);
    }	

    private void decreaseIndent() {
        //updateParagraphStyleInSelection( ps -> ps.decreaseIndent() );
    }
    
    //---------------------------
    private String breakLines(String text) {
    	text = text.replaceAll("\\{\\s*", "{" + System.lineSeparator());
    	text = text.replaceAll(";\\s*", ";" + System.lineSeparator());
    	text = text.replaceAll("\\}\\s*", "}" + System.lineSeparator());
    	return text;
    }
    
    private void appendSpaces(int wsCount, StringBuilder sb) {
		for(int i=0; i< wsCount; i++) {
			sb.append(" ");
		}
    }
    
    private int getOpenBracketsCount(StringBuilder sb) {
    	return CharMatcher.is("{".charAt(0)).countIn(sb.toString()) - CharMatcher.is("}".charAt(0)).countIn(sb.toString());
    }
    
    private void formatCode() {
    	
    	final int tabSize = 4;//????????????????????????????????      ++CONFIG

    	//break lines on boundaries '{};'
    	String text = codeArea.getText();
    	text = breakLines(text);
    	codeArea.selectAll();
    	codeArea.replaceSelection(text);
    	//align each line based on open parantesis count
    	StringBuilder sb = new StringBuilder();
    	
    	//iterate through lines of codeArea
    	LiveList<Paragraph<Collection<String>, String, Collection<String>>> paragraphs = codeArea.getParagraphs();
    	int lineCount = codeArea.getParagraphs().size();
    	for(int i=0; i< lineCount; i++) {
    		Paragraph<Collection<String>, String, Collection<String>> paragraph = paragraphs.get(i);
    	
    		String line = paragraph.getText();//without new line
    		String stripedLine = line.strip();

    		int tabCount = 0;
    		if(stripedLine.endsWith("{") ||
    				stripedLine.endsWith(";")) { 
    			tabCount = getOpenBracketsCount(sb);
    			appendSpaces(tabCount*tabSize, sb);
    			sb.append(stripedLine);
    			
    		}else if(stripedLine.endsWith("}")) { 
    			tabCount = getOpenBracketsCount(sb) - 1;
    			appendSpaces(tabCount*tabSize, sb);
    			sb.append(stripedLine);
    			
    		}else {
    			tabCount = getOpenBracketsCount(sb);
    			appendSpaces(tabCount*tabSize, sb);
    			sb.append(stripedLine);
    		}

    		if(i < lineCount-1) {//do not add new line for last line
    			sb.append(System.lineSeparator());
    		}
    	};
    	codeArea.selectAll();
    	codeArea.replaceSelection(sb.toString());
    }
    
    private void updateStyleInSelection(MyTextStyle mixin) {
    	String css = codeArea.getStyle();
    	css = mixin.mergeIntoCss(css);
    	codeArea.setStyle(css);
    }
    
    private void updateFontSize(Integer size) {
        //if(!updatingToolbar.get()) {
            updateStyleInSelection(MyTextStyle.fontSize(size));
        //}
    }

    private void updateFontFamily(String family) {
        //if(!updatingToolbar.get()) {
            updateStyleInSelection(MyTextStyle.fontFamily(family));
        //}
    }

    private void updateParagraphBackground(Color color) {
       // if(!updatingToolbar.get()) {
            //updateParagraphStyleInSelection(ParStyle.backgroundColor(color));
    		updateStyleInSelection(MyTextStyle.backgroundColor(color));
       //s }
    }
    
    private class DefaultContextMenu extends ContextMenu{
        private MenuItem fold, unfold, print;

        public DefaultContextMenu(){
            fold = new MenuItem( "Fold selected text" );
            fold.setOnAction( AE -> { hide(); fold(); } );

            unfold = new MenuItem( "Unfold from cursor" );
            unfold.setOnAction( AE -> { hide(); unfold(); } );

            print = new MenuItem( "Print" );
            print.setOnAction( AE -> { hide(); print(); } );

            getItems().addAll( fold, unfold, print );
        }

        /**
         * Folds multiple lines of selected text, only showing the first line and hiding the rest.
         */
        private void fold() {
            ((CodeArea) getOwnerNode()).foldSelectedParagraphs();
        }

        /**
         * Unfold the CURRENT line/paragraph if it has a fold.
         */
        private void unfold() {
            CodeArea area = (CodeArea) getOwnerNode();
            area.unfoldParagraphs( area.getCurrentParagraph() );
        }

        private void print() {
            System.out.println( ((CodeArea) getOwnerNode()).getText() );
        }
    }
    
    private Button createButton( String styleClass, Runnable action, String toolTip) {
        Button button = new Button();
        button.getStyleClass().add(styleClass);
        button.setOnAction(evt -> {
            action.run();
            codeArea.requestFocus();
        });
        button.setPrefWidth(25);
        button.setPrefHeight(25);
        if (toolTip != null) {
            button.setTooltip(new Tooltip(toolTip));
        }
        return button;
    }

}
