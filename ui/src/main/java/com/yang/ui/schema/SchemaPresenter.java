package com.yang.ui.schema;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import com.yang.ui.App;
import com.yang.ui.schema.any.AnyPresenter;
import com.yang.ui.schema.any.AnyView;
import com.yang.ui.schema.augmentation.AugmentationPresenter;
import com.yang.ui.schema.augmentation.AugmentationView;
import com.yang.ui.schema.choice.ChoicePresenter;
import com.yang.ui.schema.choice.ChoiceView;
import com.yang.ui.schema.choicecase.ChoicecasePresenter;
import com.yang.ui.schema.choicecase.ChoicecaseView;
import com.yang.ui.schema.container.ContainerPresenter;
import com.yang.ui.schema.container.ContainerView;
import com.yang.ui.schema.deviate.DeviatePresenter;
import com.yang.ui.schema.deviate.DeviateView;
import com.yang.ui.schema.deviation.DeviationPresenter;
import com.yang.ui.schema.deviation.DeviationView;
import com.yang.ui.schema.extension.ExtensionPresenter;
import com.yang.ui.schema.extension.ExtensionView;
import com.yang.ui.schema.feature.FeaturePresenter;
import com.yang.ui.schema.feature.FeatureView;
import com.yang.ui.schema.file.FilePresenter;
import com.yang.ui.schema.file.FileView;
import com.yang.ui.schema.group.GroupPresenter;
import com.yang.ui.schema.group.GroupView;
import com.yang.ui.schema.header.HeaderPresenter;
import com.yang.ui.schema.header.HeaderView;
import com.yang.ui.schema.identity.IdentityPresenter;
import com.yang.ui.schema.identity.IdentityView;
import com.yang.ui.schema.importfiles.ImportPresenter;
import com.yang.ui.schema.importfiles.ImportView;
import com.yang.ui.schema.info.InfoPresenter;
import com.yang.ui.schema.info.InfoView;
import com.yang.ui.schema.leaf.LeafPresenter;
import com.yang.ui.schema.leaf.LeafView;
import com.yang.ui.schema.leaflist.LeaflistPresenter;
import com.yang.ui.schema.leaflist.LeaflistView;
import com.yang.ui.schema.list.ListPresenter;
import com.yang.ui.schema.list.ListView;
import com.yang.ui.schema.module.ModulePresenter;
import com.yang.ui.schema.module.ModuleView;
import com.yang.ui.schema.notification.NotificationPresenter;
import com.yang.ui.schema.notification.NotificationView;
import com.yang.ui.schema.rpc.RpcPresenter;
import com.yang.ui.schema.rpc.RpcView;
import com.yang.ui.schema.schemanode.SchemanodePresenter;
import com.yang.ui.schema.schemanode.SchemanodeView;
import com.yang.ui.schema.type.TypePresenter;
import com.yang.ui.schema.type.TypeView;
import com.yang.ui.schema.uses.UsesPresenter;
import com.yang.ui.schema.uses.UsesView;
import com.yang.ui.services.FileService;
import com.yang.ui.services.QueryService;
import com.yang.ui.services.SchemaService;
import com.yang.ui.services.TemplateService;
import com.yang.ui.services.model.ErrorItem;
import com.yang.ui.services.model.SchemaItem;
import com.yang.ui.services.model.SchemaValidationStatus;
import com.yang.ui.services.model.YangFile;
import com.yang.ui.services.model.YangRepo;
import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;



public class SchemaPresenter implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(SchemaPresenter.class);
	
    @FXML
    SplitPane schemaPane;
    
    @FXML
    TreeView<SchemaItem> schemaTreeView;
    
    @FXML
    JFXButton importBtn;
    
    @FXML
    JFXButton newBtn;
    
    @FXML
    JFXButton deleteBtn;
    
    @FXML
    JFXButton validateBtn;
    
    @FXML
    TextField filterText;
    
    @FXML
	JFXButton sortBtn;
    
	private BooleanProperty sortedProperty = new SimpleBooleanProperty(true);//ASC->true, DSC->false
	public BooleanProperty getSortedProperty() {
		return sortedProperty;
	}
    
    @FXML
	JFXComboBox<String> filterTypeCmb;
    
    private ObservableList<String> filterTypeList;
    private ObservableList<String> getFilterTypeList() {
        if (filterTypeList == null) {
        	filterTypeList = FXCollections.observableArrayList();
        	filterTypeList.add("file");
        	filterTypeList.add("module");
        }
        return filterTypeList;
    }
    
//	@FXML
//	JFXToggleButton categoriesCheck;
//	private BooleanProperty categoriesProperty = new SimpleBooleanProperty(true);
	

	@FXML
    JFXToggleButton sourceCheck;
    private BooleanProperty sourceProperty = new SimpleBooleanProperty(false);
    
    @FXML
    HBox filterPane;
    
    private TreeItem<SchemaItem> rootTreeItem = new TreeItem<SchemaItem> ();//the root of the graphic tree is always merged with the graphic tree parsed from schema
    
	final private Comparator<TreeItem<SchemaItem>> treeItemComparator = new Comparator<TreeItem<SchemaItem>>() {//used to sort the graph after merge in order to preserver insertion order
        @Override
        public int compare(TreeItem<SchemaItem> item1, TreeItem<SchemaItem> item2) {
        	return Integer.compare(item1.getValue().getIndex(), item2.getValue().getIndex());
        }
    };
    
    @Inject
    SchemaService schemaSrv;
    
    @Inject
    QueryService querySrv;
    
    @Inject
	FileService fileService;
    
    @Inject
    TemplateService templateService;
    
    private SchemaRootService schemaLoaderSrv;
    
    private SelectSourceService selectSrcSrv;
    
    private DeleteFileService deleteFileSrv;
    
    
    //sort button icons
    private Text ascIcon;
    private Text dscIcon;

    //import view
    private ImportView importView;
    private ImportPresenter importPresenter;
    private Parent importPane;
    
    //SchemaItem views
    private InfoView infoView;
    private InfoPresenter infoPresenter;
    private Parent infoPane;
    
    private FileView fileView; 
    private FilePresenter filePresenter;
    private Parent filePane;
    
    private ModuleView moduleView;
    private ModulePresenter modulePresenter;
    private Parent modulePane;
    
    private SchemanodeView schemanodeView;
    private SchemanodePresenter schemanodePresenter;
    private Parent schemanodePane;
    
    private AnyView anyView;
    private AnyPresenter anyPresenter;
    private Parent anyPane;
    
    private ChoiceView choiceView;
    private ChoicePresenter choicePresenter;
    private Parent choicePane;
    
    private ChoicecaseView choicecaseView;
    private ChoicecasePresenter choicecasePresenter;
    private Parent choicecasePane;
    
    private ContainerView containerView;
    private ContainerPresenter containerPresenter;
    private Parent containerPane;
    
    private LeafView leafView;
    private LeafPresenter leafPresenter;
    private Parent leafPane;
    
    private LeaflistView leaflistView;
    private LeaflistPresenter leaflistPresenter;
    private Parent leaflistPane;
    
    private ListView listView;
    private ListPresenter listPresenter;
    private Parent listPane;
    
    private DeviationView deviationView;
    private DeviationPresenter deviationPresenter;
    private Parent deviationPane;
    
    private DeviateView deviateView;
    private DeviatePresenter deviatePresenter;
    private Parent deviatePane;
    
    private TypeView typeView;
    private TypePresenter typePresenter;
    private Parent typePane;
    
    private GroupView groupView;
    private GroupPresenter groupPresenter;
    private Parent groupPane;
    
    private UsesView usesView;
    private UsesPresenter usesPresenter;
    private Parent usesPane;
    
    private AugmentationView augmentationView;
    private AugmentationPresenter augmentationPresenter;
    private Parent augmentationPane;
    
    private ExtensionView extensionView;
    private ExtensionPresenter extensionPresenter;
    private Parent extensionPane;
    
    private FeatureView featureView;
    private FeaturePresenter featurePresenter;
    private Parent featurePane;
    
    private IdentityView identityView;
    private IdentityPresenter identityPresenter;
    private Parent identityPane;
    
    private NotificationView notificationView;
    private NotificationPresenter notificationPresenter;
    private Parent notificationPane;
    
    private RpcView rpcView;
    private RpcPresenter rpcPresenter;
    private Parent rpcPane;
    
    private HeaderView headerView;
    private HeaderPresenter headerPresenter;
    private Parent headerPane;
    
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	try {
    		//initialize schema root node; make sure you also initialize rootTreeItem from SchemaService
    		YangRepo repo = fileService.getYangRepo();
    		SchemaItem newRootItem = new SchemaItem(repo, 0);
    		rootTreeItem.setExpanded(true);
    		rootTreeItem.setValue(newRootItem);
    		rootTreeItem.setGraphic(newRootItem.getItemIcon());
    		
    		schemaTreeView.setRoot(rootTreeItem);
    		
    		//build schema loader service
    		schemaLoaderSrv = new SchemaRootService(null);
    		//schemaTreeView.rootProperty().bind(schemaLoaderSrv.valueProperty());//IT ALSO WORKS 
    		schemaLoaderSrv.setOnSucceeded((WorkerStateEvent t) ->{
        		    try {
        		    	//schemaTreeView.setRoot((TreeItem<SchemaItem>) t.getSource().getValue());
	        			TreeItem<SchemaItem> newRootTreeItem = (TreeItem<SchemaItem>) t.getSource().getValue();
	        			
	        			//merge rootTreeItem w newRootTreeItem for Update(non equals) + Delete
	        		    mergeTreeUpdateRec(rootTreeItem, newRootTreeItem);
	        		    //merge newRootTreeItem w rootTreeItem for Update(equals) + Add
						mergeTreeAddRec(newRootTreeItem, rootTreeItem);
						//sort the graph after merge in order to preserver insertion order
						sortTreeItemRec(rootTreeItem);
						
						//reselect item
						if(schemaLoaderSrv.getSelectedItem() != null)
							selectTreeItem(schemaLoaderSrv.getSelectedItem());
		        		else
		        			schemaTreeView.getSelectionModel().selectFirst();
//						if(schemaTreeView.getSelectionModel().getSelectedItem() == null)
//							schemaTreeView.getSelectionModel().selectFirst();
					} catch (Exception e) {
						LOG.error("Schema Error: ", e);
					}
        	});
    		schemaLoaderSrv.setOnFailed((WorkerStateEvent t) -> {
    			LOG.error("Schema Failed: " + schemaLoaderSrv.getException());
        	});
    		
    		//SchemaItem fileItem = fileTreeItem.getValue();
        	deleteFileSrv = new DeleteFileService();
        	deleteFileSrv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
        		public void handle(WorkerStateEvent t) {
        			LOG.trace("DeleteFileService Done: " + t.getSource().getValue());
    				schemaLoaderSrv.restart();
        	    }
        	});
    		
    		//select source service
    		selectSrcSrv = new SelectSourceService();
    		selectSrcSrv.setOnSucceeded((WorkerStateEvent t) ->{
    			Integer index = (Integer) t.getSource().getValue();
    			schemaSrv.getSourceIndexProperty().set(0);//force refresh
    			schemaSrv.getSourceIndexProperty().set(index);//FilePresenter watches it
        	});
    		selectSrcSrv.setOnFailed((WorkerStateEvent t) -> {
    			LOG.error("Selecte Source Failed: " + selectSrcSrv.getException());
        	});
    		
    		
    		//build filter pane
     		filterPane.getStyleClass().add("filterPane");
     		filterText.textProperty().addListener((obj, oldVal, newVal) -> {
     			
     			if(schemaLoaderSrv.isRunning()) {
     				schemaLoaderSrv.cancel();
     				schemaLoaderSrv.reset();
     			}
 				schemaLoaderSrv.restart();
     		});
     		ascIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.ARROW_UP_BOLD, "27px");

    		//ascIcon.setFill(Paint.valueOf("#17bec9"));
     		ascIcon.setFill(Paint.valueOf("white"));
    		ascIcon.getStyleClass().add("sorticon");
    		dscIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.ARROW_DOWN_BOLD, "27px");
    		dscIcon.setFill(Paint.valueOf("white"));
    		dscIcon.getStyleClass().add("sorticon");
    		sortBtn.setText(null);
     		sortBtn.graphicProperty().bind(
     			Bindings.createObjectBinding(
    		        new Callable<Text>() {
    		            @Override
    		            public Text call() throws Exception {
    		            	boolean isAsc = getSortedProperty().get();
    		            	if(isAsc)
    		            		return dscIcon;
    		            	else
    		            		return ascIcon;
    		            }
    		        }, 
    		getSortedProperty()));
        	filterTypeCmb.setCellFactory(c -> new FilterTypeCell());
        	filterTypeCmb.setButtonCell(new FilterTypeCell());
    		filterTypeCmb.setItems(getFilterTypeList());
    		filterTypeCmb.getSelectionModel().selectFirst();
    		
    		//disable buttons until service is ready
    		BooleanBinding isRunning1 = Bindings.createBooleanBinding(() -> schemaLoaderSrv.runningProperty().get(), schemaLoaderSrv.runningProperty());
    		BooleanBinding isRunning2 = Bindings.createBooleanBinding(() -> deleteFileSrv.runningProperty().get(), deleteFileSrv.runningProperty());
    		BooleanBinding isRunning3 = Bindings.createBooleanBinding(() -> selectSrcSrv.runningProperty().get(), selectSrcSrv.runningProperty());
    		BooleanBinding isRunningAny = isRunning1.or(isRunning2);
    		importBtn.disableProperty().bind(isRunningAny);
    		newBtn.disableProperty().bind(isRunningAny);
    		validateBtn.disableProperty().bind(isRunningAny);
    		filterTypeCmb.disableProperty().bind(isRunningAny);
    		sortBtn.disableProperty().bind(isRunningAny);
    		sourceCheck.disableProperty().bind(isRunningAny);
    		//filterText.disableProperty().bind(isRunning);//loses focus
    		schemaTreeView.disableProperty().bind(isRunningAny);
        	
    		//build panels for each SchemaItem
    		schemaTreeView.setEditable(false);
    		schemaTreeView.setCellFactory((TreeView<SchemaItem> p) -> new SchemaItemTreeCellImpl());
    		
		    infoView = new InfoView();
		    infoPresenter = (InfoPresenter) infoView.getPresenter();
		    infoPane = infoView.getView();
		    
		    fileView = new FileView();
			filePresenter = (FilePresenter) fileView.getPresenter();
			filePane = fileView.getView();
		    
		    moduleView = new ModuleView();
			modulePresenter = (ModulePresenter) moduleView.getPresenter();
			modulePane = moduleView.getView();
			
			schemanodeView = new SchemanodeView();
			schemanodePresenter = (SchemanodePresenter) schemanodeView.getPresenter();
			schemanodePane = schemanodeView.getView();
			
			anyView = new AnyView();
			anyPresenter = (AnyPresenter) anyView.getPresenter();
			anyPane = anyView.getView();
			
			choiceView = new ChoiceView();
			choicePresenter = (ChoicePresenter) choiceView.getPresenter();
			choicePane = choiceView.getView();
			
			choicecaseView = new ChoicecaseView();
			choicecasePresenter = (ChoicecasePresenter) choicecaseView.getPresenter();
			choicecasePane = choicecaseView.getView();
			
			containerView = new ContainerView();
			containerPresenter = (ContainerPresenter) containerView.getPresenter();
			containerPane = containerView.getView();
			
			leafView = new LeafView();
			leafPresenter = (LeafPresenter) leafView.getPresenter();
			leafPane = leafView.getView();
			
			leaflistView = new LeaflistView();
			leaflistPresenter = (LeaflistPresenter) leaflistView.getPresenter();
			leaflistPane = leaflistView.getView();
			
			listView = new ListView();
			listPresenter = (ListPresenter) listView.getPresenter();
			listPane = listView.getView();
			
			deviationView = new DeviationView();
			deviationPresenter = (DeviationPresenter) deviationView.getPresenter();
			deviationPane = deviationView.getView();
			
			deviateView = new DeviateView();
			deviatePresenter = (DeviatePresenter) deviateView.getPresenter();
			deviatePane = deviateView.getView();
			
			typeView = new TypeView();
			typePresenter = (TypePresenter) typeView.getPresenter();
			typePane = typeView.getView();
			
			groupView = new GroupView();
			groupPresenter = (GroupPresenter) groupView.getPresenter();
			groupPane = groupView.getView();
			
			usesView = new UsesView();
			usesPresenter = (UsesPresenter) usesView.getPresenter();
			usesPane = usesView.getView();
			
			augmentationView = new AugmentationView();
			augmentationPresenter = (AugmentationPresenter) augmentationView.getPresenter();
			augmentationPane = augmentationView.getView();
			 
			extensionView = new ExtensionView();
			extensionPresenter = (ExtensionPresenter) extensionView.getPresenter();
			extensionPane = extensionView.getView();
			
			featureView = new FeatureView();
			featurePresenter = (FeaturePresenter) featureView.getPresenter();
			featurePane = featureView.getView();
			
			identityView = new IdentityView();
			identityPresenter = (IdentityPresenter) identityView.getPresenter();
			identityPane = identityView.getView();
			
			notificationView = new NotificationView();
			notificationPresenter = (NotificationPresenter) notificationView.getPresenter();
			notificationPane = notificationView.getView();
			
			rpcView = new RpcView();
			rpcPresenter = (RpcPresenter) rpcView.getPresenter();
			rpcPane = rpcView.getView();
			
			headerView = new HeaderView();
			headerPresenter = (HeaderPresenter) headerView.getPresenter();
			headerPane = headerView.getView();
			
			//update line number for target link belonging to either uses, augmentation or deviation
	    	schemaSrv.getLinkProperty().addListener( ( ov, oldv, newv ) -> {
	    		String xpath = (String)newv;
	    		
	    		TreeItem<SchemaItem> targetTreeItem = schemaSrv.searchTreeItemRec( schemaTreeView.getRoot(), (SchemaItem p) -> p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ? p.getSchemaNode().getXpath().equals(xpath) : false);
	    		if(targetTreeItem != null) {
		    		selectTreeItem(xpath);
		    		schemaTreeView.scrollTo(schemaTreeView.getRow(targetTreeItem));
	    		}
	    	});
	    	
			//on file update refresh selection in schema tree
			filePresenter.updatedFileProperty().addListener(new ChangeListener<SchemaItem>() {
	                @Override
	                public void changed(ObservableValue<? extends SchemaItem> updatedFileProperty, SchemaItem oldUpdatedFile, SchemaItem updatedFile) {
	                    
	                    if (updatedFileProperty.getValue() == null || updatedFile == null ) {
	                    	LOG.trace("FileUpdateEvent updatedFileProperty = null");
	                    	return; 
	                    }
	                    //LOG.debug("FileUpdateEvent updatedFileProperty = " + updatedFileProperty.getValue().getFile().getName() + " old " + oldUpdatedFile + " new " + updatedFile.getFile().getName());	
	                    switch (updatedFile.getStatusEnum()) {
			                 case OK:
		                		schemaLoaderSrv.restart();
			                     break;
			                 case ERR_NO_FILE_NAME:
			                 case ERR_FILE_DUPLICATE:
			                	 
			                 case ERR_NO_FILE_CONTENT:
			                	 
			                 case ERR_NO_MODULE:
			                 case ERR_MODULE_DUPLICATE:
			                	 
			                 case ERR_NO_NAMESPACE:
			                 case ERR_NAMESPACE_DUPLICATE:
			                	 
			                 case ERR_NO_MOD_REVISION://never happens
			                 case ERR_NO_MOD_IMPORT:
			                	 
			                 case ERR_IO:
			                	 LOG.error("FileUpdate error " + updatedFile.getStatusEnum().text());
			                	 
			         			 for(TreeItem<SchemaItem> fileTreeItem : schemaTreeView.getRoot().getChildren()){
			         				
			         				if(updatedFile.getFile() != null && fileTreeItem.getValue().getFile().getName().equalsIgnoreCase( updatedFile.getFile().getName() )) {
			         					fileTreeItem.setValue(updatedFile);
			         					
			         					 LOG.error("FileUpdate error content length " + updatedFile.getFile().getContent().length());
			         					
			         					fileTreeItem.getChildren().clear();	 
			         					//force tree item update
			         					fileTreeItem.getParent().setExpanded(false);
			         					fileTreeItem.getParent().setExpanded(true);
			         					//re select
			         					
			         					selectTreeItem(updatedFile);
			         			    	//schemaTreeView.getSelectionModel().select(fileTreeItem);
			         					break;
			         				}
			         			 }
			                     break;
			                 case ERR_YANG:
			                	 break; 
			                 default:
			                	 LOG.error("FileUpdate returned N/A Errors ");
	                    }
	                }
			});
			
			//reload schema after feature update 
			modulePresenter.updateFeatureProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
    			schemaLoaderSrv.restart();
    		});
			modulePane.disableProperty().bind(schemaLoaderSrv.runningProperty());
			
			
			schemaPane.setDividerPositions(0.38);
			//on schema item select refresh right side pane 
    		schemaTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    		schemaTreeView.getSelectionModel().selectedItemProperty().addListener(
    			new ChangeListener<TreeItem <SchemaItem>>() {
    				@Override
    			    public void changed(ObservableValue<? extends TreeItem<SchemaItem>> selectedItemProperty, TreeItem<SchemaItem> oldSelectedTreeItem, TreeItem<SchemaItem> selectedTreeItem) {
	    					if(selectedTreeItem == null)
	    						return;
		    				//schema item details pane is not present in the stage from the start
		    				if(schemaPane.getItems().size() > 1) {
		    					double[] divPositions = schemaPane.getDividerPositions();
		    					schemaPane.getItems().remove( schemaPane.getItems().size()-1 );
		    					schemaPane.setDividerPositions(divPositions[0]);
		    				}
		    				SchemaItemType selectedItemType = selectedTreeItem.getValue().getTypeEnum();
		    				
		    				//delete button state
		    				if(selectedItemType != SchemaItemType.FILE)
		    					deleteBtn.setDisable(true);
		    				else
		    					deleteBtn.setDisable(false);
		    				
		    				//since file content is not observable value update it on lost selection
//		    				if(oldSelectedTreeItem != null ) {
//			    				SchemaItemType oldSelectedItemType = oldSelectedTreeItem.getValue().getTypeEnum();
//			    					filePresenter.updateOldFileContent(oldSelectedTreeItem.getValue());
//		    				}
		    				
		    				//Source View always loads file presenter
		    				if(selectedItemType != SchemaItemType.FILE && selectedItemType != SchemaItemType.ROOT_NODE) {
		    					if(SchemaPresenter.this.sourceProperty.get()) {
		    						
	    							TreeItem<SchemaItem> fileTreeItem = getRootTreeItem(selectedTreeItem);
	    							//fileTreeItem.getValue().setSourceMsg(selectedTreeItem.getValue().getSourceMsg());
	    							filePresenter.updateFileItem(fileTreeItem.getValue());
	    							
		     					    schemaPane.getItems().add(filePane);
		     					    
		     					   //String fileContent = fileTreeItem.getValue().getFile().getContent();
				    				selectSrcSrv.setData(selectedTreeItem, fileTreeItem.getValue().getFile().getContent());//this updates SchemaService.sourceIndexProperty which FilePresenter watches
				    				selectSrcSrv.restart();
		     					    return;
	    						}
		    				}
		    				//Non Source Views
		    				switch (selectedItemType)
		    				{
		    					case ROOT_NODE:
		    						infoPresenter.updateInfoItem(selectedTreeItem.getValue());
		     					    schemaPane.getItems().add(infoPane);
		    					    break;
		    					    
		    					case FILE:
		     					    filePresenter.updateFileItem(selectedTreeItem.getValue());
		     					    schemaPane.getItems().add(filePane);
		     					    break;
		     					    
		    					case MODULE:
		    						modulePresenter.updateModuleItem(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(modulePane);
		     					    break;
		     					    
		    					case ANY_XML:
		    						anyPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(anyPane);
		     					    break;
		     					    
		    					case CHOICE:
		    						choicePresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(choicePane);
		     					    break;
		     					    
		    					case CHOICE_CASE:
		    						choicecasePresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(choicecasePane);
		     					    break;
		     					    
		    					case CONTAINER:
			    					containerPresenter.updateView(selectedTreeItem.getValue());
			    					schemaPane.getItems().add(containerPane);
		     					    break;
		     					    
		    					case LEAF:
		    						leafPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(leafPane);
		     					    break;
		     					    
		    					case LEAF_LIST:
		    						leaflistPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(leaflistPane);
		     					    break;
		     					    
		    					case LIST:
		    						listPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(listPane);
		     					    break;
		     					    
		    					case DEVIATION:
		    						deviationPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(deviationPane);
		     					    break;
		     					    
		    					case DEVIATE:
		    						deviatePresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(deviatePane);
		     					    break;
		     					    
		    					case TYPEDEF:
		    						typePresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(typePane);
		     					    break;
		     					    
		    					case GROUPING:
		    						groupPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(groupPane);
		     					    break;
		     					    
		    					case USES:
		    						usesPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(usesPane);
		     					    break;
		     					    
		    					case AUGMENTATION:
		    						augmentationPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(augmentationPane);
		     					    break;
		     					    
		    					case EXTENSION:
		    						extensionPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(extensionPane);
		     					    break;
		     					    
		    					case FEATURE:
		    						featurePresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(featurePane);
		     					    break;
		     					    
		    					case IDENTITY:
		    						identityPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(identityPane);
		     					    break;
		     					    
		    					case NOTIFICATION:
		    						notificationPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(notificationPane);
		     					    break;
		     					    
		    					case RPC:
		    						rpcPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(rpcPane);
		     					    break;
		     					    
		    					case HEADER:
		    						headerPresenter.updateView(selectedTreeItem.getValue());
			     					schemaPane.getItems().add(headerPane);
		     					    break;
		     					    
		    					default:   
		    						LOG.error("Unknown TreeItem Type");
		    					 	break;
		    				}//end switch
		    				
	    				}
    		});
    		//load schema
    		schemaLoaderSrv.start();
    	}catch(Exception ex) {
    		LOG.error("Schema initialization exception : ", ex);
    	}
    }
    

    @FXML
    protected void selectFilterAction(ActionEvent event) {
    	//String filterType = filterTypeCmb.getSelectionModel().getSelectedItem();
		schemaLoaderSrv.restart();
    }
    
    
    @FXML
    protected void sortAction(ActionEvent event) {
    	sortedProperty.set(!sortedProperty.get());
		schemaLoaderSrv.restart();
    }
    
    
//    @FXML
//    protected void categoriesAction(ActionEvent event) {
//    	categoriesProperty.set(!categoriesProperty.get());
//    	schemaLoaderSrv.restart();
//    }
    
    @FXML
    protected void sourceAction(ActionEvent event) {
    	sourceProperty.set(!sourceProperty.get());
    	TreeItem<SchemaItem>  selectedItem = schemaTreeView.getSelectionModel().getSelectedItem();
    	schemaTreeView.getSelectionModel().selectFirst();
    	schemaTreeView.getSelectionModel().select(selectedItem);
    }
    
    @FXML
    protected void importAction(ActionEvent event) {
    	//build the importView here as a modal dlg; when close dlg do nothing since all the import is done in the ImportView
        final Stage importDlg = new Stage();
        importDlg.setTitle("Yang Files Import");
        importDlg.initModality(Modality.APPLICATION_MODAL);
        importDlg.initOwner((Stage) App.MAIN_STAGE);
        importDlg.initStyle(StageStyle.UTILITY);
        importDlg.setOnHidden(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	//schemaLoaderSrv.setSelectedItem(null);
            	schemaLoaderSrv.restart();
            }
        });     
	    importView = new ImportView();
	    importPresenter = (ImportPresenter) importView.getPresenter();
	    importPane = importView.getView();
        Scene dialogScene = new Scene(importPane, App.IMPORT_DLG_WIDTH, App.IMPORT_DLG_HEIGHT);
        importDlg.setScene(dialogScene);
        importDlg.show();
    }
    
    
    @FXML
    protected void newFileAction(ActionEvent event) {
    	TreeItem<SchemaItem> newFileTreeItem = schemaSrv.searchTreeItemRec( schemaTreeView.getRoot(), (SchemaItem p) -> (p.getFile() != null ? p.getFile().getId() == 0 : false));
		if(newFileTreeItem != null) {
    		schemaTreeView.getSelectionModel().select(newFileTreeItem); //scroll in view
    		schemaTreeView.scrollTo(schemaTreeView.getRow(newFileTreeItem));
		}else {
	        YangFile yangFile = templateService.getDefaultTemplateFile();
	        SchemaItem fileItem = new SchemaItem(yangFile, 0); 
		    TreeItem<SchemaItem> fileTreeItem = new TreeItem<SchemaItem> (fileItem, fileItem.getItemIcon());
		    
		    schemaTreeView.getRoot().getChildren().add(fileTreeItem);
		    schemaTreeView.scrollTo(schemaTreeView.getRow(fileTreeItem));//scroll in view
		    schemaTreeView.getSelectionModel().select(fileTreeItem);
		}
    }
    
    
    @FXML
    protected void deleteFileAction(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Delete File Confirmation");
    	alert.setHeaderText("");
    	alert.setContentText("Are you sure you want to delete Yang file from schema ?");
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		TreeItem<SchemaItem> fileTreeItem = schemaTreeView.getSelectionModel().getSelectedItem();
    		if(fileTreeItem == null || fileTreeItem.getValue().getTypeEnum() != SchemaItemType.FILE)//delete only files items -> also disabled from GUI
    			return;
    		else {
            	SchemaItem fileItem = fileTreeItem.getValue();
            	deleteFileSrv.setSelectedItem(fileItem);
            	deleteFileSrv.restart();
            }
    	} else {
    	    // ... user chose CANCEL or closed the dialog
    	}
    }
    
    
    @FXML
    protected void validateSchemaAction(ActionEvent event) {
    	//schemaLoaderSrv.setSelectedItem(schemaTreeView.getSelectionModel().getSelectedItem().getValue());
		schemaLoaderSrv.restart();
    }
    
    
    public void selectError(ErrorItem errorItem) {//called from MainPresenter.selectedItemProperty()
    	String fileId = errorItem.getSource();
    	
    	TreeItem<SchemaItem> fileTreeItem = schemaSrv.searchTreeItemRec( schemaTreeView.getRoot(), (SchemaItem p) -> SchemaItemType.FILE == p.getTypeEnum() && p.getFile().getIdString().equals(fileId));
		if(fileTreeItem != null) {
			System.out.println("File Tree Item found ");
    		selectTreeItem(fileTreeItem.getValue());
    		schemaTreeView.scrollTo(schemaTreeView.getRow(fileTreeItem));
		}else
			System.out.println("File Tree Item not found ");
    }
    
    
	private TreeItem<SchemaItem> getRootTreeItem(TreeItem<SchemaItem> treeItem){//returns first parent in path of type FILE; treeItem is always YangSchemaNode
		TreeItem<SchemaItem> fileTreeItem = null;
		TreeItem<SchemaItem> parentTreeItem = null;
		while( (parentTreeItem = treeItem.getParent()) != null ) {
			if(SchemaItemType.FILE == parentTreeItem.getValue().getTypeEnum()) {
				fileTreeItem = parentTreeItem;
				break;
			}
			treeItem = parentTreeItem;
		}
		return fileTreeItem;
    }
	
	
//	//return the TreeItem which satisfies predicate.test(SchemaItem)==true
//	private TreeItem<SchemaItem> searchTreeItemRec(TreeItem<SchemaItem> treeItem, Predicate<SchemaItem> predicate) {
//				
//		SchemaItem item = treeItem.getValue();
//		if(item != null && predicate.test(item)) {
//			return treeItem;
//		}
//		for(TreeItem<SchemaItem> treeItemChild : treeItem.getChildren()){
//			TreeItem<SchemaItem> foundItem = searchTreeItemRec(treeItemChild, predicate);
//			if(foundItem != null)
//				return foundItem;
//		}
//		return null;
//	}
		
		
	//called from newFileAction() & onFileUpdated property		
	public void selectTreeItem(SchemaItem selectedItem) {										
		schemaTreeView.getSelectionModel().selectFirst();
			
		TreeItem<SchemaItem> selectedTreeItem = schemaSrv.searchTreeItemRec( schemaTreeView.getRoot(), (SchemaItem p) -> p.equals(selectedItem));
		schemaTreeView.getSelectionModel().select(selectedTreeItem);
    }
	
	
	//called from navigate link -> Augmentation, Deviation, Uses
	private void selectTreeItem(String xpath) {										
		schemaTreeView.getSelectionModel().selectFirst();
		
		TreeItem<SchemaItem> selectedTreeItem = schemaSrv.searchTreeItemRec( schemaTreeView.getRoot(), (SchemaItem p) -> p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ? p.getSchemaNode().getXpath().equals(xpath) : false);
		schemaTreeView.getSelectionModel().select(selectedTreeItem);
    }
	
	
	//sort the graph after merge in order to preserver insertion order
	public void sortTreeItemRec(TreeItem<SchemaItem> treeItem) throws Exception {
		ObservableList<TreeItem<SchemaItem>> treeItemChildren = treeItem.getChildren();
		//sort treeItemChildren
		FXCollections.sort(treeItemChildren, treeItemComparator);
		
		for(TreeItem<SchemaItem> treeItemChild : treeItemChildren)
			sortTreeItemRec(treeItemChild);
	}
	

	//merge newTreeItem w treeItem for Add + Update(equals); parse newTreeItem -> search each element in treeItem and if not found add it on its parent from treeItem
	public void mergeTreeAddRec(TreeItem<SchemaItem> newTreeItem, TreeItem<SchemaItem> treeItem) throws Exception {
			
		SchemaItem newItem = newTreeItem.getValue();
		TreeItem<SchemaItem> foundTreeItem = searchTreeItemRec(newItem,  treeItem);
		if(foundTreeItem == null) {
			TreeItem<SchemaItem> parentNewTreeItem = newTreeItem.getParent();//parentNewItem of parentNewTreeItem is always found in treeItem
			SchemaItem parentNewItem = parentNewTreeItem.getValue();
				
			TreeItem<SchemaItem> foundTreeItem1 = searchTreeItemRec(parentNewItem,  treeItem);//index=???
			if(foundTreeItem1 == null) {
				throw new Exception("Parent not found");
			}else {
				foundTreeItem1.getChildren().add(newTreeItem);
			}
		}else {
			for(TreeItem<SchemaItem> newTreeItemChild : newTreeItem.getChildren())
				mergeTreeAddRec(newTreeItemChild, treeItem);
		}
	}
		
		
	//merge treeItem w newTreeItem for Update + Delete; parse treeItem -> search each element in newTreeItem and update it back in treeItem
	public void mergeTreeUpdateRec(TreeItem<SchemaItem> treeItem, TreeItem<SchemaItem> newTreeItem) {
		//purge children first
		List<TreeItem<SchemaItem>> removedItems = new ArrayList<TreeItem<SchemaItem>>(0);
		for(TreeItem<SchemaItem> treeItemChild : treeItem.getChildren()) {
			SchemaItem itemChild = treeItemChild.getValue();
			TreeItem<SchemaItem> foundTreeItem = searchTreeItemRec(itemChild,  newTreeItem);
			if(foundTreeItem == null) {
				removedItems.add(treeItemChild);
			}else {
				SchemaItem foundItem= foundTreeItem.getValue();
				if(SchemaItemType.FILE == foundItem.getTypeEnum() && itemChild.getFile().getId() == 0) {//remove unsaved new files at refresh
					removedItems.add(treeItemChild);
				}else
					treeItemChild.setValue(foundTreeItem.getValue());//???
			}
		}
		treeItem.getChildren().removeAll(removedItems);
		
		for(TreeItem<SchemaItem> treeItemChild : treeItem.getChildren())
			mergeTreeUpdateRec(treeItemChild, newTreeItem);
			
	}
	
		
	//return the TreeItem which contains a SchemaItem equal w item
	private TreeItem<SchemaItem> searchTreeItemRec(SchemaItem item, TreeItem<SchemaItem> newTreeItem) {
			
		SchemaItem newItem = newTreeItem.getValue();
		if(newItem != null && newItem.equals(item)) {
			return newTreeItem;
		}
		for(TreeItem<SchemaItem> newTreeItemChild : newTreeItem.getChildren()){
			TreeItem<SchemaItem> foundItem = searchTreeItemRec(item, newTreeItemChild);
			if(foundItem != null)
				return foundItem;
	    }
		return null;
	}
    
	//Builds regex from treeItem path, matches in 
    private final class SelectSourceService extends Service<Integer> {
    	
    	private TreeItem<SchemaItem> treeItem;
    	
    	private String content;
    	
    	public void setData(TreeItem<SchemaItem> treeItem, String content) {
			this.treeItem = treeItem;
			this.content = content;
		}

		public SelectSourceService() {
			super();
	    }	
    	
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                	 try {
                		return schemaSrv.getIndexFileContent(treeItem, content);
                	 }catch(Exception ex) {
                		 ex.printStackTrace();
                		 return 0;
                	 }
                }
            };
        }
   }
    
    //Builds schema tree from schema repository
    private final class SchemaRootService extends Service<TreeItem<SchemaItem>> {
    	
    	SchemaItem selectedItem;
    	
    	public SchemaRootService(SchemaItem schemaItem) {
			super();
	    }	
    	
	    public final SchemaItem getSelectedItem() {
	        return selectedItem;
	    }
	    
        @Override
        protected Task<TreeItem<SchemaItem>> createTask() {
            return new Task<TreeItem<SchemaItem>>() {
                @Override
                protected TreeItem<SchemaItem> call() throws Exception {
                	 TreeItem<SchemaItem> treeItem = null;
                	 try {
                		 //schemaLoaderSrv.setSelectedItem(schemaTreeView.getSelectionModel().getSelectedItem().getValue());
                		 
                		TreeItem<SchemaItem> selSchemaItem = schemaTreeView.getSelectionModel().getSelectedItem();//keep selected item on service in order to restore it when schema load is done
                		if(selSchemaItem != null)
                			selectedItem = schemaTreeView.getSelectionModel().getSelectedItem().getValue();
                		//String filterType, String filterText, boolean ascending
                		String filterType = filterTypeCmb.getSelectionModel().getSelectedItem();
                		String filter = filterText.getText();
                		boolean isAscending = sortedProperty.get();
                		//boolean isCategories = categoriesProperty.get();
                		boolean isCategories = false;
                		
                		treeItem = schemaSrv.getSchemaTreeRoot(filterType, filter, isAscending, isCategories);
                	 }catch(Exception ex) {
                		 ex.printStackTrace();
                	 }
                	 return treeItem;
                }
            };
        }
   }
    
    
    //Deletes local file in the schema repository
    private final class DeleteFileService extends Service<Boolean> {
    	
    	SchemaItem selectedItem;

		public DeleteFileService() {
			super();
	    }	
    	
	    public final SchemaItem getSelectedItem() {
	        return selectedItem;
	    }
    	
    	public void setSelectedItem(SchemaItem selectedItem) {
			this.selectedItem = selectedItem;
		}
    	
	      @Override
	      protected Task<Boolean> createTask() {
	          return new Task<Boolean>() {
	              @Override
	              protected Boolean call() {
	            	  try {
						return fileService.deleteFile(selectedItem);
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
	              }
	          };
	      }
    }
    
    
    //Custom TreeCell implementation able to display SchemaItem objects with different colors
    private final class SchemaItemTreeCellImpl extends TreeCell<SchemaItem> {   
    	 
        public SchemaItemTreeCellImpl() {
        }
 
        @Override
        public void updateItem(SchemaItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                setStyle(null);
            } else {
            	if(item.getStatusEnum() != SchemaValidationStatus.OK) {
            		//setText(getString() + "	" + item.getStatusEnum().text());
            	}else {
            		setText(getString());
            	}
                setGraphic(getTreeItem().getGraphic());
                
                switch (item.getTypeEnum())
				{
					
					case FILE:
						setStyle("-fx-font-weight: bold;");
 					    break;
					default:
						setStyle("-fx-font-weight: regular;");
						break;
//					default:
//						setStyle(null);
//					 	break;
				}
                
                if(item.getStatusEnum() != SchemaValidationStatus.OK) {
            		setStyle("-fx-background-color: #e74856; -fx-font-weight: bold;");
            		setGraphic(createErrorFile(getTreeItem().getGraphic(), getString() , item.getStatusEnum().text()));
            		setText(null);
            	}
            }
        }
 
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
    
    private Node createErrorFile(Node graphic, String name, String error) {
    	HBox panel = new HBox();
    	//panel.setStyle("-fx-background-color: transparent;");
    	
    	Label nameLabel = new Label(name + "		");
    	//nameLabel.setStyle("-fx-text-fill : white; -fx-background-color: transparent;");
    	
    	Label errorLabel = new Label(error);
    	//errorLabel.setStyle("-fx-text-fill : white; background-color: #e74856;");
    	
    	panel.getChildren().add(graphic);
    	panel.getChildren().add(nameLabel);
    	panel.getChildren().add(errorLabel);
    	return panel;
    }
    
    
    private class FilterTypeCell extends ListCell<String> {
    	
    	private Text moduleIcon;
    	 
    	private Text fileIcon;
    	
        public FilterTypeCell() {
			super();
			fileIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.FILE_OUTLINE);
      		fileIcon.setFill(Paint.valueOf("cyan"));
      		moduleIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.MAXCDN);
      		moduleIcon.setFill(Paint.valueOf("#f8e71c"));
		}

		protected void updateItem(String item, boolean empty){
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if(item!=null){
            	if(item.equals("file")) { 
            		setGraphic(fileIcon);
            		setText("file");
            	}else { 
            		setGraphic(moduleIcon);
            		setText("module");
            	}
            }
        }
    }
    
    
    
}
