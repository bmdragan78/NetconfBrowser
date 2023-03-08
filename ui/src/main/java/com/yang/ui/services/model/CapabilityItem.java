package com.yang.ui.services.model;



public class CapabilityItem {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CapabilityItem other = (CapabilityItem) obj;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return namespace;
	}

	private String namespace;
	
	private String params;//might include 'module', 'revision', 'features'....
	
	private boolean isMatch = false;

	
	public CapabilityItem(String namespace, String params) {
		super();
		this.namespace = namespace;
		this.params = params;
	}
	
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public boolean getIsMatch() {
		return isMatch;
	}

	public void setIsMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}
}
