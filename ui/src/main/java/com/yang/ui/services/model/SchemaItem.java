package com.yang.ui.services.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.yang.ui.services.model.YangFile;
import com.yang.ui.services.model.YangModule;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;



//Model class used to display schema items( root, YangFile, YangModule, SchemaNode ) 
public class SchemaItem extends RecursiveTreeObject<SchemaItem>{
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((typeEnum == null) ? 0 : typeEnum.hashCode());
		result = prime * result + ((queryPath == null) ? 0 : queryPath.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SchemaItem other = (SchemaItem) obj;
		if (typeEnum != other.typeEnum)
			return false;
		
		switch (typeEnum)
	    {
	      case ROOT_NODE:
	      case FILE:
	      case MODULE:
	    	  return (this.toString()).equals(other.toString());
	    	  
	      default:   
	    	  YangSchemaNode thisNode = yangSchemaNode;
	    	  YangSchemaNode otherNode = other.getSchemaNode();
	    	  return thisNode.equals(otherNode);//override toString() in all classes 
	    }
		
	}
	
	@Override
	public String toString() {
		switch (typeEnum)
	    {
	      case ROOT_NODE:
	    	  return 			repo.toString();
	      case FILE:
		  	   return			file.toString();
	      case MODULE:
	    	  return 			module.toString();
//	      case SCHEMA_NODE:  
//	    	  return 			yangSchemaNode.toString();
	      default:        
	    	  return 			yangSchemaNode.toString();//override toString() in all classes 
	    }
	}


	
	public static enum SchemaItemType {
		ROOT_NODE("ROOT_NODE"), FILE("FILE"), MODULE("MODULE"), 
		CONTAINER("CONTAINER"), LIST("LIST"), LEAF_LIST("LEAF-LIST"), LEAF("LEAF"), CHOICE("CHOICE"), CHOICE_CASE("CHOICE_CASE"), ANY_XML("ANYXML"),
		CONSTRAINT("CONSTRAINT"),
		EXTENSION("EXTENSION"), AUGMENTATION("AUGMENT"), DEVIATION("DEVIATION"), DEVIATE("DEVIATE"), FEATURE("FEATURE"), 
		GROUPING("GROUPING"), IDENTITY("IDENTITY"), NOTIFICATION("NOTIFICATION"), TYPEDEF("TYPEDEF"),
		RPC("RPC"), RPC_IN("input"), RPC_OUT("output"),
		USES("USES"),
		HEADER("HEADER"),
		UNKNOWN("UNKNOWN");

		private final String value;

		SchemaItemType(String value) {
			this.value = value;
		}

		public String text() {
			return value;
		}

		public static final SchemaItemType getInstance(String action) {
			for (SchemaItemType actionValue : SchemaItemType.values()) {
				if (actionValue.text().equals(action)) {
					return actionValue;
				}
			}
			return null;
		}
	}
	
	private YangRepo repo;
	
	private YangFile file;
	
	private YangModule module;
	
	//private YangNode schemaNode;//REPLACED OLD SCHEMA
	
	private YangSchemaNode yangSchemaNode;
	
	//Enum holding the schema item visual type
	private SchemaItemType typeEnum;
	
	private Text itemIcon;
	
	//Enum holding the schema validation errors 
	private SchemaValidationStatus statusEnum;
	
	private int errorLineNumber;
	
	private String errorMsg;
	
	private int index;//insertion order in the list of its parent; very important when merging existing tree with new tree at schema refresh
	
	private StringProperty queryPath = new SimpleStringProperty(this, "queryPath");
	public void setQueryPath(String queryPath) { queryPathProperty().set(queryPath); }
    public String getQueryPath() { return queryPathProperty().get(); }
    public StringProperty queryPathProperty() { return queryPath; }
    
	
	public SchemaItem(YangRepo repo, int index) {
		super();
		this.repo = repo;
		this.typeEnum = SchemaItemType.ROOT_NODE;
		this.itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.YIN_YANG);
		this.itemIcon.setFill(Paint.valueOf("#f8e71c"));
		this.statusEnum = SchemaValidationStatus.OK; 
		this.errorLineNumber = 0;
		this.errorMsg = "";
		this.index = index;
		//this.queryPath.set(file.getPath()); this is not needed since files are not selectable in xPath choosers
	}
	
	
	public SchemaItem(YangFile file, int index) {
		super();
		this.file = file;
		this.typeEnum = SchemaItemType.FILE;
		this.itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.FILE_OUTLINE);
		this.itemIcon.setFill(Paint.valueOf("#17bec9"));
		this.statusEnum = SchemaValidationStatus.OK;
		this.errorLineNumber = 0;
		this.errorMsg = "";
		this.index = index;
		//this.queryPath.set(file.getPath()); this is not needed since files are not selectable in xPath choosers
	}
	
	
	public SchemaItem(YangModule module, int index) {
		super();
		this.module = module;
		this.typeEnum = SchemaItemType.MODULE;
		this.itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.MAXCDN);
		this.itemIcon.setFill(Paint.valueOf("#f8e71c"));
		this.statusEnum = SchemaValidationStatus.OK;
		this.queryPath.set(module.getQueryPath());
		this.errorLineNumber = 0;
		this.errorMsg = "";
		this.index = index;
	}

	
	public SchemaItem(YangSchemaNode yangSchemaNode, int index) {
		super();
		this.yangSchemaNode = yangSchemaNode;
		this.typeEnum = yangSchemaNode.getTypeEnum();//CONTAINER, LIST, LEAF_LIST, LEAF, CHOICE, CHOICE_CASE, ANY_XML, UNKNOWN;
		this.statusEnum = SchemaValidationStatus.OK;
		this.queryPath.set(yangSchemaNode.getXpath());
		this.errorLineNumber = 0;
		this.errorMsg = "";
		this.itemIcon = yangSchemaNode.getItemIcon();
		this.index = index;
//		this.itemIcon.setFill(Paint.valueOf("#de79f2"));
	}
	
	
	
//	public SchemaItem(String queryPath) {
//		this.typeEnum = SchemaItemType.SCHEMA_NODE;
//		this.queryPath.set(queryPath);
//		this.errorLineNumber = 0;
//		this.errorMsg = "";
//	}

	public int getIndex() {
		return index;
	}
	
	public Text getItemIcon() {
	    return 	itemIcon;
	}

	public SchemaValidationStatus getStatusEnum() {
		return statusEnum;
	}
	
	public void setStatusEnum(SchemaValidationStatus statusEnum) {
		this.statusEnum = statusEnum;
	}
	
	public SchemaItemType getTypeEnum() {
		return typeEnum;
	}
	
	public void setTypeEnum(SchemaItemType typeEnum) {
		this.typeEnum = typeEnum;
	}
	
	public YangRepo getRepo() {
		return repo;
	}

	public void setRepo(YangRepo repo) {
		this.repo = repo;
	}
	
	public YangFile getFile() {
		return file;
	}

	public void setFile(YangFile file) {
		this.file = file;
	}

	public YangModule getModule() {
		return module;
	}

	public void setModule(YangModule module) {
		this.module = module;
	}

	public int getErrorLineNumber() {
		return errorLineNumber;
	}
	
	public void setErrorLineNumber(int errorLineNumber) {
		this.errorLineNumber = errorLineNumber;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
	public YangSchemaNode getSchemaNode() {
		return yangSchemaNode;
	}
	public void setSchemaNode(YangSchemaNode yangSchemaNode) {
		this.yangSchemaNode = yangSchemaNode;
	}

	
}
