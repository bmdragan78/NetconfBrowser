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


public class XmlHighlighter implements CodeHighlighter {
	
	private static final Pattern XML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
    		+"|(?<COMMENT><!--[^<>]+-->)");
    
    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");
    
    private static final int GROUP_OPEN_BRACKET = 2;
    private static final int GROUP_ELEMENT_NAME = 3;
    private static final int GROUP_ATTRIBUTES_SECTION = 4;
    private static final int GROUP_CLOSE_BRACKET = 5;
    private static final int GROUP_ATTRIBUTE_NAME = 1;
    private static final int GROUP_EQUAL_SYMBOL = 2;
    private static final int GROUP_ATTRIBUTE_VALUE = 3;
    
    private static final List<String> CLEAR_STYLE = Collections.emptyList();
    private static final List<String> MATCH_STYLE = Collections.singletonList("match");
    private static final String BRACKET_PAIRS = "(){}[]<>";
    
    private final CodeArea codeArea;
    private final XmlParser xmlParser;
    private List<BracketPair> bracketPairs;


    public XmlHighlighter(CodeArea codeArea) {
        this.codeArea = codeArea;
        this.xmlParser = new XmlParser(codeArea);
        this.bracketPairs = new ArrayList<>();
        // listen for changes in text or caret position
        //this.codeArea.addTextInsertionListener((start, end, text) -> clearBracket());
        this.codeArea.caretPositionProperty().addListener((obs, oldVal, newVal) -> {
        	Platform.runLater(() -> {
        		try {
        			clearBracket();
            		codeArea.setStyleSpans( 0, highlightCode(codeArea.getText()) ); 
            		highlightBracket(newVal);
        		}catch(Exception ex) {
        			
        		}
        	 });
        });
//        this.codeArea.selectedTextProperty().addListener((obs, oldSelection, newSelection) -> {
//        	 System.out.println("selectedTextProperty " + newSelection);
//        });
        	 
    }
    
   private void styleBrackets(BracketPair pair, List<String> styles) {
	   //select only matches
//      styleBracket(pair.start, styles);
//      styleBracket(pair.end, styles);
  	
          //OR select range
  		if(pair.start > pair.end) {//works only if start<end
			int tmp = pair.start;
			pair.start = pair.end;
			pair.end = tmp;
		}
  		//System.out.println("styleBrackets 1 between " + pair.start + ", " + pair.end);
  		codeArea.setStyle(pair.start, pair.end + 1, styles);
   }

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
            styleBrackets(pair, MATCH_STYLE);
            this.bracketPairs.add(pair);
            
            //select matching xml tag
            XmlTag startTag = xmlParser.parseLine(codeArea.getCurrentParagraph());
            //XmlTag endTag = xmlParser.getMatchingTag(startTag);
            
            String textBetweenBrackets = codeArea.getText(pair.start, pair.end);
            XmlTag endTag = xmlParser.getMatchingTagExtended(startTag, textBetweenBrackets);
            BracketPair pair1 = new BracketPair(endTag.getStart(), endTag.getEnd());
            styleBrackets(pair1, MATCH_STYLE);
            this.bracketPairs.add(pair1);
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

    private void styleBracket(int pos, List<String> styles) {
        if (pos < codeArea.getLength()) {
            String text = codeArea.getText(pos, pos + 1);
            //if (BRACKET_PAIRS.contains(text)) {
                //codeArea.suspendVisibleParsWhile1(() -> {
                	codeArea.setStyle(pos, pos + 1, styles);
                	//});
                //System.out.println("BRACKET_PAIRS pos " + pos + " text " + text);
            //}
        }
    }

    @Override
    public  StyleSpans<Collection<String>> highlightCode(String text) {
    	
        Matcher matcher = XML_TAG.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
        	
        	spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
        	if(matcher.group("COMMENT") != null) {
        		spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
        	}
        	else {
        		if(matcher.group("ELEMENT") != null) {
        			String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);
        			
        			spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
        			spansBuilder.add(Collections.singleton("anytag"), matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

        			if(!attributesText.isEmpty()) {
        				
        				lastKwEnd = 0;
        				
        				Matcher amatcher = ATTRIBUTES.matcher(attributesText);
        				while(amatcher.find()) {
        					spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
        					spansBuilder.add(Collections.singleton("attribute"), amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
        					spansBuilder.add(Collections.singleton("tagmark"), amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
        					spansBuilder.add(Collections.singleton("avalue"), amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
        					lastKwEnd = amatcher.end();
        				}
        				if(attributesText.length() > lastKwEnd)
        					spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
        			}

        			lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);
        			
        			spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
        		}
        	}
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
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
