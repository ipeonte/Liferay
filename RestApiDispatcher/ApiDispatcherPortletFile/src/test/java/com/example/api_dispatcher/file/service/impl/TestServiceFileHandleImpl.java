package com.example.api_dispatcher.file.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.example.api_dispatcher.file.service.TestServiceFileHandle;

@Service
public class TestServiceFileHandleImpl implements TestServiceFileHandle {

	@Override
	public Long testFileUpload(Integer intVar, String strVar, InputStream in) throws Exception {
		// Read file
		final byte[] buffer = new byte[1024];
		long size = 0;
		int bytes;
		while ((bytes = in.read(buffer, 0, buffer.length)) >= 0)
		    size += bytes;
		
		// Return the total size of all 3 parameters
		return intVar + strVar.length() + size;
	}

	@Override
	public Path testFileDownload(String fname, OutputStream out) throws Exception {
		URL url = this.getClass().getClassLoader().getResource(fname);
		if (url == null)
			throw new FileNotFoundException("File '" + fname + "' not found");
		
		Path path = new File(url.toURI()).toPath();
		
		if (out == null)
			// Return only file path
			return path;
		
		// Send file into output stream
		Files.copy(path, out);
				
		return null;
	}

}
