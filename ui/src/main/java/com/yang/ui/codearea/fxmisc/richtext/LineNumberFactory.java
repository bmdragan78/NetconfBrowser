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

/**
 * Graphic factory that produces labels containing line numbers and a "+" to indicate folded paragraphs.
 * To customize appearance, use {@code .lineno} and {@code .fold-indicator} style classes in CSS stylesheets.
 */
public class LineNumberFactory<PS> implements IntFunction<Node> {

    private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
    private static final Paint DEFAULT_TEXT_FILL = Color.web("#666");
    private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.ITALIC, 14);
    private static final Font DEFAULT_FOLD_FONT = Font.font("monospace", FontWeight.BOLD, 14);
    private static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.web("#ddd"), null, null)); 
    
    //private static final String BRACKET_PAIRS = "(){}[]<>";
    private static final String BRACKET_PAIRS = "{}";

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
    
    /**
     * Use this if you extended GenericStyledArea for your own text area and you're using paragraph folding.
     *
     * @param <PS> The paragraph style type being used by the text area
     * @param format Given an int convert to a String for the line number.
     * @param isFolded Given a paragraph style PS check if it's folded.
     * @param removeFoldStyle Given a paragraph style PS, return a <b>new</b> PS that excludes fold styling.
     */
    public static <PS> IntFunction<Node> get(
            GenericStyledArea<PS, ?, ?> area,
            IntFunction<String> format,
            Predicate<PS> isFolded,
            UnaryOperator<PS> removeFoldStyle ){
        return new LineNumberFactory<>( area, format, isFolded, removeFoldStyle );
    }
    
    private final Val<Integer> nParagraphs;
    private final IntFunction<String> format;
    private final GenericStyledArea<PS, ?, ?> area;
    private final UnaryOperator<PS> removeFoldStyle;
    private final Predicate<PS> isFoldedCheck;
    

    private LineNumberFactory(
            GenericStyledArea<PS, ?, ?> area,
            IntFunction<String> format,
            Predicate<PS> isFolded,
            UnaryOperator<PS> removeFoldStyle){
        this.nParagraphs = LiveList.sizeOf(area.getParagraphs());
        this.removeFoldStyle = removeFoldStyle;
        this.isFoldedCheck = isFolded;
        this.format = format;
        this.area = area;  
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
            
            String text = area.getParagraph(idx).getText();
            
        	final int index = text.lastIndexOf("{");
			final int columnIndex = index > -1 ? index : text.lastIndexOf("}");
			
			if( columnIndex > -1) {
				
        		foldIndicator.setOnMouseClicked( ME -> {
        			int start = area.getAbsolutePosition( idx, columnIndex );
        			int end = getMatchingBracket(start);
        			if(start > end) {//works only if start<end
        				int tmp = start;
        				start = end;
        				end = tmp;
        			}
        			((CodeArea)area).foldText(start, end);
        		});
                foldIndicator.setCursor( Cursor.HAND );
                
                foldIndicator.setText( "-" );
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
    
    private Integer getMatchingBracket(int index) {
        if (index < 0 || index >= area.getLength()) return null;

        char initialBracket = area.getText(index, index + 1).charAt(0);
        int bracketTypePosition = BRACKET_PAIRS.indexOf(initialBracket); // "(){}[]<>"
        if (bracketTypePosition < 0) return null;

        // even numbered bracketTypePositions are opening brackets, and odd positions are closing
        // if even (opening bracket) then step forwards, otherwise step backwards
        int stepDirection = ( bracketTypePosition % 2 == 0 ) ? +1 : -1;

        // the matching bracket to look for, the opposite of initialBracket
        char match = BRACKET_PAIRS.charAt(bracketTypePosition + stepDirection);

        index += stepDirection;
        int bracketCount = 1;

        while (index > -1 && index < area.getLength()) {
            char code = area.getText(index, index + 1).charAt(0);
            
            if (code == initialBracket) 
            	bracketCount++;
            else if (code == match) 
            	bracketCount--;
            
            if (bracketCount == 0) 
            	return index;
            else 
            	index += stepDirection;
        }

        return null;
    }

}
