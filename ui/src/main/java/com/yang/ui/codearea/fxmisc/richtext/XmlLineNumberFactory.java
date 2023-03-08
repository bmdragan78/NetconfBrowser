package com.yang.ui.codearea.fxmisc.richtext;

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import com.yang.ui.codearea.base.TagType;
import com.yang.ui.codearea.base.XmlParser;
import com.yang.ui.codearea.base.XmlTag;


public class XmlLineNumberFactory<PS> implements IntFunction<Node> {

    private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
    private static final Paint DEFAULT_TEXT_FILL = Color.web("#666");
    private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.ITALIC, 14);
    private static final Font DEFAULT_FOLD_FONT = Font.font("monospace", FontWeight.BOLD, 14);
    private static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.web("#ddd"), null, null)); 
    
    public static IntFunction<Node> get(GenericStyledArea<?, ?, ?> area) {
        return get(area, digits -> "%1$" + digits + "s");
    }
    
    public static <PS> IntFunction<Node> get( GenericStyledArea<PS, ?, ?> area, IntFunction<String> format ){
        if ( area instanceof StyleClassedTextArea ) {
            StyleClassedTextArea classArea = (StyleClassedTextArea) area; 
            return get( classArea, format, classArea.getFoldStyleCheck(), classArea.getRemoveFoldStyle() );
        }
        else if ( area instanceof InlineCssTextArea ) {
            InlineCssTextArea inlineArea = (InlineCssTextArea) area;
            return get( inlineArea, format, inlineArea.getFoldStyleCheck(), inlineArea.getRemoveFoldStyle() );
        }
        return get( area, format, null, null );
    }
    
    public static <PS> IntFunction<Node> get(
            GenericStyledArea<PS, ?, ?> area,
            IntFunction<String> format,
            Predicate<PS> isFolded,
            UnaryOperator<PS> removeFoldStyle ){
        return new XmlLineNumberFactory<>( area, format, isFolded, removeFoldStyle );
    }
    
    private final Val<Integer> nParagraphs;
    private final IntFunction<String> format;
    private final GenericStyledArea<PS, ?, ?> area;
    private final UnaryOperator<PS> removeFoldStyle;
    private final Predicate<PS> isFoldedCheck;
    private final XmlParser xmlParser;

    private XmlLineNumberFactory(
            GenericStyledArea<PS, ?, ?> area,
            IntFunction<String> format,
            Predicate<PS> isFolded,
            UnaryOperator<PS> removeFoldStyle){
        this.nParagraphs = LiveList.sizeOf(area.getParagraphs());
        this.removeFoldStyle = removeFoldStyle;
        this.isFoldedCheck = isFolded;
        this.format = format;
        this.area = area;  
        this.xmlParser = new XmlParser(area);
    }

    @Override
    public Node apply(int idx) {
        Val<String> formatted = nParagraphs.map(n -> format(idx+1, n));

        Label lineNo = new Label();
        lineNo.setFont(DEFAULT_FONT);
        lineNo.setBackground(DEFAULT_BACKGROUND);
        lineNo.setTextFill(DEFAULT_TEXT_FILL);
        lineNo.setPadding(DEFAULT_INSETS);
        lineNo.setAlignment(Pos.TOP_RIGHT);
        lineNo.getStyleClass().add("lineno");
        // bind label's text to a Val that stops observing area's paragraphs
        // when lineNo is removed from scene
        lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));
        
        if ( isFoldedCheck != null ){//always true?
        	
            Label foldIndicator = new Label( " " );
            foldIndicator.setTextFill( Color.BLUE ); // Prevents CSS errors
            foldIndicator.setFont( DEFAULT_FOLD_FONT );

            lineNo.setContentDisplay( ContentDisplay.RIGHT );
            lineNo.setGraphic( foldIndicator );
            
            XmlTag xmlTag = xmlParser.parseLine(idx);//return global char positions
            if(xmlTag != null) {
                TagType type   = xmlTag.getType();
                if(TagType.START.equals(type) || TagType.END.equals(type)) {
            		foldIndicator.setOnMouseClicked( ME -> {
            			int start = xmlTag.getStart();
            			XmlTag matchTag = xmlParser.getMatchingTag(xmlTag);
            			if(matchTag != null) {
            				int end = matchTag.getEnd();
            				if(start > end) {//works only if start<end
                				int tmp = start;
                				start = end;
                				end = tmp;
                			}
                			((CodeArea)area).foldText(start, end);
            			}
            		});
                    foldIndicator.setCursor( Cursor.HAND );
                    foldIndicator.setText( "-" );
                }
            }
            
            if ( area.getParagraphs().size() > idx+1 ) {
                if ( isFoldedCheck.test( area.getParagraph( idx+1 ).getParagraphStyle() )
                && ! isFoldedCheck.test( area.getParagraph( idx ).getParagraphStyle() ) ) {
                	
                    foldIndicator.setOnMouseClicked( ME -> {
                		area.unfoldParagraphs(idx, isFoldedCheck, removeFoldStyle);
                	});
                    foldIndicator.getStyleClass().add( "fold-indicator" );
                    foldIndicator.setCursor( Cursor.HAND );
                    foldIndicator.setText( "+" );
                }
            }
        }

        return lineNo;
    }
    
    private String format(int x, int max) {
        int digits = (int) Math.floor(Math.log10(max)) + 1;
        return String.format(format.apply(digits), x);
    }
    
}
