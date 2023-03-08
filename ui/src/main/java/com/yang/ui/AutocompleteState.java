package com.yang.ui;

import com.yang.ui.codearea.fxmisc.richtext.model.PlainTextChange;



class AutocompleteState {

    private static final String EMPTY_STRING = "";

    private State state;

	private String filterText;
	
	private String insertText;

	public static enum State { SHOW, HIDE, REFILTER, INSERTION }

    
    private AutocompleteState(String filterText, State state) {
        this.filterText = filterText;
        this.state = state;
    }

    public static AutocompleteState initial() {
        return new AutocompleteState(EMPTY_STRING, State.HIDE);
    }

    public void showBox() {
    	filterText = "";
    	state = State.SHOW;
    }

    public void insertSelected(String insertText) {
    	this.insertText = insertText;
    	state = State.INSERTION;
    	System.out.println("insertSelected() -> insertText " + insertText);
    }
    
    public void hideBox() {
    	state = State.HIDE;
        filterText = EMPTY_STRING;
    }

    //one letter at a time???
    public void updateFilter(PlainTextChange textChange) {
    	String inserted = textChange.getInserted();
    	String removed = textChange.getRemoved();
    	if (inserted != null && inserted.trim().length() > 0) {
            filterText += inserted;
	    }else if (removed != null && removed.trim().length() > 0) {
	    	if(filterText.trim().length() == 1)
	    		filterText = "";
	    	else
	    		filterText = filterText.trim().substring(0, filterText.trim().length() - 1);
	    }
    	//state = !filterText.isEmpty() ? State.REFILTER : State.HIDE;
    	state = State.REFILTER ;
    	System.out.println("updateFilter() -> filterText " + filterText + "		inserted " + textChange.getInserted() + " removed " + textChange.getRemoved());
    }
    
    
    public State getState() {
		return state;
	}
    
    public String getFilterText() {
		return filterText;
	}
    
	public String getInsertText() {
		return insertText;
	}
}