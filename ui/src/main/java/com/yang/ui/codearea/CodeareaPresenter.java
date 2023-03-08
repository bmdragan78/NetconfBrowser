package com.yang.ui.codearea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.jfoenix.controls.JFXButton;
import com.yang.ui.codearea.base.FXComboBox;
import com.yang.ui.codearea.base.MyTextStyle;
import com.yang.ui.codearea.fxmisc.richtext.CodeArea;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class CodeareaPresenter implements Initializable {
	
    @FXML
    JFXButton undoBtn;
   
    @FXML
    JFXButton redoBtn;
   
    @FXML
    JFXButton cutBtn;
   
    @FXML
    JFXButton copyBtn;
   
    @FXML
    JFXButton pasteBtn;
    
    @FXML
    JFXButton formatBtn;
    
    @FXML
    FXComboBox<Integer> sizeCombo;
    
    @FXML
    FXComboBox<String> familyCombo;
    
    @FXML
    ColorPicker paragraphBackgroundPicker;
   
	@FXML
	CodeArea codeArea;
	public CodeArea getCodeArea() {
		return codeArea;
	}

	@FXML
    Label lineNo;
	
	@FXML
    Label colNo;
	
	@FXML
    Label charPos;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	//Toolbar
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

        List<Integer> fontSizeList = IntStream.rangeClosed(7, 56).boxed().collect(Collectors.toList());  
        sizeCombo.setItems(FXCollections.observableArrayList(fontSizeList));
        sizeCombo.setTooltip(new Tooltip("Font size"));
        sizeCombo.getSelectionModel().select(5);//size 12
        updateStyleInSelection(MyTextStyle.fontSize(12));//select default font size

        List<String> fonts = Font.getFamilies();
        System.out.println("CodeareaPresenter fonts " + fonts.size());
        
        familyCombo.setItems(FXCollections.observableList(Font.getFamilies()));
        familyCombo.setTooltip(new Tooltip("Font family"));
        familyCombo.getSelectionModel().select(85);//Monospaced
        updateStyleInSelection(MyTextStyle.fontFamily("Monospaced"));//select default font family
        
        paragraphBackgroundPicker.setTooltip(new Tooltip("Paragraph background"));
        paragraphBackgroundPicker.valueProperty().addListener((o, old, color) -> updateParagraphBackground(color));
         
        //line number toolbar
        lineNo.textProperty().bind(
				Bindings.createStringBinding(() -> { 
					return "" + (codeArea.currentParagraphProperty().getValue() + 1);
		       }, codeArea.currentParagraphProperty()));
        colNo.textProperty().bind(
				Bindings.createStringBinding(() -> { 
					return "" + codeArea.caretColumnProperty().getValue();
		       }, codeArea.caretColumnProperty()));
        charPos.textProperty().bind(
				Bindings.createStringBinding(() -> { 
					return "" + codeArea.caretPositionProperty().getValue();
		       }, codeArea.caretPositionProperty()));
    }
    
    @FXML
    protected void undo(ActionEvent event) {
    	codeArea.undo();
    }  
    
    @FXML
    protected void redo(ActionEvent event) {
    	codeArea.redo();
    }
    
    @FXML
    protected void cut(ActionEvent event) {
    	codeArea.cut();
    }
    
    @FXML
    protected void copy(ActionEvent event) {
    	codeArea.copy();
    }
    
    @FXML
    protected void paste(ActionEvent event) {
    	codeArea.paste();
    }
    
    @FXML
    protected void selectSizeAction(ActionEvent event) {
    	updateStyleInSelection(MyTextStyle.fontSize(sizeCombo.getValue()));
    }
    
    @FXML
    protected void selectFamilyAction(ActionEvent event) {
    	updateStyleInSelection(MyTextStyle.fontFamily(familyCombo.getValue()));
    }
    
    private void updateStyleInSelection(MyTextStyle mixin) {
    	String css = codeArea.getStyle();
    	css = mixin.mergeIntoCss(css);
    	codeArea.setStyle(css);
    }
    
    private void updateParagraphBackground(Color color) {
   		updateStyleInSelection(MyTextStyle.backgroundColor(color));
    }
    
    @FXML
    private void formatCode() {
    	String text = codeArea.getCodeHighlighter().formatCode(codeArea.getText());
    	codeArea.selectAll();
    	codeArea.replaceSelection(text);    	
    }
       
}