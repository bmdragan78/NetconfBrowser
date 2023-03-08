package com.yang.ui.codearea.base;

import java.util.Collection;
import com.yang.ui.codearea.fxmisc.richtext.model.StyleSpans;


public interface CodeHighlighter {
	
	void highlightBracket(int newVal);
	
	void clearBracket();
	
	StyleSpans<Collection<String>> highlightCode(String text);
	
	String formatCode(String text);

}
