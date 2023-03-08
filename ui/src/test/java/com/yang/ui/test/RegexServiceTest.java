package com.yang.ui.test;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.fxmisc.richtext.demo.richtext.MyTextStyle;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yang.ui.services.RegexService;



public class RegexServiceTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(RegexServiceTest.class);

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
	    		"			</Item>"});
//	    		"		</items>",
//	    		"	</Order>",
//	    		"</orders>"
//	    		});
	
	@Test
	public void testXmlMatch() throws Exception {
		//String sz= "\\<Item\\>(.*)\\</Item\\>";
		String sz= "<Item>(.+)</Item>";
		Pattern startTagPattern = Pattern.compile( sz, Pattern.MULTILINE);
		//String text = area.getText(0, xmlTag.getEnd());
		Matcher startMatcher = startTagPattern.matcher(xmlSample);
   	   	if(startMatcher.find()) {
   	   		System.out.println("sz=" + sz + "	text="+ xmlSample);
   	   		//System.out.println("START=" + startMatcher.group() + " start=" + startMatcher.start() + " end=" + startMatcher.end());
   	   		System.out.println("START=" + startMatcher.group(1) + " start=" + startMatcher.start(1) + " end=" + startMatcher.end(1));
   	   	}
	}
	
	@Test
	public void testXmlMatch2() throws Exception {
		String in = "num 123 num 1 num 698 num 19238 num 2134";
	    //Pattern p = Pattern.compile(".*num ([0-9]+)");
		Pattern p = Pattern.compile(".*num");
	    Matcher m = p.matcher(in);
	    if(m.find()) {
	      System.out.println(m.group(0));
	    }
	}
	
	
//	@Test
//	public void testFontSize() throws Exception {
//		MyTextStyle mixin = MyTextStyle.fontSize(23);
//		
//		String css = "-fx-font-family: Arial;"
//				+ "-fx-font-size: 12pt;"
//				+ "-fx-fill: ;";
//    	
//		LOG.debug("css before" + css);
//		css = mixin.mergeIntoCss(css);
//		LOG.debug("css after" + css);
//		assertNotNull(css);
//	}
//	
//	@Test
//	public void testFontFamily() throws Exception {
//		MyTextStyle mixin = MyTextStyle.fontFamily("Serif");
//		
//		String css = "-fx-font-family: Arial;"
//				+ "-fx-font-size: 12pt;"
//				+ "-fx-fill: ;";
//    	
//		LOG.debug("css before" + css);
//		css = mixin.mergeIntoCss(css);
//		LOG.debug("css after" + css);
//		assertNotNull(css);
//	}

}
