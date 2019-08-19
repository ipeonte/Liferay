package com.example.api_dispatcher.file.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public interface TestServiceFileHandle {

	Long testFileUpload(Integer intVar, String strVar, InputStream file) throws Exception;
	
	Path testFileDownload(String fname, OutputStream out) throws Exception;
}
