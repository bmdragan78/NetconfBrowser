package com.yangui.main.services.model;



public class ModuleIdentifier implements Comparable<ModuleIdentifier> {


	@Override
	public int compareTo(ModuleIdentifier o) {
		int result = name.compareTo(o.name);
		if(result == 0)
			result = namespace.compareTo(o.namespace);
		if(result == 0)
			result = revision.compareTo(o.revision);
		return result;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		//result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((revision == null) ? 0 : revision.hashCode());
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
		ModuleIdentifier other = (ModuleIdentifier) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
//		if (prefix == null) {
//			if (other.prefix != null)
//				return false;
//		} else if (!prefix.equals(other.prefix))
//			return false;
		if (revision == null) {
			if (other.revision != null)
				return false;
		} else if (!revision.equals(other.revision))
			return false;
		return true;
	}

	@Override
	public String toString() {//NOT USED
		return "(" + namespace + YangNode.QNAME_REVISION_DELIMITER + revision + ")";
		//return "name";
	}
	
	private String name;
	
	private String namespace;

	private String revision;
	
	private String prefix;
	
	
	public ModuleIdentifier(String name, String namespace, String revision, String prefix) {
		super();
		this.name = name;
		this.namespace = namespace;
		this.revision = revision;
		this.prefix = prefix;
	}
	
	public String getName() {
		return name;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getRevision() {
		return revision;
	}

	public String getPrefix() {
		return prefix;
	}
}
