package com.yang.ui.codearea.base;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reactfx.collection.LiveList;

import com.google.common.base.CharMatcher;
import com.yang.ui.codearea.fxmisc.richtext.CodeArea;
import com.yang.ui.codearea.fxmisc.richtext.model.Paragraph;
import com.yang.ui.codearea.fxmisc.richtext.model.StyleSpans;
import com.yang.ui.codearea.fxmisc.richtext.model.StyleSpansBuilder;


public class YangHighlighter implements CodeHighlighter {
	
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
    private static final String STRING_PATTERN = "\"([^\"]*)\"";
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

    private final CodeArea codeArea;

    // the list of highlighted bracket pairs
    private List<BracketPair> bracketPairs;

    // constants
    private static final List<String> CLEAR_STYLE = Collections.emptyList();
    private static final List<String> MATCH_STYLE = Collections.singletonList("match");
    private static final String BRACKET_PAIRS = "(){}[]<>";

    /**
     * Parameterized constructor
     * @param codeArea the code area
     */
    public YangHighlighter(CodeArea codeArea) {
        this.codeArea = codeArea;

        this.bracketPairs = new ArrayList<>();

        // listen for changes in text or caret position
        //this.codeArea.addTextInsertionListener((start, end, text) -> clearBracket());
        this.codeArea.caretPositionProperty().addListener((obs, oldVal, newVal) -> {
        	
        	System.out.println("caret moved at " + newVal);
        	
        	Platform.runLater(() -> {
        		try {
	        		clearBracket();
	        		codeArea.setStyleSpans( 0, highlightCode(codeArea.getText()) ); 
	        		highlightBracket(newVal);
        		}catch(Exception ex) {
        			
        		}
        	 });
        });
    }
    
    @Override
    public StyleSpans<Collection<String>> highlightCode(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Highlight the matching bracket at current caret position
     */
//    public void highlightBracket() {
//        this.highlightBracket(codeArea.getCaretPosition());
//    }
    @Override
    public void highlightBracket(int newVal) {
        // first clear existing bracket highlights
        this.clearBracket();

        // detect caret position both before and after bracket
        String prevChar = (newVal > 0 && newVal <= codeArea.getLength()) ? codeArea.getText(newVal - 1, newVal) : "";
        if (BRACKET_PAIRS.contains(prevChar)) newVal--;

        // get other half of matching bracket
        Integer other = getMatchingBracket(newVal);

        if (other != null) {
            // other half exists
            BracketPair pair = new BracketPair(newVal, other);

            // highlight pair
            styleBrackets(pair, MATCH_STYLE);

            // add bracket pair to list
            this.bracketPairs.add(pair);
        }
    }

    private Integer getMatchingBracket(int index) {
        if (index < 0 || index >= codeArea.getLength()) return null;

        char initialBracket = codeArea.getText(index, index + 1).charAt(0);
        int bracketTypePosition = BRACKET_PAIRS.indexOf(initialBracket); // "(){}[]<>"
        if (bracketTypePosition < 0) return null;

        // even numbered bracketTypePositions are opening brackets, and odd positions are closing
        // if even (opening bracket) then step forwards, otherwise step backwards
        int stepDirection = ( bracketTypePosition % 2 == 0 ) ? +1 : -1;

        // the matching bracket to look for, the opposite of initialBracket
        char match = BRACKET_PAIRS.charAt(bracketTypePosition + stepDirection);

        index += stepDirection;
        int bracketCount = 1;

        while (index > -1 && index < codeArea.getLength()) {
            char code = codeArea.getText(index, index + 1).charAt(0);
            if (code == initialBracket) bracketCount++;
            else if (code == match) bracketCount--;
            if (bracketCount == 0) return index;
            else index += stepDirection;
        }

        return null;
    }
    
    @Override
    public void clearBracket() {
        // get iterator of bracket pairs
        Iterator<BracketPair> iterator = this.bracketPairs.iterator();

        // loop through bracket pairs and clear all
        while (iterator.hasNext()) {
            // get next bracket pair
            BracketPair pair = iterator.next();

            // clear pair
            styleBrackets(pair, CLEAR_STYLE);

            // remove bracket pair from list
            iterator.remove();
        }
    }

    private void styleBrackets(BracketPair pair, List<String> styles) {
        styleBracket(pair.start, styles);
        styleBracket(pair.end, styles);
    }

    private void styleBracket(int pos, List<String> styles) {
        if (pos < codeArea.getLength()) {
            String text = codeArea.getText(pos, pos + 1);
            if (BRACKET_PAIRS.contains(text)) {
                //codeArea.suspendVisibleParsWhile1(() -> {
                	codeArea.setStyle(pos, pos + 1, styles);
                	//});
                //System.out.println("BRACKET_PAIRS pos " + pos + " text " + text);
            }
        }
    }

    @Override
 	public String formatCode(String text) {
    	//break lines on boundaries '{};'
    	text = breakLines(text);
    	codeArea.selectAll();
    	codeArea.replaceSelection(text);
    	//align each line based on open parantesis count
    	StringBuilder sb = new StringBuilder();
    	//iterate through lines of codeArea
    	LiveList<Paragraph<Collection<String>, String, Collection<String>>> paragraphs = codeArea.getParagraphs();
    	int lineCount = codeArea.getParagraphs().size();
    	for(int i=0; i< lineCount; i++) {
    		Paragraph<Collection<String>, String, Collection<String>> paragraph = paragraphs.get(i);
    	
    		String line = paragraph.getText();//without new line
    		String stripedLine = line.strip();

    		int tabCount = 0;
    		if(stripedLine.endsWith("{") ||
    				stripedLine.endsWith(";")) { 
    			tabCount = getOpenBracketsCount(sb);
    			appendSpaces(tabCount * CodeArea.TAB_SIZE, sb);
    			sb.append(stripedLine);
    			
    			if(tabCount == 1  && stripedLine.endsWith(";")) {
    				sb.append(System.lineSeparator());
    			}
    			
    		}else if(stripedLine.endsWith("}")) { 
    			tabCount = getOpenBracketsCount(sb) - 1;
    			appendSpaces(tabCount * CodeArea.TAB_SIZE, sb);
    			sb.append(stripedLine);
    			
    			if(tabCount == 1) {
    				sb.append(System.lineSeparator());
    			}
    			
    		}else if(stripedLine.equals("")) {//append empty lines
    			sb.append(line);
    		}else {
    			tabCount = getOpenBracketsCount(sb);
    			appendSpaces(tabCount * CodeArea.TAB_SIZE, sb);
    			sb.append(stripedLine);
    		}

    		if(i < lineCount-1) {//do not add new line for last line
    			sb.append(System.lineSeparator());
    		}
    	};
 		return sb.toString();
 	}
    
    private String breakLines(String text) {
    	text = text.replaceAll("\\{\\s*", "{" + System.lineSeparator());
    	text = text.replaceAll(";\\s*", ";" + System.lineSeparator());
    	//text = text.replaceAll("\\}\\s*", "}" + System.lineSeparator() + System.lineSeparator());
    	text = text.replaceAll("\\}\\s*", "}" + System.lineSeparator());
    	return text;
    }
    
    private void appendSpaces(int wsCount, StringBuilder sb) {
		for(int i=0; i< wsCount; i++) {
			sb.append(" ");
		}
    }
    
    private int getOpenBracketsCount(StringBuilder sb) {
    	return CharMatcher.is("{".charAt(0)).countIn(sb.toString()) - CharMatcher.is("}".charAt(0)).countIn(sb.toString());
    }


}
