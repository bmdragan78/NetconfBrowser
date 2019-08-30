package com.yangui.main.services;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opendaylight.yangtools.yang.common.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yangui.gui.Conf;
import com.yangui.main.services.model.ErrorItem;
import com.yangui.main.services.model.ErrorTypeEnum;
import com.yangui.main.services.model.ImportFile;
import com.yangui.main.services.model.ImportFileAction;
import com.yangui.main.services.model.ImportFileStatus;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.SchemaValidationStatus;
import com.yangui.main.services.model.YangFile;
import com.yangui.main.services.model.YangNode;
import com.yangui.main.services.model.YangRepo;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



@Singleton
public class FileService {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileService.class);
	
	@Inject
	private LogService logService;
	
	@Inject
	private RegexService regexService;
	  
	private FileWatcher fileWatcherSrv;
	
	private static Random rand = new Random();
	
	private List<YangFile> fileList = new ArrayList<YangFile>();
	  
	  
//	private StringProperty errorMsgProperty = new SimpleStringProperty();//set only when selectedItem=FileItem and read by MainPresenter to update Log Window line number
//	public StringProperty getErrorMsgProperty() {
//		return errorMsgProperty;
//	}
	    
		
	private StringProperty logProperty = new SimpleStringProperty();
	public StringProperty getLogProperty() {
		return logProperty;
	}
	  
	  
	@PostConstruct
	public void init () {
		Path path = Paths.get(Conf.LOGFILE_PATH);
		fileWatcherSrv = new FileWatcher(path.toFile());
		fileWatcherSrv.start();
	}
	  
	  
	//poll the log file and update UI
	private final class FileWatcher extends Thread {
		  
		private final File file;
		    
		private AtomicBoolean stop = new AtomicBoolean(false);

		public FileWatcher(File file) {
			this.file = file;
		}

		public boolean isStopped() { 
			return stop.get(); 
		}
		    
		public void stopThread() { 
			stop.set(true); 
		}

		public void doOnChange() {
			try {
				Path file = Paths.get("/" + Conf.LOGFILE_PATH);
				String fileContent = new String(Files.readAllBytes(file));
				Platform.runLater(() -> {
					logProperty.set(fileContent);
				});
		    }catch(Exception ex) {
		    	LOG.error("Error polling log file ", ex);
		    }
		}
		    
		@Override
		public void run() {
			try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
				Path path = file.toPath().getParent();
		        path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
		        while (!isStopped()) {
		        	WatchKey key;
		            try { 
		                key = watcher.poll(2000, TimeUnit.MILLISECONDS); 
		            }catch (InterruptedException e) {
		                return; 
		            }
		            if (key == null) { 
		            	Thread.yield(); 
		                continue; 
		            }
		            for (WatchEvent<?> event : key.pollEvents()) {
		            	WatchEvent.Kind<?> kind = event.kind();
		                WatchEvent<Path> ev = (WatchEvent<Path>) event;
		                Path filename = ev.context();
		                if (kind == StandardWatchEventKinds.OVERFLOW) {
		                	Thread.yield();
		                    continue;
		                } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY && filename.toString().equals(file.getName())) 
		                    doOnChange();
		                    
		                boolean valid = key.reset();
		                if (!valid)  
		                	break; 
		            }
		            Thread.yield();
		        }
			} catch (Throwable ex) {
		        LOG.error("Error polling log file ", ex);
		    }
		}
	}
	  
	  
	  public YangRepo getYangRepo() {//called when building the schema tree
		  YangRepo repo = new YangRepo();
		  try {
			  
			  String schemaFolder =  getSchemaFolder();
			  int fileCount = getFileCount();
			  long fileSize = getFilesSize();
			  repo = new YangRepo(schemaFolder, fileCount, fileSize);
		  }catch(Exception ex) {
		  }
		  return repo;
	  }
	  
	  
	  private String getSchemaFolder() {
			Path path = Paths.get(Conf.SCHEMA_FOLDER);
			return path.toAbsolutePath().toString();
	  }
	  
	  
	  private int getFileCount() {
		  int count = 0;
		  Path path = Paths.get(Conf.SCHEMA_FOLDER);
		  try (Stream<Path> files = Files.list(path)) {
			  count = (int) files.count();
		  }catch(Exception ex) {
		  }
		  return count;
	  }
	  
	  
	  private long getFilesSize() {
		  long size = 0L;
		  Path path = Paths.get(Conf.SCHEMA_FOLDER);
		  try (Stream<Path> files = Files.list(path)) {
			  Iterator<Path> iterator = files.iterator();
		      while (iterator.hasNext()) {
		    	  Path next = iterator.next();
		    	  size = size + next.toFile().length();
		      }
		  }catch(Exception ex) {
		  }
		  return size;
	  }
	  
	 
	  //Utility method used to return only the file name from a FS path
	  public static String getFileFromPath(Path file) {
		  return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf("."));
	  }

	  
	  //Returns the bean files by parsing the schema repository; Called from SchemaSrv.getSchemaTreeRoot() & SchemaSrv.getModulesTreeRoot() & this.updateCreateFile()
	  public synchronized List<YangFile> getFileObjects() throws Exception{
		  
		  //List<YangFile> fileList = new ArrayList<YangFile>();
		  Path dir = Paths.get(Conf.SCHEMA_FOLDER);
		  DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
			 
		  fileList.clear();
		  for (Path file: stream) {
			  BasicFileAttributes yangFileAttr = Files.readAttributes(file, BasicFileAttributes.class);
		      
			  String name = null;
			  try {
				  name = getFileFromPath(file);
			  }catch(StringIndexOutOfBoundsException ex) {//ignore non yang files
				  continue;
			  }
		      String path = file.toString();
		      long size = yangFileAttr.size();
		      Date lastModifiedTime = new Date(yangFileAttr.lastModifiedTime().toMillis());
		      String fileContent = new String(Files.readAllBytes(file));
		          
		      //apply regex on content to extract -> moduleName, moduleNamespace, revisionDate, imports & features
		      String moduleName = regexService.getModuleName(fileContent);
		      String modulePrefix = regexService.getModulePrefix(fileContent);
		      String moduleNamespace = regexService.getModuleNamespace(fileContent);
		      Date moduleRevision = regexService.getModuleRevision(fileContent);
		      String moduleRevisionString = YangNode.formatDate(moduleRevision);
		      List<String> moduleImports = regexService.getModuleImports(fileContent);
		      Set<QName> moduleFeatures = regexService.getModuleFeatures(fileContent, moduleNamespace, moduleRevisionString);
		      
		      //remove id=Rand when we implement DB persistence
		      YangFile yangFile = new YangFile(rand.nextInt(Integer.MAX_VALUE/1000), name, path, size, lastModifiedTime, fileContent, moduleName, modulePrefix, moduleNamespace, moduleRevisionString, moduleImports, moduleFeatures);
		      fileList.add(yangFile);
		  }
		  stream.close();
		  
		  return fileList;
	  }
	  
	  
	  //Deletes a yang file from schema folder 
	  public synchronized boolean deleteFile(SchemaItem schemaItem) throws Exception{
		  LOG.debug("Deleting file " + schemaItem.getFile().getPath());
		  Path path = Paths.get(schemaItem.getFile().getPath());
		  if(Files.deleteIfExists(path))							
			  return true;
		  else 														
			  return false;
	  }
	  
	  
	private YangFile searchFileItem(List<YangFile> yangFileList, Predicate<YangFile> predicate) {
		  
		for(YangFile file: yangFileList) {
			if(predicate.test(file)) {
				return file;
			}
		}
		return null;
	}
	  
	  //creates/updates SchemaItem file with newName & newContent
	  public synchronized SchemaItem updateCreateFile(SchemaItem schemaItem, String newName, String newContent) throws Exception{//called on save file
		  LOG.debug("Saving file " + schemaItem.getFile().getPath());
		  
		  YangFile yangFile = null;
		  List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		  try {
			  yangFile  = schemaItem.getFile();
			  
	          //validate fileName is present
			  if(newName == null || newName.trim().length() == 0) {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_NO_FILE_NAME);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_FILE_NAME.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
			  }
			  
			  //validate fileName is unique
	          String oldFileName = schemaItem.getFile().getName();
	          String newFileName = newName;
	          Path newFilePath = Paths.get(Conf.SCHEMA_FOLDER + File.separator + newFileName + YangNode.YANG_EXT);
	          int currentId = yangFile.getId();
	          YangFile duplicateFile = searchFileItem(this.fileList, (YangFile file) -> file.getId() != currentId && file.getName().equals(newFileName));
	          if(duplicateFile != null) {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_FILE_DUPLICATE);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_FILE_DUPLICATE.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
	          }
	          
			  //validate fileContent is present
			  if(newContent == null || newContent.trim().length() == 0) {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_NO_FILE_CONTENT);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_FILE_CONTENT.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
			  }
			  
			  //validate moduleName is present
			  String moduleName = regexService.getModuleName(newContent);
	          if(moduleName != null) {
	        	  schemaItem.getFile().setModuleName(moduleName);
	          }else {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_NO_MODULE);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_MODULE.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
	          }
	          //validate moduleName is unique
	          String currentModuleName = yangFile.getModuleName();
	          YangFile duplicateModule = searchFileItem(this.fileList, (YangFile file) -> file.getId() != currentId && file.getModuleName().equals(currentModuleName));
	          if(duplicateModule != null) {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_MODULE_DUPLICATE);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_MODULE_DUPLICATE.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
	          }
	          
	          //validate moduleNamespace is present
	          String moduleNamespace = regexService.getModuleNamespace(newContent);
	          if(moduleNamespace != null)
	        	  schemaItem.getFile().setModuleNamespace(moduleNamespace);
	          else {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_NO_NAMESPACE);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_NAMESPACE.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
	          }
	          //validate moduleNamespace is unique
	          String currentModuleNs = yangFile.getModuleNamespace();
	          YangFile duplicateNs = searchFileItem(this.fileList, (YangFile file) -> file.getId() != currentId && file.getModuleNamespace().equals(currentModuleNs));
	          if(duplicateNs != null) {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_NAMESPACE_DUPLICATE);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NAMESPACE_DUPLICATE.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
	          }
	          
	          //validate moduleRevision is present( This is always present since we return a default revision date for modules without revision date )
	          String moduleRevisionFormat = null;
	          Date moduleRevision = regexService.getModuleRevision(newContent);
	          schemaItem.getFile().setModuleRevision(YangNode.formatDate(moduleRevision));
	          if(moduleRevision != null)
	        	  moduleRevisionFormat = YangNode.formatDate(moduleRevision);
	          else {
	        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_NO_MOD_REVISION);
				  schemaItem.setErrorLineNumber(0);
				  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_NO_MOD_REVISION.text() + " [at null:0:0]";
				  schemaItem.setErrorMsg(errorMsg);
				  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", errorMsg));
				  updateErrors(errorList);
	        	  return schemaItem;
	          }
			  
	          if(errorList.size() == 0) {
		          //update content properties on YangFile bean
				  schemaItem.getFile().setContent(newContent);		
				  schemaItem.getFile().setSize(newContent.length());
		          schemaItem.getFile().setLastModifiedTime(new Date());
		          
		          //save file name and content by recreating the file
			      boolean wasPresent = Files.deleteIfExists(newFilePath);
			      newFilePath = Files.createFile(newFilePath);
		          byte[] newContentBytes = newContent.getBytes();
		          Files.write(newFilePath, newContentBytes);
		
		          //update file name properties on YangFile bean
		          schemaItem.getFile().setName(newFileName);
		          schemaItem.getFile().setPath(newFilePath.toString());
		          
		          //reset create flag since this is now an update
		          schemaItem.getFile().setCreate(false);
		          
		          schemaItem.setStatusEnum(SchemaValidationStatus.OK);
	          }
		  }catch(Exception ex) {
        	  schemaItem.setStatusEnum(SchemaValidationStatus.ERR_IO);
			  schemaItem.setErrorLineNumber(0);
			  String errorMsg = "Exception in File: " + yangFile.getName() + " Message: " + SchemaValidationStatus.ERR_IO.text() + " [at null:0:0]";
			  schemaItem.setErrorMsg(errorMsg);
        	  //return schemaItem;
        	  LOG.error("", ex);
	  		  errorList.clear();
	  		  errorList.add(new ErrorItem(ErrorTypeEnum.SCHEMA, yangFile.getIdString(), "0", "Check Error Window"));
		  }
		  updateErrors(errorList);
          
          return schemaItem;
	  }
	  
	  
	  private void updateErrors(List<ErrorItem> errorList) {
		  Platform.runLater(() -> {
				if(errorList.size() > 0) {
					logService.updateSchemaErrors(errorList);
					LOG.error("Saving file returned errors");
					errorList.stream().forEach(x -> {
						LOG.error(x.toString());
					});
				}else {
					logService.clearSchemaErrors();
					LOG.error("Saving file finished ok");
				}
			});
	  }
	  
	  
	  //Populates import files report table importAction; Called only from ImportPresenter.importAction() 
	  public synchronized List<ImportFile> createImportReport(List<ImportFile> inputFiles){
		  
		  List<ImportFile> importFiles = new ArrayList<>(inputFiles.size()); 
		  Path repoDir = Paths.get(Conf.SCHEMA_FOLDER);
	      for (ImportFile file: inputFiles) {
	    	  
	    	  ImportFile importFile = file.copy();
	    	  try {
	    		    //build imported file path in schema repository
	    		  	Path importedFilePath = repoDir.resolve(file.getDstName());
	    		  	ImportFileAction actionEnum = ImportFileAction.getInstance(file.getAction()); 
	    		  	switch (actionEnum)
	        	    {
	        	      case LEAVE:
	        	    	  	importFile.setStatus(ImportFileStatus.REPORT_LEAVE.text());
	        	    	  	break;															//unmodified
	        	      case OVERWRITE:														//delete & create & write file
	        	    	    if(Files.deleteIfExists(importedFilePath))	{			
	  	        			  	importedFilePath = Files.createFile(importedFilePath);
		  		            	byte[] newContentBytes = file.getContent().getBytes();
		  		            	Files.write(importedFilePath, newContentBytes);
		  		            	importFile.setStatus(ImportFileStatus.REPORT_OVERWRITE.text());
	        	    	    }else {//NEVER
	        	    	    	importFile.setStatus(ImportFileStatus.ERR_FILE_IO.text());
	        	    	    }
	        	    	    break;
	        	      case COPY:															//create & write file
	        	    	  	importedFilePath = Files.createFile(importedFilePath);
	  		            	byte[] newContentBytes = file.getContent().getBytes();
	  		            	Files.write(importedFilePath, newContentBytes);
	        	    	    importFile.setStatus(ImportFileStatus.REPORT_COPY.text());
	  		            	break;
	        	      default:        
	        	    	  break;
	        	    }
		            importFiles.add(importFile);
	    	  } catch (Exception e) {
	    		  e.printStackTrace();
	    		  LOG.error("createImportReport exception : ", e);
	    		  importFile.setStatus(ImportFileStatus.ERR_FILE_IO.text());
	    	  }
	      }
	      return importFiles;
	  }
	  
	  
	  //Populates import files preview table; Called only from ImportPresenter.browseAction() 
	  public List<ImportFile> createImportPreview(List<File> inputFiles){
		  
		  LOG.trace("createImportPreview");
		  List<ImportFile> importFiles = new ArrayList<>(inputFiles.size()); 
		  
	      for (File file: inputFiles) {
	    	  ImportFile importFile = new ImportFile();
	    	  
	    	  try {
	    		  importFile.setSrcName(file.getName());
	    		  importFile.setDstName("");
	    		  
	    		  String fileContent = new String(Files.readAllBytes(file.toPath()));
	    		  if(fileContent.trim().length() > 0) {
	    			  
	    			  importFile.setContent(fileContent);
	    			  
	    			  //validate basic Yang info on file content
	    			  String moduleName = regexService.getModuleName(fileContent);
	    			  if(moduleName == null) {
	    				  importFile.setStatus(ImportFileStatus.ERR_NO_NAME.text());
	    				  importFile.setAction(ImportFileAction.LEAVE.text());//Error-> LEAVE, OK-> (Overwrite || Copy)
	    				  importFiles.add(importFile);
	    				  continue;
	    			  }
			          String moduleNamespace = regexService.getModuleNamespace(fileContent);
			          if(moduleNamespace == null) {
	    				  importFile.setStatus(ImportFileStatus.ERR_NO_NAMESPACE.text());
	    				  importFile.setAction(ImportFileAction.LEAVE.text());
	    				  importFiles.add(importFile);
	    				  continue;
	    			  }
			          Date moduleRevision = regexService.getModuleRevision(fileContent);
			          if(moduleRevision == null) {
	    				  importFile.setStatus(ImportFileStatus.ERR_NO_REVISION.text());
	    				  importFile.setAction(ImportFileAction.LEAVE.text());
	    				  importFiles.add(importFile);
	    				  continue;
	    			  }
			          importFile.setDstName(moduleName + YangNode.NS_REV_FILE_SEP + YangNode.formatDate(moduleRevision) + YangNode.YANG_EXT);

			          //Search importFile in SchemaRepo -> if (found) {status=VALID_DUPLICATE && action=Overwrite} else {status=VALID_UNIQUE && action=Copy}
			          boolean isAlreadyInRepo = false;
			          List<String> repoFileNames = getFileNames();
			          for (String fileName: repoFileNames) {
			        	LOG.debug("createImportPreview_fileName : " + (fileName + YangNode.YANG_EXT) + " importFile.getDstName() : " + importFile.getDstName());
				    	if((fileName + YangNode.YANG_EXT).equalsIgnoreCase(importFile.getDstName())) { 
				    		isAlreadyInRepo = true;
				    		break;
				    	}
				      }
			          if(isAlreadyInRepo) {
			        	  importFile.setStatus(ImportFileStatus.VALID_DUPLICATE.text());
				          importFile.setAction(ImportFileAction.OVERWRITE.text());
			          }else {
			        	  importFile.setStatus(ImportFileStatus.VALID_UNIQUE.text());
				          importFile.setAction(ImportFileAction.COPY.text());
			          }
			          importFiles.add(importFile);
	    		  }else {
	    			  importFile.setStatus(ImportFileStatus.ERR_EMPTY.text());
	    			  importFile.setAction(ImportFileAction.LEAVE.text());
	    			  importFiles.add(importFile);
	    			  continue;
	    		  }
	    	  } catch (Exception e) {
	    		  e.printStackTrace();
	    		  LOG.error("createImportPreview exception : ", e);
	    		  importFile.setStatus(ImportFileStatus.ERR_FILE_IO.text());
	    	  }
	      }
	      //Search importFile in the other importFiles -> if (found) {status=ERR_DUPLICATE_IMPORT && action=Leave}
          for (ImportFile importFile : importFiles) {
        	  int count = 0;
        	  String dstFileName = importFile.getDstName();
        	  for (ImportFile importFileA : importFiles) {
        		  if(dstFileName.equals(importFileA.getDstName())) {
        			  count++;
        			  LOG.trace("Match FOUND : count = " + count + " for file : " + dstFileName);
        		  }
        	  }
        	  if(count > 1) {
        		  importFile.setStatus(ImportFileStatus.ERR_DUPLICATE_IMPORT.text());
		          importFile.setAction(ImportFileAction.LEAVE.text());
        	  }
          }
	      return importFiles;
	  }
	  
	  
	  //Returns the names of all files in the schema repository ; Called only from ImportPresenter.preview() 
	  private List<String> getFileNames() {
		  
		  LOG.trace("getFileNames");
		  List<String> fileNames = new ArrayList<String>();
		  Path dir = Paths.get(Conf.SCHEMA_FOLDER);
		  try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		      for (Path file: stream) {
		    	  String name = null;
				  try {
					  name = getFileFromPath(file);
				  }catch(StringIndexOutOfBoundsException ex) {//ignore non yang files
					  continue;
				  }
		    	  LOG.trace("		fileName= " + name);
		    	  fileNames.add(name);
		      }
		  } catch (Exception ex) {
			  LOG.error("getFileNames exception : ", ex);
		  }
		  return fileNames;
	  }
	  
	  
}
