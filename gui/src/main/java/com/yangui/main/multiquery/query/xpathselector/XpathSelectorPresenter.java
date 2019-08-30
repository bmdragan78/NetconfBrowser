package com.yangui.main.multiquery.query.xpathselector;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.yangui.main.multiquery.query.QueryPresenter;
import com.yangui.main.services.QueryService;
import com.yangui.main.services.SchemaService;
import com.yangui.main.services.XmlService;
import com.yangui.main.services.model.QueryTypeEnum;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.SchemaValidationStatus;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class XpathSelectorPresenter implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(XpathSelectorPresenter.class);
	
	@FXML
	VBox selectorPane;
	
	@FXML
	TilePane wizardTile;

	@FXML
	JFXButton cancelBtn;
	
	@FXML
	JFXButton okBtn;
	
	@FXML
	JFXTextField selectedXPath;
	
	@FXML
	TreeView<SchemaItem> schemaTreeView;

	@Inject
    SchemaService schemaSrv;
	
	@Inject
    XmlService xmlSrv;
	
    @Inject
    QueryService querySrv;
    
	private QueryPresenter queryPresenter;//use parent presenter instead of bindings
	public void setQueryPresenter(QueryPresenter queryPresenter) {
		this.queryPresenter = queryPresenter;
		setBehaviour();
	}
    
    private LoaderService loaderService;
    
    private GeneratorService generatorService;
	
	
	@FXML
    protected void cancelAction(ActionEvent event) {
		((Stage) selectorPane.getScene().getWindow()).close();
    }

	
	@FXML
    protected void okAction(ActionEvent event) {
		generatorService.restart();
    }
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selectorPane.getStyleClass().add("selectorPane");
		wizardTile.getStyleClass().add("wizardTile");
		
		schemaTreeView.setEditable(false);
		schemaTreeView.setCellFactory((TreeView<SchemaItem> p) -> new SchemaItemTreeCellImpl());//USE IT FOR ROW COLORS ???
		schemaTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		schemaTreeView.getSelectionModel().selectFirst();
		
		selectedXPath.textProperty().bind(
				Bindings.createStringBinding(() -> { 
					String xpath = "";
		        	TreeItem<SchemaItem> treeItem  = schemaTreeView.getSelectionModel().selectedItemProperty().get();
		        	if(treeItem != null) {
						switch (treeItem.getValue().getTypeEnum())
						{
						     case ROOT_NODE:
						    	  xpath = treeItem.getValue().getRepo().getSchemaFolder();
						    	  break;
						     case FILE:
						    	  xpath = treeItem.getValue().getFile().getName();
						    	  break;
						     case MODULE:
						    	  xpath = treeItem.getValue().getModule().getName();
						    	  break;
						     default:        
						    	  xpath = treeItem.getValue().getSchemaNode().getXpath();
						}
						return xpath;
		        	}else
		        		return "";
		       }, schemaTreeView.getSelectionModel().selectedItemProperty()));
		 
		
		 generatorService = new GeneratorService();
		 generatorService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    	@Override
		    	public void handle(WorkerStateEvent t) {
		    		queryPresenter.updateQueryInput(t.getSource().getValue().toString());
		    		((Stage) selectorPane.getScene().getWindow()).close();
		    	}
		    });
		 
		 loaderService = new LoaderService();
		 loaderService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    	@Override
		    	public void handle(WorkerStateEvent t) {
		    		//queryPresenter.updateQueryInput(t.getSource().getValue().toString());
		    		schemaTreeView.setRoot( (TreeItem<SchemaItem>) t.getSource().getValue());
		    	}
		 	});
		 loaderService.restart();
	}
	
	
	// Custom TreeCell implementation able to display SchemaItem error
    private final class SchemaItemTreeCellImpl extends TreeCell<SchemaItem> {  
    	 
        public SchemaItemTreeCellImpl() {
        }
 
        @Override
        public void updateItem(SchemaItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
            	if(item.getStatusEnum() != SchemaValidationStatus.OK)
            		setText(getString() + " " + item.getStatusEnum().text());
            	else
            		setText(getString());
                setGraphic(getTreeItem().getGraphic());
            }
        }
 
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
    
    
    private final class LoaderService extends Service<TreeItem<SchemaItem>> {
    	

		public LoaderService() {
			super();
		}
		
	    @Override
	    protected Task<TreeItem<SchemaItem>> createTask() {
	      	
	    	return new Task<TreeItem<SchemaItem>>() {
		        @Override
		        protected TreeItem<SchemaItem> call() {
		        		return schemaSrv.getModulesTreeRoot();
		        }
	        };
	    }
    }
    
    
    private final class GeneratorService extends Service<String> {
    	
		public GeneratorService() {
			super();
		}
		
	    @Override
	    protected Task<String> createTask() {
	      	
	    	return new Task<String>() {
		        @Override
		        protected String call() {
		        	
		        	TreeItem<SchemaItem> selectedTreeItem = schemaTreeView.getSelectionModel().getSelectedItem();
		    		String input = queryPresenter.getQueryInput();
		    		QueryTypeEnum queryTypeEnum = queryPresenter.getQueryType();
		    		String sourceBtnId = queryPresenter.getSourceBtnId();
		    		String queryName = queryPresenter.getQueryName();
		    		
		    		switch(queryTypeEnum) {
		    			case GET:
		    			case GET_CONFIG:
		    				return xmlSrv.updateQueryFilter(input, selectedTreeItem, schemaTreeView.getRoot(), queryName);
		    				
		    			case EDIT_CONFIG:
		    				return xmlSrv.updateQueryConfig(input, selectedTreeItem, schemaTreeView.getRoot(), queryName);
		    				
		    			case RPC:
		    				if(sourceBtnId.equals("rpcBtn")) 
		    					return xmlSrv.updateRpcInput(input, selectedTreeItem, schemaTreeView.getRoot(), queryName);
		    				else if(sourceBtnId.equals("filterBtn")) 
		    					return xmlSrv.updateRpcFilter(input, selectedTreeItem, schemaTreeView.getRoot(), queryName);
		    				else if(sourceBtnId.equals("configBtn")) 
		    					return xmlSrv.updateRpcConfig(input, selectedTreeItem, schemaTreeView.getRoot(), queryName);
		    				return "";
		    				
		    			case NOTIFICATION:
		    				return xmlSrv.updateNotificationInput(input, selectedTreeItem, schemaTreeView.getRoot(), queryName);
		    				
		    			default:
		    				return "";
		    		}
		        }
	        };
	    }
    }
    
    private  void setBehaviour() {
		//use queryPresenter.queryTypeEnum for knowing the caller id
		String sourceBtnId = queryPresenter.getSourceBtnId();
		QueryTypeEnum queryTypeEnum = queryPresenter.getQueryType();
		switch(queryTypeEnum) {
		
			//sourceBtnId.equals("filterBtn")
		 	case GET:
		 	case GET_CONFIG:
		 		schemaTreeView.setTooltip(new Tooltip("You can select any data nodes"));
		 		okBtn.disableProperty().bind(
		 				Bindings.createBooleanBinding(() -> { 
		 				 	TreeItem<SchemaItem> treeItem  = schemaTreeView.getSelectionModel().selectedItemProperty().get();
		 				 	
		 				 	if(treeItem != null) {
		 					 	switch (treeItem.getValue().getTypeEnum())
		 					    {
			 					  case CONTAINER:
			 					  case LIST:
			 					  case LEAF_LIST:
			 					  case LEAF:
			 					  case CHOICE:
			 					  case CHOICE_CASE:
			 					  case ANY_XML:
			 						  return false;
		 					      default:        
		 					    	  return true;
		 					    }
		 				 	}else
		 				 		return false;
		 				}, schemaTreeView.getSelectionModel().selectedItemProperty()));
		 		break;
		 		
		 	//sourceBtnId.equals("configBtn")
		 	case EDIT_CONFIG:
		 		schemaTreeView.setTooltip(new Tooltip("You can select only data root nodes"));
		 		okBtn.disableProperty().bind(
		 				Bindings.createBooleanBinding(() -> { 
		 				 	TreeItem<SchemaItem> treeItem  = schemaTreeView.getSelectionModel().selectedItemProperty().get();
		 				 	
		 				 	if(treeItem != null) {
		 					 	switch (treeItem.getValue().getTypeEnum())
		 					    {
			 					  case CONTAINER:
			 					  case LIST:
			 					  case LEAF_LIST:
			 					  case LEAF:
			 					  case CHOICE:
			 					  case CHOICE_CASE:
			 					  case ANY_XML:
			 						  return !treeItem.getValue().getSchemaNode().isRootNode();
		 					      default:        
		 					    	  return true;
		 					    }
		 				 	}else
		 				 		return false;
		 				}, schemaTreeView.getSelectionModel().selectedItemProperty()));
		 		break;
		 		
		 	case RPC:
				if(sourceBtnId.equals("rpcBtn")) {
					schemaTreeView.setTooltip(new Tooltip("You can select only rpc input nodes"));
					okBtn.disableProperty().bind(
			 				Bindings.createBooleanBinding(() -> { 
			 				 	TreeItem<SchemaItem> treeItem  = schemaTreeView.getSelectionModel().selectedItemProperty().get();
			 				 	
			 				 	if(treeItem != null) {
				 				 	 TreeItem<SchemaItem> parentTreeItem = treeItem.getParent();
				 				 	 if(parentTreeItem != null) {
				 					 	switch (parentTreeItem.getValue().getTypeEnum())
				 					    {
					 					 case RPC:
					 						 if("input".equals(treeItem.getValue().getSchemaNode().getLocalName()))
					 							 return false;
					 						 else
					 							 return true;
				 					      default:        
				 					    	  return true;
				 					    }
				 				 	}
			 				 	}
			 				 	return true;
			 				}, schemaTreeView.getSelectionModel().selectedItemProperty()));
				}else if(sourceBtnId.equals("filterBtn")) {
					schemaTreeView.setTooltip(new Tooltip("You can select any data nodes"));
					okBtn.disableProperty().bind(
			 				Bindings.createBooleanBinding(() -> { 
			 				 	TreeItem<SchemaItem> treeItem  = schemaTreeView.getSelectionModel().selectedItemProperty().get();
			 				 	
			 				 	if(treeItem != null) {
			 					 	switch (treeItem.getValue().getTypeEnum())
			 					    {
				 					  case CONTAINER:
				 					  case LIST:
				 					  case LEAF_LIST:
				 					  case LEAF:
				 					  case CHOICE:
				 					  case CHOICE_CASE:
				 					  case ANY_XML:
				 						  return false;
			 					      default:        
			 					    	  return true;
			 					    }
			 				 	}else
			 				 		return false;
			 				}, schemaTreeView.getSelectionModel().selectedItemProperty()));
				}else if(sourceBtnId.equals("configBtn")) {
					schemaTreeView.setTooltip(new Tooltip("You can select only data root nodes"));
					okBtn.disableProperty().bind(
			 				Bindings.createBooleanBinding(() -> { 
			 				 	TreeItem<SchemaItem> treeItem  = schemaTreeView.getSelectionModel().selectedItemProperty().get();
			 				 	
			 				 	if(treeItem != null) {
			 					 	switch (treeItem.getValue().getTypeEnum())
			 					    {
				 					  case CONTAINER:
				 					  case LIST:
				 					  case LEAF_LIST:
				 					  case LEAF:
				 					  case CHOICE:
				 					  case CHOICE_CASE:
				 					  case ANY_XML:
				 						  return !treeItem.getValue().getSchemaNode().isRootNode();
			 					      default:        
			 					    	  return true;
			 					    }
			 				 	}else
			 				 		return false;
			 				}, schemaTreeView.getSelectionModel().selectedItemProperty()));
				}
		 		break;
		 		
		 	case NOTIFICATION:
		 		schemaTreeView.setTooltip(new Tooltip("You can select only notification nodes"));
		 		okBtn.disableProperty().bind(
		 				Bindings.createBooleanBinding(() -> { 
		 				 	TreeItem<SchemaItem> treeItem  = schemaTreeView.getSelectionModel().selectedItemProperty().get();
		 				 	
		 				 	if(treeItem != null) {
		 					 	switch (treeItem.getValue().getTypeEnum())
		 					    {
			 					 case NOTIFICATION: 
			 						 return false;
		 					      default:        
		 					    	  return true;
		 					    }
		 				 	}else
		 				 		return false;
		 				}, schemaTreeView.getSelectionModel().selectedItemProperty()));
		 		break;
		 		
		 	default:
		}
	}
	
}
