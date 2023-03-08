package com.yang.ui.codearea.base;

public class BracketPair {

	public int start;
	public int end;

    public BracketPair(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "BracketPair{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}