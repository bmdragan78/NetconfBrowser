package com.yang.ui.services.model;



/**
 * Class used to represent a complete module
 * Linked to ModuleIdentifier !!!
 */
public class ModuleContext {//NOT USED ANYMore
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleIdentifier == null) ? 0 : moduleIdentifier.hashCode());
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
		ModuleContext other = (ModuleContext) obj;
		if (moduleIdentifier == null) {
			if (other.moduleIdentifier != null)
				return false;
		} else if (!moduleIdentifier.equals(other.moduleIdentifier))
			return false;
//		if (prefix == null) {
//			if (other.prefix != null)
//				return false;
//		} else if (!prefix.equals(other.prefix))
//			return false;
		return true;
	}

	@Override
	public String toString() {
		return moduleIdentifier.toString();
	}
	
	
	public static final String ROOT_PATH ="/*";
	
	// Example: if-mib:(urn:ietf:params:xml:ns:yang:smiv2:IF-MIB?revision=2000-06-14) 
	public static ModuleContext fromString(String moduleCtxStr) {
		String namespace = getNsFromString(moduleCtxStr);
		String revision = getRevisionFromString(moduleCtxStr);
		return new ModuleContext(namespace, revision);
	}
	
	private static String getNsFromString(String moduleCtxStr) {
		int startPath = moduleCtxStr.indexOf("(");
		int endPrefix = moduleCtxStr.indexOf("?revision=", startPath);
		return moduleCtxStr.substring(startPath+1, endPrefix);
	}

	private static String getRevisionFromString(String moduleCtxStr) {
		int startPath = moduleCtxStr.indexOf("?revision=");
		int endPrefix = moduleCtxStr.indexOf(")", startPath);
		return moduleCtxStr.substring(startPath + "?revision=".length(), endPrefix);
	}
	
	
	private ModuleIdentifier moduleIdentifier;
	

	public ModuleContext(String namespace, String revision) {
		super();
		//this.moduleIdentifier = new ModuleIdentifier(namespace, revision);
		//public ModuleIdentifier(String name, String namespace, String revision, String prefix) {
	}
	
//	public ModuleIdentifier getModuleIdentifier() {
//		return moduleIdentifier;
//	}
//	
//	public String getNamespace() {
//		return moduleIdentifier.getNamespace();
//	}
//	
//	public void setNamespace(String namespace) {
//		this.moduleIdentifier.setNamespace(namespace);
//	}
//	
//	public String getRevision() {
//		return this.moduleIdentifier.getRevision();
//	}
//	
//	public void setRevision(String revision) {
//		this.moduleIdentifier.setRevision(revision);
//	}
}
