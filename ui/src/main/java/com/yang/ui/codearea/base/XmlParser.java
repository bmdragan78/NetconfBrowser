package com.yang.ui.codearea.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.yang.ui.codearea.fxmisc.richtext.GenericStyledArea;


public class XmlParser {
	
	private static final String COMPLETE_TAG1 = "\\<\\s*(\\w+)[^<>]*/\\s*>";					//<a att/>
    private static final String COMPLETE_TAG2 = "\\<\\s*(\\w+)[^<>]*>.*\\</\\s*\\w+\\s*>";	//<a att>xxx</a>
    private static final String END_TAG = "\\<\\s*/\\s*(\\w+)\\s*\\>";						//</a>
    private static final String START_TAG = "\\<\\s*(\\w+)[^<>]*\\>";							//<a att>
    
    private static final Pattern PATTERN = Pattern.compile(
            "(?<COMPLETE1>" + COMPLETE_TAG1 + ")"
            + "|(?<COMPLETE2>" + COMPLETE_TAG2 + ")"
            + "|(?<END>" + END_TAG + ")"
            + "|(?<START>" + START_TAG + ")"
    		);
    
    private GenericStyledArea area;
    
    public XmlParser(GenericStyledArea area) {
		this.area = area;
	}
    
    public XmlTag parseLine(int idx) {//return global char positions
    	String text = area.getParagraph(idx).getText();
        Matcher matcher = PATTERN.matcher(text);
      	if(matcher.find()) {
      		 if(matcher.group("START") != null) {
      			 //System.out.println("START " + matcher.group("START") + " " + matcher.group(8) );
      			return new XmlTag(TagType.START, matcher.group(8), area.getAbsolutePosition(idx, matcher.start("START")), area.getAbsolutePosition(idx, matcher.end("START")) );
            }else if(matcher.group("END") != null) {
              	 //System.out.println("END " + matcher.group("END") + " "  + matcher.group(6));
            	return new XmlTag(TagType.END, matcher.group(6), area.getAbsolutePosition(idx, matcher.start("END")), area.getAbsolutePosition(idx, matcher.end("END")) );
            }else if(matcher.group("COMPLETE1") != null) {
              	 //System.out.println("COMPLETE1 " + matcher.group("COMPLETE1")  + " " + matcher.group(2) );
            	return new XmlTag(TagType.COMPLETE1, matcher.group(2), area.getAbsolutePosition(idx, matcher.start("COMPLETE1")), area.getAbsolutePosition(idx, matcher.end("COMPLETE1")) );
            }else if(matcher.group("COMPLETE2") != null) {
             	 //System.out.println("COMPLETE2 " + matcher.group("COMPLETE2") + " " + matcher.group(4));
            	return new XmlTag(TagType.COMPLETE2, matcher.group(4), area.getAbsolutePosition(idx, matcher.start("COMPLETE2")), area.getAbsolutePosition(idx, matcher.end("COMPLETE2")) );
           }
        }
      	return null;
    }

    //LiveList lineList = area.getParagraphs()		->  	parse lineList ->		for each elem	-> 		match regex   ->		if match return new XmlTag
	// OR do global regex
    public XmlTag getMatchingTag(XmlTag xmlTag) {//only called for START & END tags
    	TagType type = xmlTag.getType();
    	if(TagType.START.equals(type)) {
	   	   	return matchEndTag(area.getText(xmlTag.getStart(), area.getLength()), xmlTag);
    	}else if(TagType.END.equals(type)) {
  			return matchStartTag(area.getText(0, xmlTag.getEnd()), xmlTag);  				
    	}    	
     	return null;
    }
    
    public XmlTag getMatchingTagExtended(XmlTag xmlTag, String textBetweenBrackets) {//only called for START & END tags
    	XmlTag matchTag = getMatchingTag(xmlTag);
    	if(matchTag != null)
    		return matchTag;
    	
    	//one line tag
    	TagType type = xmlTag.getType();
    	if(TagType.COMPLETE2.equals(type)) {
    		if(textBetweenBrackets != null && textBetweenBrackets.trim().length() > 0) {
    			if(textBetweenBrackets.contains("/")){//search for START tag
    	  			return matchStartTag(area.getText(0, xmlTag.getEnd()), xmlTag);
    			}else {//search for END tag
    				return matchEndTag(area.getText(xmlTag.getStart(), area.getLength()), xmlTag);
    			}
            }
    	}   	
     	return null;
    }
    
    private XmlTag matchStartTag(String text, XmlTag xmlTag) {
    	XmlTag startTag = null;
    	Pattern startTagPattern = Pattern.compile( "\\<\\s*" + xmlTag.getValue() + "[^<>]*\\>", Pattern.MULTILINE);
		Matcher startMatcher = startTagPattern.matcher(text);
		while(startMatcher.find()) {
   	   		//System.out.println("START=" + startMatcher.group() + " start=" + startMatcher.start() + " end=" + startMatcher.end() - 1);
   	   		startTag = new XmlTag(TagType.START, startMatcher.group(), startMatcher.start(), startMatcher.end() - 1);
   	   	}
		return startTag; 
    }
    
    private XmlTag matchEndTag(String text, XmlTag xmlTag) {
    	XmlTag endTag = null;
		Pattern endTagPattern = Pattern.compile("\\<\\s*?/\\s*?" + xmlTag.getValue() + "\\s*?\\>" , Pattern.MULTILINE);
		Matcher endMatcher = endTagPattern.matcher(text);
   	   	if(endMatcher.find()) { 
   	   		//System.out.println("END=" + endMatcher.group() + " start=" + (endMatcher.start() + xmlTag.getStart()) + " end=" + (endMatcher.end() + xmlTag.getStart() - 1));
   	   		endTag = new XmlTag(TagType.END, endMatcher.group(), endMatcher.start() + xmlTag.getStart(), endMatcher.end() + xmlTag.getStart() - 1 );
   	   	}
   	   	return endTag;
    }
}
