package com.yang.ui.codearea.base;

public enum TagType {
    START, END, COMPLETE1, COMPLETE2;

    @Override
    public String toString() {
        return this.name();
    }
}