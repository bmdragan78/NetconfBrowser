package com.yangui.main.schema.file;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.fxmisc.richtext.Caret.CaretVisibility;
import org.fxmisc.richtext.CaretNode;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.yangui.main.services.FileService;
import com.yangui.main.services.SchemaService;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.SchemaValidationStatus;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;



public class FilePresenter  implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(FilePresenter.class);

	@FXML
	GridPane filePane;
	
	@FXML
	TextField fileName;
	
	@FXML
	TextField fileSize;
	
	@FXML
	TextField fileLastModified;
	
	@FXML
	JFXButton fileSaveBtn;
	
	@FXML
	CodeArea fileCodeArea;
	
	@Inject
	FileService fileService;
	
	@Inject
	SchemaService schemaService;
	
    private UpdateFileService updateFileSrv;
	
	//Pass this parameter from parent controller to populate this view -> selected SchemaItem in parent controller 
	private SchemaItem fileItem;
	
	private CaretNode myCaret;
	
	private final Pattern whiteSpacePattern = Pattern.compile( "^\\s+" );
	
	
	//Pass this parameter back to parent controller to update parent view 
	private ObjectProperty<SchemaItem> updatedFileProperty;
	public ReadOnlyObjectProperty<SchemaItem> updatedFileProperty() {
        return updatedFileProperty;
    }

	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	filePane.getStyleClass().add("gridPane");
    	
    	myCaret = new CaretNode("MyCaret", fileCodeArea);
        if (!fileCodeArea.addCaret(myCaret)) {
            throw new IllegalStateException("MyCaret was not added to code area");
        }
        fileCodeArea.setShowCaret(CaretVisibility.ON);
        fileCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(fileCodeArea));
        Subscription cleanupWhenNoLongerNeedIt = fileCodeArea.richChanges().successionEnds(java.time.Duration.ofMillis(400)).subscribe(ignore -> fileCodeArea.setStyleSpans(0, computeHighlighting(fileCodeArea.getText())));
        
        //tab size
        InputMap<KeyEvent> im = InputMap.consume(EventPattern.keyPressed(KeyCode.TAB), e -> fileCodeArea.replaceSelection("    "));
        Nodes.addInputMap(fileCodeArea, im);
        //preserve indentation on ENTER
        fileCodeArea.addEventFilter( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER )
            {
                Matcher m = whiteSpacePattern.matcher( fileCodeArea.getParagraph( fileCodeArea.getCurrentParagraph() ).getSegments().get( 0 ) );
                if ( m.find() ) Platform.runLater( () -> fileCodeArea.insertText( fileCodeArea.getCaretPosition(), m.group() ) );
            }
        });
        //HOME key
        fileCodeArea.addEventFilter( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.HOME )
            {
                Matcher m = whiteSpacePattern.matcher( fileCodeArea.getParagraph( fileCodeArea.getCurrentParagraph() ).getSegments().get( 0 ) );
                if ( m.find() ) Platform.runLater( () ->  fileCodeArea.moveTo(fileCodeArea.getCurrentParagraph(), m.end()) );
            }
        });
        
        //init update file service
        updatedFileProperty = new SimpleObjectProperty<SchemaItem>();
    	updateFileSrv = new UpdateFileService();
    	updateFileSrv.setOnSucceeded((WorkerStateEvent t) -> {
    		SchemaItem updatedFile = (SchemaItem) t.getSource().getValue();
    		
    		switch (updatedFile.getStatusEnum()) {
	            case OK:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 0px");
	    			fileCodeArea.setStyle("-fx-border-color: red; -fx-border-width: 0px");
	                break;
	            case ERR_NO_FILE_NAME:
	            case ERR_FILE_DUPLICATE:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 2px");
	            	break;
	            case ERR_NO_FILE_CONTENT:
	            case ERR_NO_MODULE:
	            case ERR_NO_NAMESPACE:
	            case ERR_NO_MOD_REVISION://never happens
	            case ERR_MODULE_DUPLICATE:
	            case ERR_IO:
	            case ERR_YANG:
	            	fileCodeArea.setStyle("-fx-border-color: red; -fx-border-width: 2px");
	           	 	break;
	            default:
	           	 	LOG.error("FileUpdate returned N/A Errors ");
	       }
    		//refresh local view
    	    updateView(updatedFile);
    		//refresh parent view
    	    updatedFileProperty.set(null);
    	    updatedFileProperty.set(updatedFile);
    	});
    	updateFileSrv.setOnFailed((WorkerStateEvent t) -> {
			LOG.error("UpdateFileService ------------------- Failed: " + updateFileSrv.getException().getMessage());
    	});
    	
    	//update line number
    	schemaService.getSourceIndexProperty().addListener( ( ov, oldv, newv ) -> {
    		Integer index = (Integer)newv;
    		if(index > 1) 
	    		fileCodeArea.moveTo(index-1);
	    	else
	    		fileCodeArea.moveTo(0);
    		fileCodeArea.requestFollowCaret();
    	});
    }

    
    @FXML
    protected void saveAction(ActionEvent event) {
        updateFileSrv.setData(fileItem, fileName.getText(), fileCodeArea.getText());
    	updateFileSrv.restart();
    }
    
    // Populates this view with model
	public void updateFileItem(SchemaItem selectedFileItem) {
		fileItem = selectedFileItem;
		updateView(fileItem);
	}
	
	
    private void updateView(SchemaItem selSchemaItem) {//called from updateFileSrv.OnAction & SchemaPresenter.selectedItemChange event
    	try {
    		//LOG.debug("updateView staus " + selSchemaItem.getStatusEnum().text());
    		
	    	fileName.setText(selSchemaItem.getFile().getName());
	    	fileSize.setText(selSchemaItem.getFile().getSize()+"");
	    	fileLastModified.setText(selSchemaItem.getFile().getLastModifiedTime().toString());
	    	fileCodeArea.replaceText(selSchemaItem.getFile().getContent());
	    	
	    	switch (selSchemaItem.getStatusEnum()) {
	            case OK:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 0px");
	    			fileCodeArea.setStyle("-fx-border-color: red; -fx-border-width: 0px");
	                break;
	            case ERR_NO_FILE_NAME:
	            case ERR_FILE_DUPLICATE:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 2px");
	            	break;
	            case ERR_NO_FILE_CONTENT:
	            case ERR_NO_MODULE:
	            case ERR_NO_NAMESPACE:
	            case ERR_NO_MOD_REVISION://never happens
	            case ERR_MODULE_DUPLICATE:
	            case ERR_IO:
	            case ERR_YANG:
	            	fileCodeArea.setStyle("-fx-border-color: red; -fx-border-width: 2px");
	           	 	break;
	            default:
	           	 	LOG.error("FileUpdate returned N/A Errors ");
	       }
	    	
	    	//in case of errors present on this SchemaItem go to line in FileEditor and update logText from MainPresenter
	    	if(SchemaValidationStatus.OK != selSchemaItem.getStatusEnum()) {
	    		
	    		try {
		    		int errorLineNumber = selSchemaItem.getErrorLineNumber();
			    	if(errorLineNumber > 1) 
			    		fileCodeArea.moveTo(errorLineNumber-1, 0);
			    	else
			    		fileCodeArea.moveTo(0, 0);
			    	fileCodeArea.requestFollowCaret();
	    		}catch(IndexOutOfBoundsException iex) {
	    			
	    		}
	    	}else {//in case of no errors present on this SchemaItem go to sourceMsg in FileEditor; sourceMsg is set on SchemaPresenter.selectedItem event
//	    		String sourceMsg = selSchemaItem.getSourceMsg();
//	    		if(sourceMsg != null) {
//	    		}else {//if sourceMsg is not set go to line 0 in FileEditor
//		    		fileContent.moveTo(0, 0);
//		    		fileService.getErrorMsgProperty().set(null);
//	    		}
	    	}
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		
    	}
    }
    

	//Updates local file in the schema repository
    private final class UpdateFileService extends Service<SchemaItem> {
    	
    	private SchemaItem schemaItem;
		  
		private String newName;
		  
	    private String newContent;
	      
	    public UpdateFileService() {
	  		super();
	  	}	
	
	    @Override
	    protected Task<SchemaItem> createTask() {
	      	
	    	return new Task<SchemaItem>() {
	    		@Override
	            protected SchemaItem call() throws Exception {
	    			return fileService.updateCreateFile(schemaItem, newName, newContent);
	    		}
	       };
	    }
	    
		public void setData(SchemaItem schemaItem, String newName, String newContent) {
			this.schemaItem = schemaItem;
			this.newName = newName;
			this.newContent = newContent;
		}

    }
    
    
    private static final String[] KEYWORDS = new String[] {
            "module", "prefix", "namespace", "revision", "anyxml", "container",
            "list", "leaf-list", "leaf", "identity", "organization", "contact", "description", "import", "typedef", "feature", "key", 
            "type", "if-feature", "reference", "config", "yang-version", "mandatory", "base", "status", "rpc", "input", "output", 
            "choice", "notification", "units", "enum", "uses", "grouping", "action", "pattern", "length", "default", "range", "path"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    		);
    
    
    private static org.fxmisc.richtext.model.StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        org.fxmisc.richtext.model.StyleSpansBuilder<Collection<String>> spansBuilder = new org.fxmisc.richtext.model.StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; 
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
}
    
}
