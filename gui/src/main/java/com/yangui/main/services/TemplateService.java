package com.yangui.main.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.opendaylight.yangtools.yang.common.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yangui.gui.Conf;
import com.yangui.main.services.model.YangFile;
import com.yangui.main.services.model.YangNode;


@Singleton
public class TemplateService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TemplateService.class);
	
	@Inject
	private RegexService regexService;
	
	  
	/** Returns the bean file created from template file */
	public YangFile getDefaultTemplateFile(){
		  
		LOG.debug("getDefaultTemplateFile");
		YangFile yangFile = null;
		try {
			
			//load template from jar folder
//	        Path templateFile = SchemaAPI.getPathFromResources(templatesFolder + "/" + defaultTemplate + SchemaSrv.YANG_EXT);
			Path templateFile = Paths.get("/" + Conf.TEMPLATE_PATH);
	        BasicFileAttributes yangFileAttr = Files.readAttributes(templateFile, BasicFileAttributes.class);
	        
	        String name = FileService.getFileFromPath(templateFile);
	        //String path = schemaFolder + "/" + defaultTemplate + SchemaSrv.YANG_EXT;
	        String path = Conf.TEMPLATE_PATH;
	        long size = yangFileAttr.size();
		    Date lastModifiedTime = new Date(yangFileAttr.lastModifiedTime().toMillis());
		    String fileContent = new String(Files.readAllBytes(templateFile));
	        
		    String moduleName = regexService.getModuleName(fileContent);
		    String modulePrefix = regexService.getModulePrefix(fileContent);
		    String moduleNamespace = regexService.getModuleNamespace(fileContent);
		    Date moduleRevision = regexService.getModuleRevision(fileContent);
		    String moduleRevisionString = YangNode.formatDate(moduleRevision);
		    List<String> moduleImports = regexService.getModuleImports(fileContent);
		    Set<QName> moduleFeatures = regexService.getModuleFeatures(fileContent, moduleNamespace, moduleRevisionString);
		        
		    yangFile = new YangFile(0, name, path, size, lastModifiedTime, fileContent, moduleName, modulePrefix, moduleNamespace, moduleRevisionString, moduleImports, moduleFeatures);//Added file has always id=0
		    yangFile.setCreate(true);
		} catch (Exception ex) {
			LOG.error("getDefaultTemplateFile exception : ", ex);
			yangFile = null;
		}
		return yangFile;
   }

	
}
