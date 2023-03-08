package com.yang.ui.codearea.fxmisc.richtext;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import com.yang.ui.codearea.base.CodeHighlighter;
import com.yang.ui.codearea.base.XmlHighlighter;
import com.yang.ui.codearea.base.YangHighlighter;
import com.yang.ui.codearea.fxmisc.richtext.model.EditableStyledDocument;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * A convenience subclass of {@link StyleClassedTextArea} with fixed-width font and an undo manager that observes
 * only plain text changes (not styled changes). It's style class is {@code code-area}.
 */
public class CodeArea extends StyleClassedTextArea {
	private static final String yangSample = String.join("\n", new String[] {
	        "module earth {\n"
	        + "\n"
	        + "    yang-version 1;\n"
	        + "\n"
	        + "    namespace \"urn:ietf:params:xml:ns:yang:earth\";\n"
	        + "\n"
	        + "    prefix my-module;\n"
	        + "\n"
	        + "    organization \"my-module Description\";\n"
	        + "\n"
	        + "    contact \"email: user@org.com\";\n"
	        + "\n"
	        + "    description \"This module is a basic yang file\";\n"
	        + "\n"
	        + "    revision \"2018-02-10\" {\n"
	        + "      description \"Initial revision.\";\n"
	        + "      reference \"RFC XXX\";\n"
	        + "    }\n"
	        + "    \n"
	        + "    container earthinfo {\n"
	        + "\n"
	        + "	    leaf info1 {\n"
	        + "	      type int32;\n"
	        + "	      config false;\n"
	        + "	      description \"Added by me\";\n"
	        + "	    }	    	   \n"
	        + "	    \n"
	        + "	    list country {\n"
	        + "      		key \"name\";\n"
	        + "      		description \"Country info\";\n"
	        + "\n"
	        + "		    leaf name {\n"
	        + "		      type string;\n"
	        + "		      description \"Column one of listM\";\n"
	        + "		    }\n"
	        + "\n"
	        + "		   leaf gdp {\n"
	        + "		      type string;\n"
	        + "		      description \"Column one of listM\";\n"
	        + "		    }\n"
	        + "		    \n"
	        + "		    leaf size {\n"
	        + "		      type string;\n"
	        + "		      description \"Column one of listM\";\n"
	        + "		    }\n"
	        + "		    \n"
	        + "		      leaf-list regions {\n"
	        + "		 	type string;\n"
	        + "		 	description \"Device Model\";\n"
	        + "	    }\n"
	        + "		\n"
	        + "		    container cities {\n"
	        + "			leaf counter {\n"
	        + "	      			type int32;\n"
	        + "	      			config false;\n"
	        + "	      			description \"Added by me\";\n"
	        + "	    		}\n"
	        + "			list city {\n"
	        + "		      		key \"name\";\n"
	        + "      				description \"Country info\";\n"
	        + "\n"
	        + "			    leaf name {\n"
	        + "			      type string;\n"
	        + "			      description \"Column one of listM\";\n"
	        + "			    }\n"
	        + "			    leaf population {\n"
	        + "			      type string;\n"
	        + "			      description \"Column one of listM\";\n"
	        + "			    }\n"
	        + "			    leaf size {\n"
	        + "		      		type string;\n"
	        + "		      		description \"Column one of listM\";\n"
	        + "		    	}\n"
	        + "			}\n"
	        + "		     \n"
	        + "		    }\n"
	        + "	     }\n"
	        + "	}\n"
	        + "	\n"
	        + "}\n"
	        + ""
	    });
	
	 private static final String xmlSample = String.join("\n", new String[] {
	    		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
	    		"<!-- Sample XML -->",
	    		"< orders >",
	    		"	<Order number=\"1\" table=\"center\">",
	    		"		<items>",
	    		"			<Item>",
	    		"				<type>ESPRESSO</type>",
	    		"				<shots>2</shots>",
	    		"				<iced>false</iced>",
	    		"				<orderNumber>1</orderNumber>",
	    		"			</Item>",
	    		"			<Item>",
	    		"				<type>CAPPUCCINO</type>",
	    		"				<shots>1</shots>",
	    		"				<iced>false</iced>",
	    		"				<orderNumber>1</orderNumber>",
	    		"			</Item>",
	    		"			<Item>",
	    		"			<type>LATTE</type>",
	    		"				<shots>2</shots>",
	    		"				<iced>false</iced>",
	    		"				<orderNumber>1</orderNumber>",
	    		"			</Item>",
	    		"			<Item>",
	    		"				<type>MOCHA</type>",
	    		"				<shots>3</shots>",
	    		"				<iced>true</iced>",
	    		"				<orderNumber>1</orderNumber>",
	    		"			</Item>",
	    		"		</items>",
	    		"	</Order>",
	    		"</orders>"
	    		});
	
	public static final int TAB_SIZE = 4;

    {
        getStyleClass().add("code-area");

        // load the default style that defines a fixed-width font
        getStylesheets().add(CodeArea.class.getResource("code-area.css").toExternalForm());

        // don't apply preceding style to typed text
        setUseInitialStyleForInsertion(true);
    }
    
    /**
     * Creates an area that can render and edit the same {@link EditableStyledDocument} as another {@link CodeArea}.
     */
//    public CodeArea(@NamedArg("document") EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
//        super(document, false);
//    }
    
    private CodeHighlighter codeHighlighter;

    public CodeHighlighter getCodeHighlighter() {
		return codeHighlighter;
	}

//	public void setCodeHighlighter(CodeHighlighter codeHighlighter) {
//		this.codeHighlighter = codeHighlighter;
//	}

	public void initYang(boolean initalize) {
		codeHighlighter = new YangHighlighter(this);
        setParagraphGraphicFactory(YangLineNumberFactory.get(this));
        //codeArea.setContextMenu( new DefaultContextMenu() );
        if(initalize)
        	replaceText(0, 0, yangSample);
	}
	
	
	public void initXml(boolean initalize) {
		codeHighlighter = new XmlHighlighter(this);
        setParagraphGraphicFactory(XmlLineNumberFactory.get(this));
        if(initalize)
        	replaceText(0, 0, xmlSample);
	}
	/**
     * Creates an area with no text.
     */
    protected final Pattern whiteSpacePattern = Pattern.compile( "^\\s+" );
    protected Pattern WORD_PATTERN = Pattern.compile( "\\w+" );
    protected Pattern WORD_OR_SYMBOL = Pattern.compile(
            "([\\W&&[^\\h]]{2}"    // Any two non-word characters (excluding white spaces), matches like:
                                   // !=  <=  >=  ==  +=  -=  *=  --  ++  ()  []  <>  &&  ||  //  /*  */
            +"|\\w*)"              // Zero or more word characters [a-zA-Z_0-9]
            +"\\h*"                // Both cases above include any trailing white space
        );
    
    public CodeArea() {
        super(false);
        
        this.setEditable(true);
		this.setWrapText(false);
        //tab size
        InputMap<KeyEvent> im = InputMap.consume(EventPattern.keyPressed(KeyCode.TAB), e -> this.replaceSelection(" ".repeat(TAB_SIZE)));
        Nodes.addInputMap(this, im);
        //preserve indentation on ENTER
        this.addEventFilter( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) 
            {
                Matcher m = whiteSpacePattern.matcher( this.getParagraph( this.getCurrentParagraph() ).getSegments().get( 0 ) );
                if ( m.find() ) Platform.runLater( () -> this.insertText( this.getCaretPosition(), m.group() ) );
            }
        });
        //HOME key
        this.addEventFilter( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.HOME )
            {
                Matcher m = whiteSpacePattern.matcher( this.getParagraph( this.getCurrentParagraph() ).getSegments().get( 0 ) );
                if ( m.find() ) Platform.runLater( () ->  this.moveTo(this.getCurrentParagraph(), m.end()) );
            }
        });
    }

    /**
     * Creates a text area with initial text content.
     * Initial caret position is set at the beginning of text content.
     *
     * @param text Initial text content.
     */
//    public CodeArea(@NamedArg("text") String text) {
//        this();
//
//        appendText(text);
//        getUndoManager().forgetHistory();
//        getUndoManager().mark();
//
//        // position the caret at the beginning
//        selectRange(0, 0);
//    }
   
   

    /**
     * Skips ONLY 1 number of word boundaries backwards.
     * @param n is ignored !
     */
    @Override
    public void wordBreaksBackwards(int n, SelectionPolicy selectionPolicy)
    {
        if ( getLength() == 0 ) return;

        CaretSelectionBind<?,?,?> csb = getCaretSelectionBind();
        int paragraph = csb.getParagraphIndex();
        int position = csb.getColumnPosition(); 
        int prevWord = 0;

        if ( position == 0 ) {
            prevWord = getParagraph( --paragraph ).length();
            moveTo( paragraph, prevWord, selectionPolicy );
            return;
        }
        
        Matcher m = WORD_OR_SYMBOL.matcher( getText( paragraph ) );
        
        while ( m.find() )
        {
            if ( m.start() == position ) {
                moveTo( paragraph, prevWord, selectionPolicy );
                break;
            }
            if ( (prevWord = m.end()) >= position ) {
                moveTo( paragraph, m.start(), selectionPolicy );
                break;
            }
        }
    }
    
    /**
     * Skips ONLY 1 number of word boundaries forward.
     * @param n is ignored !
     */
    @Override
    public void wordBreaksForwards(int n, SelectionPolicy selectionPolicy)
    {
        if ( getLength() == 0 ) return;

        CaretSelectionBind<?,?,?> csb = getCaretSelectionBind();
        int paragraph = csb.getParagraphIndex();
        int position = csb.getColumnPosition(); 
        
        Matcher m = WORD_OR_SYMBOL.matcher( getText( paragraph ) );
        
        while ( m.find() )
        {
            if ( m.start() > position ) {
                moveTo( paragraph, m.start(), selectionPolicy );
                break;
            }
            if ( m.hitEnd() ) {
                moveTo( paragraph+1, 0, selectionPolicy );
            }
        }
    }
    
    @Override
    public void selectWord()
    {
        if ( getLength() == 0 ) return;

        CaretSelectionBind<?,?,?> csb = getCaretSelectionBind();
        int paragraph = csb.getParagraphIndex();
        int position = csb.getColumnPosition(); 
        
        Matcher m = WORD_PATTERN.matcher( getText( paragraph ) );

        while ( m.find() )
        {
            if ( m.end() > position ) {
                csb.selectRange( paragraph, m.start(), paragraph, m.end() );
                return;
            }
        }
    }
}
