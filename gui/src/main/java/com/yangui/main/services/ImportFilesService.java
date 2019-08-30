package com.yangui.main.services;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
@SuppressWarnings("deprecation")
public class ImportFilesService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImportFilesService.class);
	
	@Inject
	private FileService fileService;
	
	
	private void createImportPreview() {
		LOG.debug("createSchema");
	}
}