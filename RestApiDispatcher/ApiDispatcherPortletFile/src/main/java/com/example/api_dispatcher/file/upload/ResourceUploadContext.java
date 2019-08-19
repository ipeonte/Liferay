package com.example.api_dispatcher.file.upload;

import java.io.IOException;
import java.io.InputStream;

import javax.portlet.ResourceRequest;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.UploadContext;

/**
 * Resource Upload Context
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

public class ResourceUploadContext implements UploadContext {

	private final ResourceRequest request;

	public ResourceUploadContext(ResourceRequest request) {
		this.request = request;
	}

	@Override
	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public int getContentLength() {
		return request.getContentLength();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return request.getPortletInputStream();
	}

	@Override
	public long contentLength() {
		long size;
        try {
            size = Long.parseLong(request.getProperty(FileUploadBase.CONTENT_LENGTH));
        } catch (NumberFormatException e) {
            size = request.getContentLength();
        }
        return size;
	}
}
