package com.yang.ui.services.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.opendaylight.yangtools.yang.common.QName;


public class YangFile implements Comparable<YangFile>{


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result + id;
		//result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		YangFile other = (YangFile) obj;
//		if (id != other.id)
//			return false;
//		if (moduleName == null) {
//			if (other.moduleName != null)
//				return false;
//		} else if (!moduleName.equals(other.moduleName))
//			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		//return name + " id=(" + id + ") ";//breaks equals
		return name;
	}
	
	@Override
	public int compareTo(YangFile o) {
		int result = name.compareTo(o.name);
//		if(result == 0)
//			result = moduleIdentifier.compareTo(o.moduleIdentifier);
		return result;
	}
	
	private boolean isCreate;//true for create file & false for update file

	private String name;//because we inject the YangFile bean into controllers we cannot compute dynamically name based on path

	private String path;
	
	private long size;
	
	private Date creationTime;//COULD WORK ONLY IN SOME FILESYSTEMS LIKE EXT4 WITH EXTRA SCRIPTS; NOT USED NOW
	
	private Date lastModifiedTime;
	
	private String content;//determinate module name and revision by applying Regex to content -> save Module name on file bean???
	
	//Opendaylight schema types are set by regex on file content
	private String moduleName;
	private String moduleNamespace;
	private String moduleRevision;
	private String prefix;
	private ModuleIdentifier moduleIdentifier;
	private List<String> moduleImports;
	private Set<QName> moduleFeatures;
	
	private int id;//0 for unsaved files
	private Path filePath;
	
	public YangFile() {
		super();
	}
	
	//public YangFile(int id, String name, String path, long size, Date lastModifiedTime, String content, String moduleName, String prefix, String moduleNamespace, String moduleRevision, List<String> moduleImports, Set<QName> moduleFeatures) {
	public YangFile(int id, String name, String path, long size, Date lastModifiedTime, String content, String moduleName, String prefix, String moduleNamespace, String moduleRevision, List<String> moduleImports, Set<QName> moduleFeatures, Path filePath) {
		super();
		this.id = id;
		this.isCreate = false;
		this.name = name;
		this.path = path;
		this.size = size;
		this.lastModifiedTime = lastModifiedTime;
		this.content = content;
		this.moduleName = moduleName;
		this.prefix = prefix;
		this.moduleNamespace = moduleNamespace;
		this.moduleRevision = moduleRevision;
		
		this.moduleIdentifier = new ModuleIdentifier(moduleName, moduleNamespace, moduleRevision, prefix);
		//this.moduleCtx = new ModuleContext(moduleNamespace, moduleRevision);
		this.moduleImports = moduleImports;
		this.moduleFeatures = moduleFeatures;
		this.filePath = filePath;
	}
	
	public int getId() {
		return id;
	}
	public String getIdString() {
		return String.valueOf(id);
	}
	public boolean isCreate() {
		return isCreate;
	}

	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleNamespace() {
		return moduleNamespace;
	}

	public void setModuleNamespace(String moduleNamespace) {
		this.moduleNamespace = moduleNamespace;
	}

	public String getModuleRevision() {
		return moduleRevision;
	}

	public void setModuleRevision(String moduleRevision) {
		this.moduleRevision = moduleRevision;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public ModuleIdentifier getModuleIdentifier() {
		return moduleIdentifier;
	}

	public void setModuleIdentifier(ModuleIdentifier moduleIdentifier) {
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public List<String> getModuleImports() {
		return moduleImports;
	}

	public void setModuleImports(List<String> moduleImports) {
		this.moduleImports = moduleImports;
	}

	public Set<QName> getModuleFeatures() {
		return moduleFeatures;
	}

	public Path getFilePath() {
		return filePath;
	}

}
