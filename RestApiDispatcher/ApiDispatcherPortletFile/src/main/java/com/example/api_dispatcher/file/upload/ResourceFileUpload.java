package com.example.api_dispatcher.file.upload;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 * Resource File Upload handler
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

public class ResourceFileUpload extends FileUpload {

	public ResourceFileUpload(DiskFileItemFactory factory) {
		super(factory);
	}
}
