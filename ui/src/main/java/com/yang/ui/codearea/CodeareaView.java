package com.yang.ui.codearea;

import com.yang.ui.afterburner.views.FXMLView;
import com.yang.ui.codearea.fxmisc.richtext.CodeArea;


public class CodeareaView extends FXMLView {
	
	public CodeArea getCodeArea() {
		CodeareaPresenter presenter = (CodeareaPresenter) this.getPresenter();
		return presenter.getCodeArea();
	}
	
}

