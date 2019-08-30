package com.yangui.main.multiquery.query;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.fxmisc.richtext.CaretNode;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.Caret.CaretVisibility;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.yangui.main.multiquery.query.closeop.CloseopPresenter;
import com.yangui.main.multiquery.query.closeop.CloseopView;
import com.yangui.main.multiquery.query.copyconfigop.CopyconfigopPresenter;
import com.yangui.main.multiquery.query.copyconfigop.CopyconfigopView;
import com.yangui.main.multiquery.query.deleteconfigop.DeleteconfigopPresenter;
import com.yangui.main.multiquery.query.deleteconfigop.DeleteconfigopView;
import com.yangui.main.multiquery.query.editconfigop.EditconfigopPresenter;
import com.yangui.main.multiquery.query.editconfigop.EditconfigopView;
import com.yangui.main.multiquery.query.getconfigop.GetconfigopPresenter;
import com.yangui.main.multiquery.query.getconfigop.GetconfigopView;
import com.yangui.main.multiquery.query.getop.GetopPresenter;
import com.yangui.main.multiquery.query.getop.GetopView;
import com.yangui.main.multiquery.query.killop.KillopPresenter;
import com.yangui.main.multiquery.query.killop.KillopView;
import com.yangui.main.multiquery.query.lockop.LockopPresenter;
import com.yangui.main.multiquery.query.lockop.LockopView;
import com.yangui.main.multiquery.query.notop.NotopPresenter;
import com.yangui.main.multiquery.query.notop.NotopView;
import com.yangui.main.multiquery.query.rpcop.RpcopPresenter;
import com.yangui.main.multiquery.query.rpcop.RpcopView;
import com.yangui.main.multiquery.query.unlockop.UnlockopPresenter;
import com.yangui.main.multiquery.query.unlockop.UnlockopView;
import com.yangui.main.services.DeviceService;
import com.yangui.main.services.QueryService;
import com.yangui.main.services.XmlService;
import com.yangui.main.services.model.DeviceItem;
import com.yangui.main.services.model.ErrorItem;
import com.yangui.main.services.model.QueryTypeEnum;
import com.yangui.main.services.xml.LocationData;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;



public class QueryPresenter implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(QueryPresenter.class);
	
	@FXML
	BorderPane queryDetailsPane;
	
	@FXML
	JFXComboBox<String> operationCmb;
    private ObservableList<String> operationsList;
    
    private String sourceBtnId;//rpcBtn, filterBtn, configBtn
    
    @FXML
	JFXComboBox<DeviceItem> deviceCmb;
    
    @FXML
	HBox queryDetailsPaneBox;
    
    @FXML
	VBox queryDetailsPaneBox1;
    
    @FXML
    SplitPane queryDetailsPaneBox2;
    
    @FXML
	HBox queryDetailsPaneBox3;
    
    @FXML
    JFXButton runBtn;
    
    @FXML
    JFXButton validateBtn;
    
    @FXML
    CodeArea inputCodeArea;
    
    @FXML
    TextArea outputStringTxta;
    
    @FXML
	Label queryStatsLabel;
    
    @Inject
    QueryService querySrv;
    
    @Inject
    XmlService xmlSrv;
    
    @Inject
    DeviceService deviceSrv;
    
    private RunQueryService runQueryService;
    
    private ValidateQueryService validateQueryService;
    
    private RpcopView rpcopView;
    private RpcopPresenter rpcopPresenter;
    private Parent rpcopPane;
    
    private NotopView notopView;
    private NotopPresenter notopPresenter;
    private Parent notopPane;

    private GetopView getopView;
    private GetopPresenter getopPresenter;
    private Parent getopPane;
    
    private GetconfigopView getconfigopView;
    private GetconfigopPresenter getconfigopPresenter;
    private Parent getconfigopPane;
    
    private EditconfigopView editconfigopView;
    private EditconfigopPresenter editconfigopPresenter;
    private Parent editconfigopPane;
    
    private CopyconfigopView copyconfigopView;
    private CopyconfigopPresenter copyconfigopPresenter;
    private Parent copyconfigopPane;
    
    private DeleteconfigopView deleteconfigopView;
    private DeleteconfigopPresenter deleteconfigopPresenter;
    private Parent deleteconfigopPane;
    
    private LockopView lockopView;
    private LockopPresenter lockopPresenter;
    private Parent lockopPane;
    
    private UnlockopView unlockopView;
    private UnlockopPresenter unlockopPresenter;
    private Parent unlockopPane;
    
    private CloseopView closeopView;
    private CloseopPresenter closeopPresenter;
    private Parent closeopPane;
    
    private KillopView killopView;
    private KillopPresenter killopPresenter;
    private Parent killopPane;
    
    String queryName;//query tab name
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	private final Pattern whiteSpacePattern = Pattern.compile( "^\\s+" );

	private CaretNode myCaret;
	//-->methods used by child presenters to read and write the input code area
    public void updateQueryInput(String input) {
    	inputCodeArea.replaceText(input);
    }
    
    public String getQueryInput() {
    	return inputCodeArea.getText();
    }
    
    public QueryTypeEnum getQueryType() {
    	String opType = operationCmb.getSelectionModel().getSelectedItem().toString();
        return QueryTypeEnum.getInstance(opType);
    }
    
    public void setSourceBtnId(String sourceBtnId) {
    	this.sourceBtnId = sourceBtnId;
    }
    
    public String getSourceBtnId() {
		return sourceBtnId;
	}
    //<--methods used by children presenters to read and write the input code area
    
    
    private ObservableList<String> getOperationsList() {
        if (operationsList == null) {
        	operationsList = FXCollections.observableArrayList();
        	
        	for (QueryTypeEnum typeValue : QueryTypeEnum.values()) {
        		operationsList.add(typeValue.text());
            }
        }
        return operationsList;
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	//operation combo
    	operationCmb.setItems(getOperationsList());
    	operationCmb.getSelectionModel().selectFirst();
    	String opType = operationCmb.getSelectionModel().getSelectedItem().toString();
    	selectOperation(opType);
    	
    	Callback<ListView<String>, ListCell<String>> factory1 = lv -> new ListCell<String>() {
    		@Override
    		protected void updateItem(String item, boolean empty) {
    			super.updateItem(item, empty);
    			if(item != null) {
	    			if(item.equals(QueryTypeEnum.RPC.text()) || item.equals(QueryTypeEnum.NOTIFICATION.text())) 
	    				setStyle("-fx-font-weight: bold;");
	    			else
	    				setStyle("-fx-font-weight: regular;");
    			}
    			setText(empty ? "" : item);
    		}
    	};
    	operationCmb.setCellFactory(factory1);
    	operationCmb.setVisibleRowCount(12);
    	
    	//device combo
    	final ObjectProperty<ObservableList<DeviceItem>> items = new SimpleObjectProperty<>(deviceSrv.connectedDeviceObsList());
    	deviceCmb.itemsProperty().bind(items);
    	Callback<ListView<DeviceItem>, ListCell<DeviceItem>> factory = lv -> new ListCell<DeviceItem>() {
    		@Override
    		protected void updateItem(DeviceItem item, boolean empty) {
    			super.updateItem(item, empty);
    			setText(empty ? "" : item.getName());
    		}
    	};
    	deviceCmb.setCellFactory(factory);
    	deviceCmb.setVisibleRowCount(10);
    	//selected value 
    	deviceCmb.setConverter(new StringConverter<DeviceItem>() {
    		@Override
            public String toString(DeviceItem device) {
    			if (device == null)
                  return null;
                else 
                  return device.getName();
            }
            @Override
            public DeviceItem fromString(String name) {
                return null;
            }
        });
    	
    	//run query service
    	runQueryService = new RunQueryService();
    	runQueryService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	    	@Override
	    	public void handle(WorkerStateEvent t) {
	    		if(t.getSource().getValue() != null) {
		    		String response = t.getSource().getValue().toString();
		    		runQueryService.setDuration(Instant.now().toEpochMilli() - runQueryService.getStartTime());
		    		runQueryService.setRecvBytes(response.length());
		    		queryStatsLabel.setText("Duration " + Long.toString(runQueryService.getDuration()) + " ms, " +
		    				" Sent " + Long.toString(runQueryService.getSentBytes()) + " Bytes, " +
		    				" Recv " + Long.toString(runQueryService.getRecvBytes()) + " Bytes ");
		    		outputStringTxta.setText(response);
	    		}
	    	}
	    });
    	
    	//validate query service
    	validateQueryService = new ValidateQueryService();
    	validateQueryService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	    	@Override
	    	public void handle(WorkerStateEvent t) {
	    		Boolean isValid = (Boolean) t.getSource().getValue();
	    	}
	    });
    	
    	//enable buttons & combo
    	IntegerBinding connectedDevicesCountProp = Bindings.size(deviceSrv.connectedDeviceObsList());
    	BooleanBinding isRunning = (runQueryService.runningProperty()).or(connectedDevicesCountProp.isEqualTo(0)).or(deviceCmb.getSelectionModel().selectedItemProperty().isNull());
		runBtn.disableProperty().bind(isRunning);
		ReadOnlyBooleanProperty isValidating = validateQueryService.runningProperty();
		validateBtn.disableProperty().bind(isValidating);
		//operationCmb.disableProperty().bind(isRunning.or(isValidating));
		//deviceCmb.disableProperty().bind(isRunning.or(isValidating));

		//style panes
    	queryDetailsPane.getStyleClass().add("queryDetails");//transparent background`
    	queryDetailsPaneBox.getStyleClass().add("queryDetails");
    	queryDetailsPaneBox1.getStyleClass().add("queryDetails");
    	queryDetailsPaneBox2.getStyleClass().add("queryDetails");
    	queryDetailsPaneBox3.getStyleClass().add("queryDetails");
    	
    	//style editors
    	myCaret = new CaretNode("MyCaret", inputCodeArea);
        if (!inputCodeArea.addCaret(myCaret)) {
            throw new IllegalStateException("MyCaret was not added to code area");
        }
        inputCodeArea.setShowCaret(CaretVisibility.ON);
    	inputCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(inputCodeArea));
        Subscription cleanupWhenNoLongerNeedIt = inputCodeArea.richChanges().successionEnds(Duration.ofMillis(500)).subscribe(ignore -> inputCodeArea.setStyleSpans(0, computeHighlighting(inputCodeArea.getText())));
        //tab size
        InputMap<KeyEvent> im = InputMap.consume(EventPattern.keyPressed(KeyCode.TAB), e -> inputCodeArea.replaceSelection("    "));
        Nodes.addInputMap(inputCodeArea, im);
        //preserve indentation on ENTER
        inputCodeArea.addEventFilter( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER )
            {
                Matcher m = whiteSpacePattern.matcher( inputCodeArea.getParagraph( inputCodeArea.getCurrentParagraph() ).getSegments().get( 0 ) );
                if ( m.find() ) Platform.runLater( () -> inputCodeArea.insertText( inputCodeArea.getCaretPosition(), m.group() ) );
            }
        });
        //HOME key
        inputCodeArea.addEventFilter( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.HOME )
            {
                Matcher m = whiteSpacePattern.matcher( inputCodeArea.getParagraph( inputCodeArea.getCurrentParagraph() ).getSegments().get( 0 ) );
                if ( m.find() ) Platform.runLater( () ->  inputCodeArea.moveTo(inputCodeArea.getCurrentParagraph(), m.end()) );
            }
        });
    }
    
    @FXML
    protected void selectOperationAction(ActionEvent event) {
    	String opType = operationCmb.getSelectionModel().getSelectedItem().toString();
    	selectOperation(opType);
    }
    
    @FXML
    protected void runAction(ActionEvent event) {
    	DeviceItem device = deviceCmb.getSelectionModel().getSelectedItem();
    	runQueryService.setData(inputCodeArea.getText(), device, queryName);
    	runQueryService.restart();
    }
    
    @FXML
    protected void selectDeviceAction(ActionEvent event) {
    	DeviceItem device = deviceCmb.getSelectionModel().getSelectedItem();
    }
    
    
    @FXML
    protected void validateAction(ActionEvent event) {
    	//parse inputStringTxta->text && call XmlService.validate(text) -> schemaSrv.getModulesTreeRoot()
    	validateQueryService.setData(inputCodeArea.getText(), queryName);
    	validateQueryService.restart();
    }
    
    
    public void selectError(ErrorItem error) {//called from MultiQueryPresenter.selectError()
    	LocationData locationData = error.getLocationData();
    	int lineIndex = 0;
    	if(locationData != null)
    		lineIndex = locationData.getStartLine();
    	else
    		lineIndex =Integer.parseInt(error.getLineNumber());
    	
    	inputCodeArea.moveTo(lineIndex > 0 ? lineIndex-1 : 0, 0);
    	inputCodeArea.requestFollowCaret();
    }
    
    
    private final class ValidateQueryService extends Service<Void> {
    	
		private String queryString;
		private String queryName;
    	
		public ValidateQueryService() {
			super();
		}	
	
	    @Override
	    protected Task<Void> createTask() {
	    	return new Task<Void>() {
		        @Override
		        protected Void call() {
		        	xmlSrv.validateQuery(queryString, queryName);
		        	return null;
		        }
	        };
	    }
    	
		public void setData(String queryString, String queryName) {
			this.queryString = queryString;
			this.queryName = queryName;
		}
    }


    private final class RunQueryService extends Service<String> {
    	
    	private DeviceItem device;
		private String queryString;
		private String queryName;
    	
    	private long startTime;
		private long sentBytes;
    	
    	private long duration;
    	private long recvBytes;
    	
    	
		public RunQueryService() {
			super();
		}	
	
	    @Override
	    protected Task<String> createTask() {
	      	
	    	return new Task<String>() {
		        @Override
		        protected String call() {
		        	startTime = Instant.now().toEpochMilli();
		        	sentBytes = queryString.length();
		        	return querySrv.runQuery(device, queryName, queryString);
		        }
	        };
	    }
	    
		
	    public void setData(String queryString, DeviceItem device, String queryName) {
			this.queryString = queryString;
			this.device = device;
			this.queryName = queryName;
		}

	    public long getStartTime() {
			return startTime;
		}

		public long getSentBytes() {
			return sentBytes;
		}

		public long getDuration() {
			return duration;
		}

		public void setDuration(long duration) {
			this.duration = duration;
		}

		public long getRecvBytes() {
			return recvBytes;
		}

		public void setRecvBytes(long recvBytes) {
			this.recvBytes = recvBytes;
		}
    }
    
    
    private void selectOperation(String opType) {
    	
        QueryTypeEnum opTypeEnum = QueryTypeEnum.getInstance(opType);
        inputCodeArea.replaceText(opTypeEnum.getDefaultContent());
        
        switch(opTypeEnum) {
	        case GET:
	        	getopView = new GetopView();
	            getopPresenter = (GetopPresenter) getopView.getPresenter();
	            getopPresenter.setQueryPresenter(this);
	            getopPane = getopView.getView();
	            queryDetailsPane.setCenter(getopPane);
	        	break;
	        case GET_CONFIG:
	        	getconfigopView = new GetconfigopView();
	            getconfigopPresenter = (GetconfigopPresenter) getconfigopView.getPresenter();
	            getconfigopPresenter.setQueryPresenter(this);
	            getconfigopPane = getconfigopView.getView();
	            queryDetailsPane.setCenter(getconfigopPane);
	        	break;
	        case EDIT_CONFIG:
	        	editconfigopView = new EditconfigopView();
	            editconfigopPresenter = (EditconfigopPresenter) editconfigopView.getPresenter();
	            editconfigopPresenter.setQueryPresenter(this);
	            editconfigopPane = editconfigopView.getView();
	            queryDetailsPane.setCenter(editconfigopPane);
	        	break;
	        case COPY_CONFIG:
	        	copyconfigopView = new CopyconfigopView();
	        	copyconfigopPresenter = (CopyconfigopPresenter) copyconfigopView.getPresenter();
	        	copyconfigopPresenter.setQueryPresenter(this);
	        	copyconfigopPane = copyconfigopView.getView();
	            queryDetailsPane.setCenter(copyconfigopPane);
	        	break;
	        case DELETE_CONFIG:
	        	deleteconfigopView = new DeleteconfigopView();
	        	deleteconfigopPresenter = (DeleteconfigopPresenter) deleteconfigopView.getPresenter();
	        	deleteconfigopPresenter.setQueryPresenter(this);
	        	deleteconfigopPane = deleteconfigopView.getView();
	            queryDetailsPane.setCenter(deleteconfigopPane);
	        	break;
	        case LOCK:
	        	lockopView = new LockopView();
	        	lockopPresenter = (LockopPresenter) lockopView.getPresenter();
	        	lockopPresenter.setQueryPresenter(this);
	        	lockopPane = lockopView.getView();
	            queryDetailsPane.setCenter(lockopPane);
	        	break;
	        case UNLOCK:
	        	unlockopView = new UnlockopView();
	        	unlockopPresenter = (UnlockopPresenter) unlockopView.getPresenter();
	        	unlockopPresenter.setQueryPresenter(this);
	        	unlockopPane = unlockopView.getView();
	            queryDetailsPane.setCenter(unlockopPane);
	        	break;
	        case CLOSE_SESSION:
	        	closeopView = new CloseopView();
	        	closeopPresenter = (CloseopPresenter) closeopView.getPresenter();
	        	closeopPresenter.setQueryPresenter(this);
	        	closeopPane = closeopView.getView();
	            queryDetailsPane.setCenter(closeopPane);
	        	break;
	        case KILL_SESSION:
	        	killopView = new KillopView();
	        	killopPresenter = (KillopPresenter) killopView.getPresenter();
	        	killopPresenter.setQueryPresenter(this);
	        	killopPane = killopView.getView();
	            queryDetailsPane.setCenter(killopPane);
	        	break;
	        	
	        case RPC:
	        	rpcopView = new RpcopView();
	        	rpcopPresenter = (RpcopPresenter) rpcopView.getPresenter();
	        	rpcopPresenter.setQueryPresenter(this);
	        	rpcopPane = rpcopView.getView();
	            queryDetailsPane.setCenter(rpcopPane);
	        	break;
	        	
	        case NOTIFICATION:
	        	notopView = new NotopView();
	        	notopPresenter = (NotopPresenter) notopView.getPresenter();
	        	notopPresenter.setQueryPresenter(this);
	        	notopPane = notopView.getView();
	            queryDetailsPane.setCenter(notopPane);
	        	break;
        }
    }
    
    
    //Input area code styling based on keywords
    private static final String[] KEYWORDS = new String[] {
    		"notification", "rpc", "get", "get-config", "edit-config", "copy-config", "delete-config", "lock", "unlock", "close-session", "kill-session", "source", "target", "filter", "default-operation", "test-option", "error-option", "config"
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
