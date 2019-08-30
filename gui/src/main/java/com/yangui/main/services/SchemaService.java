package com.yangui.main.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.opendaylight.yangtools.yang.model.api.SchemaNode;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.UniqueConstraint;
import org.opendaylight.yangtools.yang.model.api.UsesNode;
import org.opendaylight.yangtools.yang.model.api.stmt.SchemaNodeIdentifier.Relative;
import org.opendaylight.yangtools.yang.model.parser.api.YangSyntaxErrorException;
import org.opendaylight.yangtools.yang.parser.spi.meta.ReactorException;
import org.opendaylight.yangtools.yang.parser.spi.meta.SomeModifiersUnresolvedException;
import org.opendaylight.yangtools.yang.parser.spi.source.SourceException;
import org.opendaylight.yangtools.yang.parser.spi.source.StatementStreamSource;
import org.opendaylight.yangtools.yang.parser.stmt.reactor.CrossSourceStatementReactor;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.YangInferencePipeline;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.YangStatementSourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yangui.main.services.model.CapabilityItem;
import com.yangui.main.services.model.ErrorItem;
import com.yangui.main.services.model.ErrorTypeEnum;
import com.yangui.main.services.model.FeatureItem;
import com.yangui.main.services.model.ModuleIdentifier;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.SchemaValidationStatus;
import com.yangui.main.services.model.YangAny;
import com.yangui.main.services.model.YangAugmentation;
import com.yangui.main.services.model.YangAugmentationHeader;
import com.yangui.main.services.model.YangChoice;
import com.yangui.main.services.model.YangChoiceCase;
import com.yangui.main.services.model.YangContainer;
import com.yangui.main.services.model.YangDataHeader;
import com.yangui.main.services.model.YangDeviate;
import com.yangui.main.services.model.YangDeviation;
import com.yangui.main.services.model.YangDeviationHeader;
import com.yangui.main.services.model.YangExtension;
import com.yangui.main.services.model.YangExtensionHeader;
import com.yangui.main.services.model.YangFeature;
import com.yangui.main.services.model.YangFeatureHeader;
import com.yangui.main.services.model.YangFile;
import com.yangui.main.services.model.YangGrouping;
import com.yangui.main.services.model.YangGroupingHeader;
import com.yangui.main.services.model.YangIdentity;
import com.yangui.main.services.model.YangIdentityHeader;
import com.yangui.main.services.model.YangLeaf;
import com.yangui.main.services.model.YangLeafList;
import com.yangui.main.services.model.YangList;
import com.yangui.main.services.model.YangModule;
import com.yangui.main.services.model.YangNode;
import com.yangui.main.services.model.YangNotification;
import com.yangui.main.services.model.YangNotificationHeader;
import com.yangui.main.services.model.YangRepo;
import com.yangui.main.services.model.YangRpc;
import com.yangui.main.services.model.YangRpcHeader;
import com.yangui.main.services.model.YangSchemaNode;
import com.yangui.main.services.model.YangType;
import com.yangui.main.services.model.YangTypeHeader;
import com.yangui.main.services.model.YangUses;
import com.yangui.main.services.model.SchemaItem.SchemaItemType;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.impl.schema.SchemaUtils;
import org.opendaylight.yangtools.yang.model.api.AnyXmlSchemaNode;
import org.opendaylight.yangtools.yang.model.api.AugmentationSchema;
import org.opendaylight.yangtools.yang.model.api.AugmentationTarget;
import org.opendaylight.yangtools.yang.model.api.ChoiceCaseNode;
import org.opendaylight.yangtools.yang.model.api.ChoiceSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.DataNodeContainer;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.DeviateDefinition;
import org.opendaylight.yangtools.yang.model.api.Deviation;
import org.opendaylight.yangtools.yang.model.api.ExtensionDefinition;
import org.opendaylight.yangtools.yang.model.api.FeatureDefinition;
import org.opendaylight.yangtools.yang.model.api.GroupingDefinition;
import org.opendaylight.yangtools.yang.model.api.IdentitySchemaNode;
import org.opendaylight.yangtools.yang.model.api.LeafListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.LeafSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.MustDefinition;
import org.opendaylight.yangtools.yang.model.api.NotificationDefinition;
import org.opendaylight.yangtools.yang.model.api.RevisionAwareXPath;
import org.opendaylight.yangtools.yang.model.api.RpcDefinition;



@Singleton
public class SchemaService  {
	
	private static final Logger LOG = LoggerFactory.getLogger(SchemaService.class);
	
	@Inject
	private FileService fileService;
	
	@Inject
	private RegexService regexService;
	
	@Inject
    LogService logService;
	
	private Map<String, YangModule> modulesMap = new HashMap<String, YangModule>();//??????Generate XML??????
	
	private Map<String, ModuleIdentifier> moduleIdMap = new HashMap<String, ModuleIdentifier>();//namespace->ModuleIdentifier map
	
	//OD SchemaContext is storing all the Yang schema statements in a tree of objects 
	private SchemaContext yangSchemaContext1 = null;
	
	
	//set by onSelected event & watched by FilePresenter
	private IntegerProperty sourceIndexProperty = new SimpleIntegerProperty(0);
	public IntegerProperty getSourceIndexProperty() {
		return sourceIndexProperty;
	}
	
	//set by AugmentationPresenter & watched by this to update selection
	private StringProperty linkProperty = new SimpleStringProperty();
	public StringProperty getLinkProperty() {
		return linkProperty;
	}
	
	//set of inactive features; updated globally by all module dialogs on featureCheck action
	private Set<QName> disabledFeatures = new HashSet<QName>();
	
	//client capabilities; all the module namespaces defined in our local store; must by updated by this
	private final ObservableList<CapabilityItem> clientCapObsList = FXCollections.observableArrayList();
	public ObservableList<CapabilityItem> clientCapObsList() {
		return clientCapObsList;
	}
	
	//from net.juniper.netconf.Device class
	private List<CapabilityItem> getDefaultClientCapabilities() {
        List<CapabilityItem> defaultCap = new ArrayList<CapabilityItem>();
        defaultCap.add(new CapabilityItem("urn:ietf:params:netconf:base:1.0", null));
        defaultCap.add(new CapabilityItem("urn:ietf:params:netconf:base:1.0#candidate", null));
        defaultCap.add(new CapabilityItem("urn:ietf:params:netconf:base:1.0#confirmed-commit", null));
        defaultCap.add(new CapabilityItem("urn:ietf:params:netconf:base:1.0#validate", null));
        defaultCap.add(new CapabilityItem("urn:ietf:params:netconf:base:1.0#url?protocol=http,ftp,file", null));
        return defaultCap;
    }


	public static Date defaultDate = new Date(70, 0, 1);//Default Date returned by OD when revision is missing from yang file
	

	@PostConstruct
	private void init() throws Exception{
		clientCapObsList.addAll( getDefaultClientCapabilities() );
	}

	
	private YangFile getModuleSourceFile(String moduleName, List<YangFile> yangFiles) {
		for(YangFile yangFile : yangFiles){
			if(moduleName.equals(yangFile.getModuleName())) {
				return yangFile;
			}
		}
		return null;//missing yang imports
	}
	
	
	//adds to yangDepList all the imported modules of yangFile
	private void updateImportsRec(YangFile yangFile, List<YangFile> yangFiles, List<YangFile> yangImports) {
		if(yangFile == null)
			return;
		List<String> moduleImports = yangFile.getModuleImports();
		for(String moduleImport : moduleImports) {
			YangFile yangImport = getModuleSourceFile(moduleImport, yangFiles);
			yangImports.add(yangImport);
			updateImportsRec(yangImport, yangFiles, yangImports);
		}
	}
	
	
	public TreeItem<SchemaItem> getSchemaTreeRoot(String filterType, String filterText, boolean isAscending, boolean isCategories){//called from UI
		LOG.debug("Validating schema");
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();//they can come either from Schema errors or from Exceptions
		TreeItem<SchemaItem> rootTreeItem = new TreeItem<SchemaItem> ();
		
		try {
			//make sure you also initialize rootTreeItem from SchemaPresenter
			YangRepo repo = fileService.getYangRepo();
			SchemaItem rootItem = new SchemaItem(repo, 0);
			//TreeItem<SchemaItem> rootTreeItem = new TreeItem<SchemaItem> (rootItem, rootItem.getItemIcon());
			rootTreeItem.setValue(rootItem);
			rootTreeItem.setGraphic(rootItem.getItemIcon());
			
			List<YangFile> yangFiles = fileService.getFileObjects();//each file contains: name, size, path, moduleName, modulePrefix, moduleNamespace, moduleRevision, importedFileList
			
			//sort file list
			if(filterType.equals("file")) {
				if(isAscending)
					Collections.sort(yangFiles, Comparator.comparing(YangFile::getName));
				else
					Collections.sort(yangFiles, Comparator.comparing(YangFile::getName).reversed());
			}else {//"module"
				if(isAscending)
					Collections.sort(yangFiles, Comparator.comparing(YangFile::getModuleName));
				else
					Collections.sort(yangFiles, Comparator.comparing(YangFile::getModuleName).reversed());
			}
			
			List<CapabilityItem> capabilitiesList = new ArrayList<CapabilityItem>();
			
			int fileIndex = 0;
			for(YangFile yangFile : yangFiles){
				SchemaItem fileItem = null;
				try {
						fileItem = new SchemaItem(yangFile, fileIndex); 
						fileIndex++;
						
						//filter file list
						if(filterType.equals("file")) {
							if(filterText != null && filterText.trim().length() > 0) {
								if(!yangFile.getName().toUpperCase().startsWith(filterText.toUpperCase())) 
									continue;
							}
						}else {
							if(filterText != null && filterText.trim().length() > 0) {
								if(!yangFile.getModuleName().toUpperCase().startsWith(filterText.toUpperCase())) 
									continue;
							}
						}
						
						//build yangFile tree item
						TreeItem<SchemaItem> fileTreeItem = new TreeItem<SchemaItem> (fileItem, fileItem.getItemIcon()); 
						fileTreeItem.setExpanded(true);
						rootTreeItem.getChildren().add(fileTreeItem);
						
						//validate fileContent is present
						if(yangFile.getContent() == null || yangFile.getContent().trim().length() == 0) {
							fileItem.setStatusEnum(SchemaValidationStatus.ERR_NO_FILE_CONTENT);
							fileItem.setErrorLineNumber(0);
							String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_FILE_CONTENT.text() + " [at null:0:0]";
							LOG.error(errorMsg);
							errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
					        continue;
						}
						
						//validate moduleName is unique !!!
						//validate moduleName is present
						if(yangFile.getModuleName() == null) {
							fileItem.setStatusEnum(SchemaValidationStatus.ERR_NO_MODULE);
							fileItem.setErrorLineNumber(0);
							String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_MODULE.text() + " [at null:0:0]";
							LOG.error(errorMsg);
							errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				        	continue;
						}
						//validate moduleNamespace is present
						if(yangFile.getModuleNamespace() == null){
							fileItem.setStatusEnum(SchemaValidationStatus.ERR_NO_NAMESPACE);
							fileItem.setErrorLineNumber(0);
							String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_NAMESPACE.text() + " [at null:0:0]";
							LOG.error(errorMsg);
							errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				        	continue;
						}
						//validate moduleRevision is present
						if(yangFile.getModuleRevision() == null){
							fileItem.setStatusEnum(SchemaValidationStatus.ERR_NO_MOD_REVISION);
							fileItem.setErrorLineNumber(0);
							String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_MOD_REVISION.text() + " [at null:0:0]";
							LOG.error(errorMsg);
							errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				        	continue;
						}
					
						//build list of recursive imports
						List<YangFile> yangImports = new ArrayList<YangFile>();
						updateImportsRec(yangFile, yangFiles, yangImports);
						yangImports = yangImports.stream().distinct().collect(Collectors.toList());//filter duplicates
						
						//load schema from yangFile and its recursive imports
						final Collection<StatementStreamSource> yangSources = new ArrayList<StatementStreamSource>(yangImports.size());
						final StatementStreamSource yangFileSource = new YangStatementSourceImpl(new ByteArrayInputStream(yangFile.getContent().getBytes()));
						yangSources.add(yangFileSource);
						for(YangFile yangImport : yangImports){
							if(yangImport == null) {
								fileItem.setStatusEnum(SchemaValidationStatus.ERR_NO_MOD_IMPORT);
								fileItem.setErrorLineNumber(0);
								String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_MOD_IMPORT.text() + " [at null:0:0]";
								LOG.error(errorMsg);
								errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
								continue;//ignore missing yang imports
							}
							final StatementStreamSource yangImportSource = new YangStatementSourceImpl(new ByteArrayInputStream(yangImport.getContent().getBytes()));
							yangSources.add(yangImportSource);
						}
						
						Set<QName> supportedFeatures = yangFile.getModuleFeatures();//get supported features for this module only////////////////////////////////////////////////////////////////////////
						//update view model of the YangModule
						List<FeatureItem> featureItemList = new ArrayList<FeatureItem>();
						for(QName feature : supportedFeatures) {
							if(disabledFeatures.contains(feature)) 
								featureItemList.add(new FeatureItem(feature, false));
							else 
								featureItemList.add(new FeatureItem(feature, true));
						}
						//update features set of OD reactor
						for(QName disabledFeature : disabledFeatures) {
							if(supportedFeatures.contains(disabledFeature)) 
								supportedFeatures.remove(disabledFeature);
							else 
								supportedFeatures.add(disabledFeature);
						}
						CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR.newBuild(supportedFeatures);//////////////////////////////////////////////////////////////
						reactor.addSources(yangSources);
						SchemaContext schemaContext = null;
						schemaContext = reactor.buildEffective();
						
						
						//build only OD module defined in yangFile
						Set<Module> modulesOD = schemaContext.getModules();
						
						//fill in the prefix map for current yang file(all the prefixes of all the modules loaded by this import hierarchy
						for(Module moduleOD : modulesOD) 
							moduleIdMap.put(moduleOD.getNamespace().toString(), new ModuleIdentifier(moduleOD.getName(), moduleOD.getNamespace().toString(), YangNode.formatDate(moduleOD.getQNameModule().getRevision()), moduleOD.getPrefix()));
						
						Set<Module> moduleODSet = schemaContext.findModuleByNamespace(new URI(yangFile.getModuleNamespace()));
						if(moduleODSet.isEmpty()) {
							String errorMsg = "Module namespace " + yangFile.getModuleNamespace() +" not found in schema";
							LOG.error(errorMsg);
							errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				        	continue;
						}
						
						Module moduleOD = moduleODSet.iterator().next();
						
						boolean isDataContainer = moduleOD.getChildNodes().size() > 0;//yang module that defines other things than yang types
						YangModule yangModule = new YangModule( moduleOD.getName(), moduleOD.getNamespace().toString(), moduleOD.getPrefix(), YangNode.formatDate(moduleOD.getQNameModule().getRevision()), 
								moduleOD.getOrganization(), moduleOD.getContact(), moduleOD.getDescription(), isDataContainer, moduleOD.getImports());
						ModuleIdentifier moduleId = yangModule.getModuleIdentifier();
						    
						//update features for current module
						yangModule.setFeatureItemList(featureItemList);
						    
						//update client capabilities with this module
						capabilitiesList.add(new CapabilityItem(moduleId.getNamespace(), "?module=" + moduleId.getName() + "&revision=" + moduleId.getRevision()));
						    
						//update module non data children
						populateModuleNonData(moduleOD, yangModule, moduleId, isCategories);
						    
						//add module items
						YangDataHeader headerNode = null;
						Collection<DataSchemaNode> rootNodesOD = moduleOD.getChildNodes();
						if(rootNodesOD != null && rootNodesOD.size() > 0 && isCategories) {
							headerNode = new YangDataHeader(moduleId);
							yangModule.getChildren().add(headerNode);
						}
						for (DataSchemaNode rootNodeOD : rootNodesOD) {
							YangSchemaNode rootYangNode = buildTreeRec(rootNodeOD, moduleId, isCategories);
							if(headerNode != null)
								headerNode.getChildren().add(rootYangNode);
							else
								yangModule.getChildren().add(rootYangNode);
								
						}
						//add module to yangFile
						SchemaItem moduleItem = new SchemaItem(yangModule, 0); 
						TreeItem<SchemaItem> moduleTreeItem = new TreeItem<SchemaItem> (moduleItem, moduleItem.getItemIcon());
						moduleTreeItem.setExpanded(false);
						fileTreeItem.getChildren().add(moduleTreeItem);
								    		
						//add module tree items by parsing items
						List<YangSchemaNode> rootNodeList = yangModule.getChildren();
						int rootIndex =0;
						for (YangSchemaNode rootNode : rootNodeList) {
							TreeItem<SchemaItem> rootNodeTreeItem = buildGraphicTreeRec(rootNode, rootIndex);
							moduleTreeItem.getChildren().add(rootNodeTreeItem);
							rootIndex++;
						}
			        }catch(Exception ex) {
			        	//extract line number and message
			        	int errorLineNumber = 0;
			        	String errorMsg = "";
			        	if(ex.getCause() instanceof SourceException ) {
			        		SourceException re = (SourceException) ex.getCause();
			        		if(re.getCause() != null)
			        			re = (SourceException)re.getCause();
			        		errorMsg = "SourceException in File: " + yangFile.getName() + " Message: " + re.getMessage();
			        		errorLineNumber = regexService.getErrorLineNumber1(errorMsg);
			        		
			        	}else if(ex instanceof SomeModifiersUnresolvedException) {
			        		SomeModifiersUnresolvedException re = (SomeModifiersUnresolvedException) ex;
			        		if(re.getCause() instanceof IllegalArgumentException) {
			        			IllegalArgumentException se = (IllegalArgumentException) re.getCause();
					        	errorMsg = "IllegalArgumentException in File: " + yangFile.getName() + " Message: " + se.getMessage();
					        	errorLineNumber = regexService.getErrorLineNumber1(errorMsg);
			        		}else if(re.getCause() instanceof SourceException) {
			        			SourceException se = (SourceException) re.getCause();
					        	errorMsg = "SomeModifiersUnresolvedException in File: " + yangFile.getName() + " Message: " + se.getMessage();
			        		}
				        	
			        	}else if(ex.getCause() instanceof ReactorException ) {
			        		ReactorException re = (ReactorException) ex.getCause();
				        	SourceException se = (SourceException) re.getCause();
				        	errorMsg = "ReactorException in File: " + yangFile.getName() + " Message: " + se.getMessage();
				        	
			        	}else if(ex.getCause() instanceof YangSyntaxErrorException) {
			        		YangSyntaxErrorException se = (YangSyntaxErrorException) ex.getCause();
			        		
			        		if(se.getMessage().contains(System.lineSeparator())){
			        			errorMsg = "YangSyntaxErrorException in File: " + yangFile.getName() + " Message: " + se.getMessage().substring(0, se.getMessage().indexOf(System.lineSeparator()));
			        			errorLineNumber = regexService.getErrorLineNumber2(errorMsg);
			        		}else {
			        			errorMsg = "YangSyntaxErrorException in File: " + yangFile.getName() + " Message: " + se.getMessage();
			        			errorLineNumber = se.getLine();
			        		}
			        		
			        	}else {
			        		errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + ex.getMessage();
			        	}
			        	//set error status on SchemaItem
			        	fileItem.setStatusEnum(SchemaValidationStatus.ERR_YANG);
			        	//set error line number to be used by FilePresenter to navigate to line
			        	fileItem.setErrorLineNumber(errorLineNumber);
			        	LOG.error("", ex);
						errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), errorLineNumber+"", errorMsg));
			        	continue;
					}
			}//end for each file
			
			//update & sort client capabilities
			capabilitiesList.sort(Comparator.comparing(CapabilityItem::getNamespace));
			Platform.runLater(() -> {
			    clientCapObsList.clear();
				clientCapObsList.addAll(capabilitiesList);
			});
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.clear();
			errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, "N/A", "0", "Check Error Window"));
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateSchemaErrors(errorList);
				LOG.error("Schema is invalid");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearSchemaErrors();
				LOG.debug("Schema is valid");
			}
		});
		
		return rootTreeItem;//merge this with SchemaPresent.rootTreeItem
	}
	
	
	public void updateFeatures(List<FeatureItem> featureItemList) {
		//on ModulePresente.UpdateFeatures
		for(FeatureItem featureItem : featureItemList) {
			if(featureItem.isEnabled()) {
				//remove it from  disabledFeatures
				if(disabledFeatures.contains(featureItem.getFeature()))
					disabledFeatures.remove(featureItem.getFeature());
			}else {
				//add it to  disabledFeatures
				if(!disabledFeatures.contains(featureItem.getFeature()))
					disabledFeatures.add(featureItem.getFeature());
			}
		}
	}
	
	
	private void populateModuleNonData(Module moduleOD, YangModule yangModule, ModuleIdentifier moduleId, boolean isCategories) {
		//SemVer semVersion = moduleOD.getSemanticVersion();//add support for Semantic Version???
	    //build module extension subtrees 
	    if(moduleOD.getExtensionSchemaNodes() != null && moduleOD.getExtensionSchemaNodes().size() > 0) {
	    	if(isCategories) {
	    		YangExtensionHeader headerNode = new YangExtensionHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getExtensionSchemaNodes(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getExtensionSchemaNodes(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module features subtrees 
	    if(moduleOD.getFeatures() != null && moduleOD.getFeatures().size() > 0) {//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
		    if(isCategories) {
		    	YangFeatureHeader headerNode = new YangFeatureHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getFeatures(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getFeatures(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module identities subtrees 
	    if(moduleOD.getIdentities() != null && moduleOD.getIdentities().size() > 0) {
		    if(isCategories) {
		    	YangIdentityHeader headerNode = new YangIdentityHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getIdentities(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getIdentities(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module notifications subtrees 
	    if(moduleOD.getNotifications() != null && moduleOD.getNotifications().size() > 0) {
		    if(isCategories) {
		    	YangNotificationHeader headerNode = new YangNotificationHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getNotifications(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getNotifications(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module rpcs subtrees 
	    if(moduleOD.getRpcs() != null && moduleOD.getRpcs().size() > 0) {
		    if(isCategories) {
		    	YangRpcHeader headerNode = new YangRpcHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getRpcs(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getRpcs(), yangModule, moduleId, isCategories);
	    	}
	    }
		//build module augmentation subtrees 
	    if(moduleOD.getAugmentations() != null && moduleOD.getAugmentations().size() > 0) {
		    if(isCategories) {
		    	YangAugmentationHeader headerNode = new YangAugmentationHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getAugmentations(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getAugmentations(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module groupings subtrees 
	    if(moduleOD.getGroupings() != null && moduleOD.getGroupings().size() > 0) {
		    if(isCategories) {
		    	YangGroupingHeader headerNode = new YangGroupingHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getGroupings(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getGroupings(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module typedefs subtrees 
	    if(moduleOD.getTypeDefinitions() != null && moduleOD.getTypeDefinitions().size() > 0) {
		    if(isCategories) {
		    	YangTypeHeader headerNode = new YangTypeHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getTypeDefinitions(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getTypeDefinitions(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module typedefs subtrees 
	    if(moduleOD.getDeviations() != null && moduleOD.getDeviations().size() > 0) {
		    if(isCategories) {
		    	YangDeviationHeader headerNode = new YangDeviationHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getDeviations(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getDeviations(), yangModule, moduleId, isCategories);
	    	}
	    } 	
	}
	
	
	private void addChildren(Collection children, YangSchemaNode yangNode, ModuleIdentifier moduleId, boolean isCategories) {
		for (Object child : children) {
			YangSchemaNode yangChild = buildTreeRec(child, moduleId, isCategories);
			yangNode.add(yangChild);
		}
	}
	
	
	private void addChildren(Collection children, YangModule yangModule, ModuleIdentifier moduleId, boolean isCategories) {
		for (Object child : children) {
			YangSchemaNode yangChild = buildTreeRec(child, moduleId, isCategories);
			yangModule.getChildren().add(yangChild);
		}
	}
	

	private YangSchemaNode buildTreeRec(Object nodeOD, ModuleIdentifier moduleId, boolean isCategories) {
		
		YangSchemaNode yangNode = buildNode(nodeOD, moduleId);
		
		if(nodeOD instanceof ChoiceSchemaNode) {//implements both DataNodeContainer & DataSchemaNode
			addChildren(((ChoiceSchemaNode)nodeOD).getCases(), yangNode, moduleId, isCategories);
		}else if(nodeOD instanceof Deviation) {
			addChildren(((Deviation)nodeOD).getDeviates(), yangNode, moduleId, isCategories);
		}else if(nodeOD instanceof UsesNode) {//adds Augmentations on Uses
			if(((UsesNode)nodeOD).getAugmentations() != null && ((UsesNode)nodeOD).getAugmentations().size() > 0) {
				if(isCategories) {
					YangAugmentationHeader headerNode = new YangAugmentationHeader(moduleId);
					yangNode.getChildren().add(headerNode);
				    addChildren(((UsesNode)nodeOD).getAugmentations(), headerNode, moduleId, isCategories);
			    }else {
			    	addChildren(((UsesNode)nodeOD).getAugmentations(), yangNode, moduleId, isCategories);
			    }
			}
		}else if(nodeOD instanceof RpcDefinition) {//RpcDefinition 
			if(((RpcDefinition)nodeOD).getTypeDefinitions() != null && ((RpcDefinition)nodeOD).getTypeDefinitions().size() > 0) {
				if(isCategories) {
					YangTypeHeader headerNode = new YangTypeHeader(moduleId);
				    yangNode.getChildren().add(headerNode);
				    addChildren(((RpcDefinition)nodeOD).getTypeDefinitions(), headerNode, moduleId, isCategories);
			    }else {
			    	addChildren(((RpcDefinition)nodeOD).getTypeDefinitions(), yangNode, moduleId, isCategories);
			    }
			}
			if(((RpcDefinition)nodeOD).getGroupings() != null && ((RpcDefinition)nodeOD).getGroupings().size() > 0) {
				if(isCategories) {
				    YangGroupingHeader headerNode = new YangGroupingHeader(moduleId);
				    yangNode.getChildren().add(headerNode);
				    addChildren(((RpcDefinition)nodeOD).getGroupings(), headerNode, moduleId, isCategories);
			    }else {
			    	addChildren(((RpcDefinition)nodeOD).getGroupings(), yangNode, moduleId, isCategories);
			    }
			}
			
//			if(((RpcDefinition) nodeOD).getQName().getLocalName().equals("get")){
//				LOG.debug("Asa");
//			}
			
			ContainerSchemaNode  inputOD = ((RpcDefinition)nodeOD).getInput();
			YangSchemaNode yangChild = buildTreeRec(inputOD, moduleId, isCategories);
			yangNode.getChildren().add(yangChild);
			ContainerSchemaNode  outputOD = ((RpcDefinition)nodeOD).getOutput();
			yangChild = buildTreeRec(outputOD, moduleId, isCategories);
			yangNode.getChildren().add(yangChild);
		}
		
		if(nodeOD instanceof AugmentationTarget) {
			if(((AugmentationTarget)nodeOD).getAvailableAugmentations() != null && ((AugmentationTarget)nodeOD).getAvailableAugmentations().size() > 0) {
			    if(isCategories) {
			    	YangAugmentationHeader headerNode = new YangAugmentationHeader(moduleId);
			    	yangNode.getChildren().add(headerNode);
			    	addChildren(((AugmentationTarget)nodeOD).getAvailableAugmentations(), headerNode, moduleId, isCategories);
		    	}else {
		    		addChildren(((AugmentationTarget)nodeOD).getAvailableAugmentations(), yangNode, moduleId, isCategories);
		    	}
			}
		}
		
		if(nodeOD instanceof DataNodeContainer) {
			addChildren(((DataNodeContainer)nodeOD).getUses(), yangNode, moduleId, isCategories);//No Header
			if(((DataNodeContainer)nodeOD).getTypeDefinitions() != null && ((DataNodeContainer)nodeOD).getTypeDefinitions().size() > 0) {
			    if(isCategories) {
			    	YangTypeHeader headerNode = new YangTypeHeader(moduleId);
			    	yangNode.getChildren().add(headerNode);
			    	addChildren(((DataNodeContainer)nodeOD).getTypeDefinitions(), headerNode, moduleId, isCategories);
		    	}else {
		    		addChildren(((DataNodeContainer)nodeOD).getTypeDefinitions(), yangNode, moduleId, isCategories);
		    	}
			}
			if(((DataNodeContainer)nodeOD).getGroupings() != null && ((DataNodeContainer)nodeOD).getGroupings().size() > 0) {
				if(isCategories) {
				    YangGroupingHeader headerNode = new YangGroupingHeader(moduleId);
				    yangNode.getChildren().add(headerNode);
					addChildren(((DataNodeContainer)nodeOD).getGroupings(), headerNode, moduleId, isCategories);
			    }else {
			    	addChildren(((DataNodeContainer)nodeOD).getGroupings(), yangNode, moduleId, isCategories);
			    }
			}
			
			addChildren(((DataNodeContainer)nodeOD).getChildNodes(), yangNode, moduleId, isCategories);//No Header
//			Collection<DataSchemaNode> childrenOD = ((DataNodeContainer)nodeOD).getChildNodes();
//			for (DataSchemaNode childOD : childrenOD) {
//				YangSchemaNode yangChild = buildTreeRec(childOD, moduleId);
//				yangNode.add(yangChild);
//			}
		}
		return yangNode;
	}
	
	
	//builds YangSchemaNode from OD SchemaNode
	private YangSchemaNode buildNode(Object nodeOD, ModuleIdentifier moduleId) {
		YangSchemaNode yangNode = null;
		String xpath = null;
		if(nodeOD instanceof SchemaNode) {
			xpath = getXpath(((SchemaNode)nodeOD).getPath(), moduleId);
		}
		//data statements
		if(nodeOD instanceof ContainerSchemaNode) {
			ContainerSchemaNode containerOD = (ContainerSchemaNode)nodeOD;
			yangNode = new YangContainer(containerOD, xpath, moduleId);
		}else if(nodeOD instanceof ChoiceSchemaNode) {
			ChoiceSchemaNode choiceOD = (ChoiceSchemaNode)nodeOD;
			yangNode = new YangChoice(choiceOD, xpath, moduleId);
		}else if(nodeOD instanceof ChoiceCaseNode) {
			ChoiceCaseNode choiceCaseOD = (ChoiceCaseNode)nodeOD;
			yangNode = new YangChoiceCase(choiceCaseOD, xpath, moduleId);
		}else if(nodeOD instanceof LeafSchemaNode) {
			LeafSchemaNode leafOD = (LeafSchemaNode)nodeOD;
			yangNode = new YangLeaf(leafOD, xpath, moduleId);
		}else if(nodeOD instanceof ListSchemaNode) {
			ListSchemaNode listOD = (ListSchemaNode)nodeOD;
			yangNode = new YangList(listOD, xpath, moduleId);
		}else if(nodeOD instanceof LeafListSchemaNode) {
			LeafListSchemaNode leafListOD = (LeafListSchemaNode)nodeOD;
			yangNode = new YangLeafList(leafListOD, xpath, moduleId);
		}else if(nodeOD instanceof AnyXmlSchemaNode) {
			AnyXmlSchemaNode anyOD = (AnyXmlSchemaNode)nodeOD;
			yangNode = new YangAny(anyOD, xpath, moduleId);
		//non data statements	
		}else if(nodeOD instanceof TypeDefinition) {					
			TypeDefinition typeOD = (TypeDefinition)nodeOD;
			yangNode = new YangType(typeOD, xpath, moduleId);
		}else if(nodeOD instanceof IdentitySchemaNode) {
			IdentitySchemaNode identityOD = (IdentitySchemaNode)nodeOD;
			yangNode = new YangIdentity(identityOD, xpath, moduleId);
		}else if(nodeOD instanceof GroupingDefinition) {				
			GroupingDefinition groupOD = (GroupingDefinition)nodeOD;
			yangNode = new YangGrouping(groupOD, xpath, moduleId);
		}else if(nodeOD instanceof NotificationDefinition) {
			NotificationDefinition notifOD = (NotificationDefinition)nodeOD;
			yangNode = new YangNotification(notifOD, xpath, moduleId);
		}else if(nodeOD instanceof RpcDefinition) {
			RpcDefinition rpcOD = (RpcDefinition)nodeOD;
			yangNode = new YangRpc(rpcOD, xpath, moduleId);
		}else if(nodeOD instanceof Deviation) {							
			Deviation deviationOD = (Deviation)nodeOD;
			String targetPath = getTargetPathModule(deviationOD.getTargetPath());
			String targetPathPrefix = getTargetPathPrefix(deviationOD.getTargetPath());
			yangNode = new YangDeviation(deviationOD, targetPath, targetPathPrefix, moduleId);
		}else if(nodeOD instanceof DeviateDefinition) {					
			DeviateDefinition deviateOD = (DeviateDefinition)nodeOD;
			yangNode = new YangDeviate(deviateOD, moduleId);
		}else if(nodeOD instanceof UsesNode) {							
			UsesNode usesOD = (UsesNode)nodeOD;
			String groupingPath = getTargetPathModule(usesOD.getGroupingPath());
			String groupingPathPrefix = getTargetPathPrefix(usesOD.getGroupingPath());
			yangNode = new YangUses(usesOD, groupingPath, groupingPathPrefix, moduleId);
		}else if(nodeOD instanceof AugmentationSchema) {				
			AugmentationSchema augOD = (AugmentationSchema)nodeOD;
			String targetPath = getTargetPathModule(augOD.getTargetPath());
			String targetPathPrefix = getTargetPathPrefix(augOD.getTargetPath());
			yangNode = new YangAugmentation(augOD, targetPath, targetPathPrefix, moduleId);
		}else if(nodeOD instanceof ExtensionDefinition) {
			ExtensionDefinition extOD = (ExtensionDefinition)nodeOD;
			yangNode = new YangExtension(extOD, xpath, moduleId);
		}else if(nodeOD instanceof FeatureDefinition) {
			FeatureDefinition featureOD = (FeatureDefinition)nodeOD;
			boolean isActive = true;
			if(this.disabledFeatures.contains(featureOD.getQName()))
				isActive = false;
			yangNode = new YangFeature(featureOD, xpath, moduleId, isActive);
		}
		
		return yangNode;
	}
	
	
	//builds TreeItem tree for a root node from schema 
	private TreeItem<SchemaItem> buildGraphicTreeRec(YangSchemaNode rootNode, int index) {
		
		SchemaItem yangNodeItem = new SchemaItem(rootNode, index); 
		TreeItem<SchemaItem> rootNodeTreeItem = new TreeItem<SchemaItem> (yangNodeItem, yangNodeItem.getItemIcon());
		
		List<YangSchemaNode> children = rootNode.getChildren();
		int childIndex =0;
		for (YangSchemaNode yangNodeChild : children) {
			rootNodeTreeItem.getChildren().add(buildGraphicTreeRec(yangNodeChild, childIndex));
			childIndex++;
		}
		return rootNodeTreeItem;
	}
	
	
	//builds xpath for tree item and updates regexp pattern -> match -> index -> select textArea line
	public Integer getIndexFileContent(TreeItem<SchemaItem> treeItem, String content){
		Integer index = 0;
		StringBuilder regexBuf = new StringBuilder();
		
		SchemaItem item = treeItem.getValue();
		if(SchemaItemType.FILE == item.getTypeEnum() || SchemaItemType.ROOT_NODE == item.getTypeEnum() || SchemaItemType.MODULE == item.getTypeEnum()) 
			return index;
		
		String localName = item.getSchemaNode().getLocalName().replaceAll("\\-", "\\\\-");
			
		if(SchemaItemType.USES == item.getTypeEnum() || SchemaItemType.HEADER == item.getTypeEnum())
			regexBuf.append("");
		else if(SchemaItemType.AUGMENTATION == item.getTypeEnum() || SchemaItemType.DEVIATION == item.getTypeEnum() || SchemaItemType.DEVIATE == item.getTypeEnum())
			regexBuf.append(item.getTypeEnum().text().toLowerCase() + new String("\\s+?\"??") + "(" + localName + ")");
		else {
			TreeItem<SchemaItem> parentTreeItem = treeItem.getParent();
			if(parentTreeItem != null && SchemaItemType.RPC == parentTreeItem.getValue().getTypeEnum())//remove type from RPC input & output 
				regexBuf.append(new String("\\s+?S*?(") + localName + ")");
			else
				regexBuf.append(item.getTypeEnum().text().toLowerCase() + new String("\\s+?S*?(") + localName + ")");
		}
		
		TreeItem<SchemaItem> parentTreeItem = null;
		while( (parentTreeItem = treeItem.getParent()) != null ) {
			
			SchemaItem parentItem = parentTreeItem.getValue();
			if(SchemaItemType.FILE == parentItem.getTypeEnum() || SchemaItemType.ROOT_NODE == parentItem.getTypeEnum() 
					|| SchemaItemType.MODULE == parentItem.getTypeEnum() || SchemaItemType.HEADER == parentItem.getTypeEnum()) 
				break;
			
			String localNameParent = parentItem.getSchemaNode().getLocalName().replaceAll("\\-", "\\\\-");
			
			if(SchemaItemType.USES == item.getTypeEnum() || SchemaItemType.HEADER == item.getTypeEnum())
				regexBuf.insert(0, parentItem.getTypeEnum().text().toLowerCase() + new String("\\s+?S*?(") + localNameParent + ").+?");
			else if(SchemaItemType.AUGMENTATION == parentItem.getTypeEnum() || SchemaItemType.DEVIATION == parentItem.getTypeEnum())
				regexBuf.insert(0, parentItem.getTypeEnum().text().toLowerCase() + new String("\\s+?") + localNameParent + ".+?");
			else {
				TreeItem<SchemaItem> parentTreeItem1 = parentTreeItem.getParent();
				if(parentTreeItem1 != null && SchemaItemType.RPC == parentTreeItem1.getValue().getTypeEnum())//remove type from RPC input & output 
					regexBuf.insert(0, new String("\\s+?S*?") + localNameParent + ".+?");
				else
					regexBuf.insert(0, parentItem.getTypeEnum().text().toLowerCase() + new String("\\s+?S*?") + localNameParent + ".+?");
			}
			
			treeItem = parentTreeItem;
		}
		regexBuf.insert(0, ".+?");
		
		String regexString = regexBuf.toString();
		Pattern elementPattern = Pattern.compile(regexString, Pattern.DOTALL);
		Matcher matcher = elementPattern.matcher(content);
		String name = null;
		if (matcher.find()) {
			name = matcher.group(matcher.groupCount());
			index = matcher.start(matcher.groupCount());
		}
		return index;
    }

	
	public static final String formatDate(Date inputDate) {
		return new SimpleDateFormat("yyyy-MM-dd").format(inputDate);
	}
	
	
	public String getXpath(String localPath, String namespace) {//validateTreeRec & validateLeafValue -> 	/a/b/c  -> /mod:a/b/c
		StringBuilder xpathBuf = new StringBuilder();
		ModuleIdentifier moduleId = moduleIdMap.get(namespace);
		String moduleName = moduleId.getName();
		xpathBuf.append("/" + moduleName + ":");
		xpathBuf.append(localPath.substring(1));
		return xpathBuf.toString();
	}
	
	
	public String getXpath(SchemaPath  schemaPath, ModuleIdentifier moduleIdentifier) {//buildNode & buildDataNode
		StringBuilder xpathBuf = new StringBuilder();
		xpathBuf.append("/" + moduleIdentifier.getName() + ":");
		Iterable<QName> pathSegments = schemaPath.getPathFromRoot();
		for (QName segment : pathSegments) 
			xpathBuf.append(segment.getLocalName() + "/");
		return xpathBuf.substring(0, xpathBuf.length() -1).toString();
	}
		
	
	public String getTargetPathPrefix(SchemaPath schemaPath) {//deviation, augmentation, uses WRONG !!! use prefixMap from module
		//use prefixMap instead
		StringBuilder xpathBuf = new StringBuilder();
		
		String ns = schemaPath.getLastComponent().getModule().getNamespace().toString();
		ModuleIdentifier moduleId = moduleIdMap.get(ns);
		String prefix = "";
		if(moduleId != null)
			prefix = moduleId.getPrefix();
			
		Iterable<QName> pathSegments = schemaPath.getPathFromRoot();
		for (QName segment : pathSegments) 
			xpathBuf.append("/" + prefix + ":" + segment.getLocalName());
		return xpathBuf.toString();
	}
	
	
	public String getTargetPathModule(SchemaPath schemaPath) {//deviation, augmentation, uses  WRONG !!! use prefixMap from module
		//use prefixMap instead
		StringBuilder xpathBuf = new StringBuilder();
		
		String ns = schemaPath.getLastComponent().getModule().getNamespace().toString();
		ModuleIdentifier moduleId = moduleIdMap.get(ns);
		String moduleName = "";
		if(moduleId != null)
			moduleName = moduleId.getName();
			
		xpathBuf.append("/" + moduleName + ":");
		Iterable<QName> pathSegments = schemaPath.getPathFromRoot();
		for (QName segment : pathSegments) 
			xpathBuf.append(segment.getLocalName() + "/");
		return xpathBuf.substring(0, xpathBuf.length() -1).toString();
	}
	
	
	public String getXpath(RevisionAwareXPath targetXPath, Map<String, String> prefixMap) {//buildValue & validateLeafValue
		String prefix = getPrefixFrom(targetXPath);
		String moduleName = prefixMap.get(prefix);
		
		String simplePath = getSimplePathFrom(targetXPath);
		StringBuilder xpathBuf = new StringBuilder();
		xpathBuf.append("/" + moduleName + ":");
		List<String> stepList = getTokens(simplePath, "/");
		for(String step:stepList) {
			xpathBuf.append(step + "/");
		}
		return xpathBuf.substring(0, xpathBuf.length() - 1).toString();
	}
	
	
	private String getSimplePathFrom(RevisionAwareXPath targetXPath) {
		StringBuilder pathBuf = new StringBuilder();
		List<String> stepList = getTokens(targetXPath.toString(), "/");
		for(String step:stepList) {
			if(step.contains(":"))
				pathBuf.append("/" + step.substring(step.indexOf(":") + 1, step.length()));
		}
		String path = pathBuf.toString();
		return path;
	}
	
	
	private  List<String> getTokens(String str, String delim) {
		return Collections.list(new StringTokenizer(str, delim)).stream().map(token -> (String) token).collect(Collectors.toList());
    }	
	
	
	private String getPrefixFrom(RevisionAwareXPath targetXPath) {
		String prefix = "";
		try {
			String targetPath = targetXPath.toString();
			StringBuilder prefixBuf = new StringBuilder();
			
			int slashIndex = targetPath.lastIndexOf("/");
			int sepIndex = targetPath.lastIndexOf(":");
			prefix = targetPath.substring(slashIndex+1, sepIndex);
		}catch(Exception ex) {
			prefix = "";
		}
		return prefix;
	}
	
	
	public static String getUniques(Collection<UniqueConstraint> uniquesSet) {
		StringBuilder mustBuf = new StringBuilder();
			
		if(uniquesSet != null) {
			for(UniqueConstraint uniqueDef : uniquesSet) {
				Collection<Relative> relTags = uniqueDef.getTag();
				for(Relative relTag : relTags) {
					SchemaPath path = relTag.asSchemaPath();
					mustBuf.append(path.toString() + "	");
				}
				mustBuf.append(File.separator);
			}
		}
		return mustBuf.toString();
	}
	 
	
	public static String getMust(Set<MustDefinition> mustSet) {
		StringBuilder mustBuf = new StringBuilder();
		
		if(mustSet != null) {
			for(MustDefinition mustDef : mustSet) {
				RevisionAwareXPath mustXpath = mustDef.getXpath();
				mustBuf.append(mustXpath.toString() + File.separator);
			}
		}
		return mustBuf.toString();
	}
	
	
	//return the TreeItem which satisfies predicate.test(SchemaItem)==true
	public TreeItem<SchemaItem> searchTreeItemRec(TreeItem<SchemaItem> treeItem, Predicate<SchemaItem> predicate) {
				
		SchemaItem item = treeItem.getValue();
		if(item != null && predicate.test(item)) {
			return treeItem;
		}
		for(TreeItem<SchemaItem> treeItemChild : treeItem.getChildren()){
			TreeItem<SchemaItem> foundItem = searchTreeItemRec(treeItemChild, predicate);
			if(foundItem != null)
				return foundItem;
		}
		return null;
	}
	
	
	public TreeItem<SchemaItem> getModuleTreeItem(TreeItem<SchemaItem> selTreeItem) {
		
		TreeItem<SchemaItem> parentTreeItem = null;
		while((parentTreeItem = selTreeItem.getParent()) != null) {
			SchemaItem parentItem = parentTreeItem.getValue();
			SchemaItemType parentType = parentItem.getTypeEnum();
			if(parentType == SchemaItemType.MODULE)
				break;
			selTreeItem = parentTreeItem;
		}
		return parentTreeItem;
	}
	
	//------------------------------------------------------------------------------------------------------------------Validator deals with Data Xpath--------------------------------------------------------------------------
	public TreeItem<SchemaItem> getModulesTreeRoot(){
		TreeItem<SchemaItem> rootTreeItem = null;
		try {
			//make sure you also initialize rootTreeItem from SchemaPresenter
			YangRepo repo = fileService.getYangRepo();
			SchemaItem rootItem = new SchemaItem(repo, 0);
			rootTreeItem = new TreeItem<SchemaItem> (rootItem, rootItem.getItemIcon());
			rootTreeItem.setExpanded(true);
			
			List<YangFile> yangFiles = fileService.getFileObjects();//each file contains: name, size, path, moduleName, modulePrefix, moduleNamespace, moduleRevision, importedFileList
			
			//List<CapabilityItem> capabilitiesList = new ArrayList<CapabilityItem>();
			Collections.sort(yangFiles, Comparator.comparing(YangFile::getName));
			
			int fileIndex = 0;
			for(YangFile yangFile : yangFiles){
				SchemaItem fileItem = null;
				try {
					fileItem = new SchemaItem(yangFile, fileIndex); 
					fileIndex++;
					
					//build list of recursive imports
					List<YangFile> yangImports = new ArrayList<YangFile>();
					updateImportsRec(yangFile, yangFiles, yangImports);
					yangImports = yangImports.stream().distinct().collect(Collectors.toList());//filter duplicates
					
					//load schema from yangFile and its recursive imports
					final Collection<StatementStreamSource> yangSources = new ArrayList<StatementStreamSource>(yangImports.size());
					final StatementStreamSource yangFileSource = new YangStatementSourceImpl(new ByteArrayInputStream(yangFile.getContent().getBytes()));
					yangSources.add(yangFileSource);
					for(YangFile yangImport : yangImports){
						if(yangImport == null) {
							continue;//ignore missing yang imports
						}
						final StatementStreamSource yangImportSource = new YangStatementSourceImpl(new ByteArrayInputStream(yangImport.getContent().getBytes()));
						yangSources.add(yangImportSource);
					}
					
					Set<QName> supportedFeatures = yangFile.getModuleFeatures();//get supported features for this module only////////////////////////////////////////////////////////////////////////
					//update features set of OD reactor
					for(QName disabledFeature : disabledFeatures) {
						if(supportedFeatures.contains(disabledFeature)) 
							supportedFeatures.remove(disabledFeature);
						else 
							supportedFeatures.add(disabledFeature);
					}
					CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR.newBuild(supportedFeatures);//////////////////////////////////////////////////////////////
					reactor.addSources(yangSources);
					SchemaContext schemaContext = null;
					schemaContext = reactor.buildEffective();
					
					//build only OD module defined in yangFile
					Set<Module> modulesOD = schemaContext.getModules();
					
//					//fill in the prefix map for current yang file(all the prefixes of all the modules loaded by this import hierarchy
//					for(Module moduleOD : modulesOD) 
//						moduleIdMap.put(moduleOD.getNamespace().toString(), new ModuleIdentifier(moduleOD.getName(), moduleOD.getNamespace().toString(), YangNode.formatDate(moduleOD.getQNameModule().getRevision()), moduleOD.getPrefix()));
					
					//build yang module
					Set<Module> moduleODSet = schemaContext.findModuleByNamespace(new URI(yangFile.getModuleNamespace()));
					if(moduleODSet.isEmpty())
						throw new Exception("Module namespace " + yangFile.getModuleNamespace() +" not found in schema");
					Module moduleOD = moduleODSet.iterator().next();
					
					if((moduleOD.getChildNodes() == null || moduleOD.getChildNodes().size() == 0)  && (moduleOD.getIdentities() == null || moduleOD.getIdentities().size() == 0)
							&& (moduleOD.getRpcs() == null || moduleOD.getRpcs().size() == 0)
							&& (moduleOD.getNotifications() == null || moduleOD.getNotifications().size() == 0))//ignore module without data statements
						continue;
					
					
					//build yangFile tree item
					TreeItem<SchemaItem> fileTreeItem = new TreeItem<SchemaItem> (fileItem, fileItem.getItemIcon()); 
					fileTreeItem.setExpanded(true);
					rootTreeItem.getChildren().add(fileTreeItem);
					
					boolean isDataContainer = moduleOD.getChildNodes().size() > 0;//yang module that defines other things than yang types
					YangModule yangModule = new YangModule( moduleOD.getName(), moduleOD.getNamespace().toString(), moduleOD.getPrefix(), YangNode.formatDate(moduleOD.getQNameModule().getRevision()), 
							moduleOD.getOrganization(), moduleOD.getContact(), moduleOD.getDescription(), isDataContainer, moduleOD.getImports());
					ModuleIdentifier moduleId = yangModule.getModuleIdentifier();
					    
					populateModuleData(moduleOD, yangModule, moduleId, false, schemaContext);
					    
					//add module items
					Collection<DataSchemaNode> rootNodesOD = moduleOD.getChildNodes();
					for (DataSchemaNode rootNodeOD : rootNodesOD) {
						YangSchemaNode rootYangNode = buildTreeDataRec(rootNodeOD, moduleId, false, schemaContext);
						yangModule.getChildren().add(rootYangNode);
							
					}
					//add module to yangFile
					SchemaItem moduleItem = new SchemaItem(yangModule, 0); 
					TreeItem<SchemaItem> moduleTreeItem = new TreeItem<SchemaItem> (moduleItem, moduleItem.getItemIcon());
					moduleTreeItem.setExpanded(false);
					fileTreeItem.getChildren().add(moduleTreeItem);
							    		
					//add module tree items by parsing items
					List<YangSchemaNode> rootNodeList = yangModule.getChildren();
					int rootIndex =0;
					for (YangSchemaNode rootNode : rootNodeList) {
						TreeItem<SchemaItem> rootNodeTreeItem = buildGraphicTreeRec(rootNode, rootIndex);
						moduleTreeItem.getChildren().add(rootNodeTreeItem);
						rootIndex++;
					}
		        }catch(Exception ex) {
		        	ex.printStackTrace();
		        	LOG.error("",ex);
		        	continue;
				}
			}//end for each file
		
		}catch(Exception ex) {
        	ex.printStackTrace();
        	LOG.error("", ex);
		}
		return rootTreeItem;//merge this with SchemaPresent.rootTreeItem
	}
	
	
	private YangSchemaNode buildTreeDataRec(Object nodeOD, ModuleIdentifier moduleId, boolean isCategories, SchemaContext schemaContext) {
		
		YangSchemaNode yangNode = buildDataNode(nodeOD, moduleId, schemaContext);
		
		if(nodeOD instanceof ChoiceSchemaNode) {//implements both DataNodeContainer & DataSchemaNode
			addChildrenData(((ChoiceSchemaNode)nodeOD).getCases(), yangNode, moduleId, isCategories, schemaContext);
		} 
		if(nodeOD instanceof RpcDefinition) {//RpcDefinition 
			ContainerSchemaNode  inputOD = ((RpcDefinition)nodeOD).getInput();
			YangSchemaNode yangChild = buildTreeDataRec(inputOD, moduleId, isCategories, schemaContext);
			yangNode.getChildren().add(yangChild);
			
			//((YangRpc)yangNode).setInput(yangChild);//set input
			
			ContainerSchemaNode  outputOD = ((RpcDefinition)nodeOD).getOutput();
			yangChild = buildTreeDataRec(outputOD, moduleId, isCategories, schemaContext);
			yangNode.getChildren().add(yangChild);
			
			//((YangRpc)yangNode).setOutput(yangChild);//set output
		}
		
		if(nodeOD instanceof DataNodeContainer) {
			addChildrenData(((DataNodeContainer)nodeOD).getUses(), yangNode, moduleId, isCategories, schemaContext);//No Header
			addChildrenData(((DataNodeContainer)nodeOD).getChildNodes(), yangNode, moduleId, isCategories, schemaContext);//No Header
		}
		return yangNode;
	}
	
	
	private void addChildrenData(Collection children, YangSchemaNode yangNode, ModuleIdentifier moduleId, boolean isCategories, SchemaContext schemaContext) {
		for (Object child : children) {
			YangSchemaNode yangChild = buildTreeDataRec(child, moduleId, isCategories, schemaContext);
			yangNode.add(yangChild);
		}
	}
	
	private void addChildrenData(Collection children, YangModule yangModule, ModuleIdentifier moduleId, boolean isCategories, SchemaContext schemaContext) {
		for (Object child : children) {
			YangSchemaNode yangChild = buildTreeDataRec(child, moduleId, isCategories, schemaContext);
			yangModule.getChildren().add(yangChild);
		}
	}
	
	
	private void populateModuleData(Module moduleOD, YangModule yangModule, ModuleIdentifier moduleId, boolean isCategories, SchemaContext schemaContext) {
		 //build module identities subtrees 
	    if(moduleOD.getIdentities() != null && moduleOD.getIdentities().size() > 0) {
		    if(isCategories) {
		    	YangIdentityHeader headerNode = new YangIdentityHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildren(moduleOD.getIdentities(), headerNode, moduleId, isCategories);
	    	}else {
			    addChildren(moduleOD.getIdentities(), yangModule, moduleId, isCategories);
	    	}
	    }
	    //build module notifications subtrees 
	    if(moduleOD.getNotifications() != null && moduleOD.getNotifications().size() > 0) {
		    if(isCategories) {
		    	YangNotificationHeader headerNode = new YangNotificationHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildrenData(moduleOD.getNotifications(), headerNode, moduleId, isCategories, schemaContext);
	    	}else {
	    		addChildrenData(moduleOD.getNotifications(), yangModule, moduleId, isCategories, schemaContext);
	    	}
	    }
	    //build module rpcs subtrees 
	    if(moduleOD.getRpcs() != null && moduleOD.getRpcs().size() > 0) {
		    if(isCategories) {
		    	YangRpcHeader headerNode = new YangRpcHeader(moduleId);
			    yangModule.getChildren().add(headerNode);
			    addChildrenData(moduleOD.getRpcs(), headerNode, moduleId, isCategories, schemaContext);
	    	}else {
	    		addChildrenData(moduleOD.getRpcs(), yangModule, moduleId, isCategories, schemaContext);
	    	}
	    }
	}	
	
	
	private YangSchemaNode buildDataNode(Object nodeOD, ModuleIdentifier moduleId, SchemaContext schemaContext) {
		YangSchemaNode yangNode = null;
		StringBuilder xpathBuf = new StringBuilder();
		String xpath = null;
			
		if(nodeOD instanceof SchemaNode) {//transform schema path into data path
			SchemaPath schemaPath = ((SchemaNode)nodeOD).getPath();
				
			if(nodeOD instanceof ChoiceCaseNode || nodeOD instanceof ChoiceSchemaNode)
				xpath = getXpath(schemaPath, moduleId);
			else {
					
				xpathBuf.insert(0, "/" + schemaPath.getLastComponent().getLocalName());
				SchemaPath schemaPathParent = null;
				while( (schemaPathParent = schemaPath.getParent()) != null) {
					SchemaNode schemaNodeParent = SchemaUtils.findParentSchemaOnPath(schemaContext, schemaPathParent);
					if(schemaNodeParent instanceof SchemaContext) 
						break;
					if(schemaNodeParent instanceof ChoiceCaseNode || schemaNodeParent instanceof ChoiceSchemaNode) {
						//ignore some statements in order to build proper data path
					}else
						xpathBuf.insert(0, "/" + schemaNodeParent.getQName().getLocalName());
						
					schemaPath = schemaPathParent;
				}
				xpathBuf.deleteCharAt(0);
				xpathBuf.insert(0, "/" + moduleId.getName() + ":");
				xpath = xpathBuf.toString();
			}
		}
		//LOG.debug("Node Computed " + xpath);
			
		//data statements
		if(nodeOD instanceof ContainerSchemaNode) {
				ContainerSchemaNode containerOD = (ContainerSchemaNode)nodeOD;
				yangNode = new YangContainer(containerOD, xpath, moduleId);
			}else if(nodeOD instanceof ChoiceSchemaNode) {
				ChoiceSchemaNode choiceOD = (ChoiceSchemaNode)nodeOD;
				yangNode = new YangChoice(choiceOD, xpath, moduleId);
			}else if(nodeOD instanceof ChoiceCaseNode) {
				ChoiceCaseNode choiceCaseOD = (ChoiceCaseNode)nodeOD;
				yangNode = new YangChoiceCase(choiceCaseOD, xpath, moduleId);
			}else if(nodeOD instanceof LeafSchemaNode) {
				LeafSchemaNode leafOD = (LeafSchemaNode)nodeOD;
				yangNode = new YangLeaf(leafOD, xpath, moduleId);
			}else if(nodeOD instanceof ListSchemaNode) {
				ListSchemaNode listOD = (ListSchemaNode)nodeOD;
				yangNode = new YangList(listOD, xpath, moduleId);
			}else if(nodeOD instanceof LeafListSchemaNode) {
				LeafListSchemaNode leafListOD = (LeafListSchemaNode)nodeOD;
				yangNode = new YangLeafList(leafListOD, xpath, moduleId);
			}else if(nodeOD instanceof AnyXmlSchemaNode) {
				AnyXmlSchemaNode anyOD = (AnyXmlSchemaNode)nodeOD;
				yangNode = new YangAny(anyOD, xpath, moduleId);
			//non data statements	
			}else if(nodeOD instanceof TypeDefinition) {					
				TypeDefinition typeOD = (TypeDefinition)nodeOD;
				yangNode = new YangType(typeOD, xpath, moduleId);
			}else if(nodeOD instanceof IdentitySchemaNode) {
				IdentitySchemaNode identityOD = (IdentitySchemaNode)nodeOD;
				yangNode = new YangIdentity(identityOD, xpath, moduleId);
			}else if(nodeOD instanceof GroupingDefinition) {				
				GroupingDefinition groupOD = (GroupingDefinition)nodeOD;
				yangNode = new YangGrouping(groupOD, xpath, moduleId);
			}else if(nodeOD instanceof NotificationDefinition) {
				NotificationDefinition notifOD = (NotificationDefinition)nodeOD;
				yangNode = new YangNotification(notifOD, xpath, moduleId);
			}else if(nodeOD instanceof RpcDefinition) {
				RpcDefinition rpcOD = (RpcDefinition)nodeOD;
				yangNode = new YangRpc(rpcOD, xpath, moduleId);
			}else if(nodeOD instanceof Deviation) {							
				Deviation deviationOD = (Deviation)nodeOD;
				String targetPath = getTargetPathModule(deviationOD.getTargetPath());
				String targetPathPrefix = getTargetPathPrefix(deviationOD.getTargetPath());
				yangNode = new YangDeviation(deviationOD, targetPath, targetPathPrefix, moduleId);
			}else if(nodeOD instanceof DeviateDefinition) {					
				DeviateDefinition deviateOD = (DeviateDefinition)nodeOD;
				yangNode = new YangDeviate(deviateOD, moduleId);
			}else if(nodeOD instanceof UsesNode) {							
				UsesNode usesOD = (UsesNode)nodeOD;
				String groupingPath = getTargetPathModule(usesOD.getGroupingPath());
				String groupingPathPrefix = getTargetPathPrefix(usesOD.getGroupingPath());
				yangNode = new YangUses(usesOD, groupingPath, groupingPathPrefix, moduleId);
			}else if(nodeOD instanceof AugmentationSchema) {				
				AugmentationSchema augOD = (AugmentationSchema)nodeOD;
				String targetPath = getTargetPathModule(augOD.getTargetPath());
				String targetPathPrefix = getTargetPathPrefix(augOD.getTargetPath());
				yangNode = new YangAugmentation(augOD, targetPath, targetPathPrefix, moduleId);
			}else if(nodeOD instanceof ExtensionDefinition) {
				ExtensionDefinition extOD = (ExtensionDefinition)nodeOD;
				yangNode = new YangExtension(extOD, xpath, moduleId);
			}else if(nodeOD instanceof FeatureDefinition) {
				FeatureDefinition featureOD = (FeatureDefinition)nodeOD;
				boolean isActive = true;
				if(this.disabledFeatures.contains(featureOD.getQName()))
					isActive = false;
				yangNode = new YangFeature(featureOD, xpath, moduleId, isActive);
			}
			
			return yangNode;
	}
	
}