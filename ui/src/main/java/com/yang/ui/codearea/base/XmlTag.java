package com.yang.ui.codearea.base;


public class XmlTag{
	
	final private TagType type;
	final private String value;
	final private int start;
	final private int end;
	
	public XmlTag(TagType type, String value, int start, int end) {
		super();
		this.type = type;
		this.value = value;
		this.start = start;
		this.end = end;
	}
	
	public TagType getType() {
		return type;
	}
	public String getValue() {
		return value;
	}
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return end;
	}
}