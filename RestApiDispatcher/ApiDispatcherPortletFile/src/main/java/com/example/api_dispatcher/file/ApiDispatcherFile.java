package com.example.api_dispatcher.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.util.StringUtils;

import com.example.api_dispatcher.base.BaseApiDispatcher;
import com.example.api_dispatcher.base.common.ApiCall;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;
import com.example.api_dispatcher.file.upload.ResourceFileUpload;
import com.example.api_dispatcher.file.upload.ResourceUploadContext;

/**
 * API Request Dispatcher with File Upload support
 * Reserved Api Exception codes [26 - 50]
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiDispatcherFile extends BaseApiDispatcher {

	// Name of file parameter
	public static final String FNAME = "file";
	
	// Create a factory for disk-based file items
	DiskFileItemFactory factory = new DiskFileItemFactory();

	// Configured name of filename upload parameter
	private String fileParamName = FNAME;
	
	// Logger
	private Logger _log = LoggerFactory.getLogger(ApiDispatcherFile.class);


	public ApiDispatcherFile(File workDir, Map<String, Map<HttpMethod, List<ApiCall>>> config) {
		super(config);
		init(workDir);
	}

	/**
	 * Process API request with file download
	 * 
	 * @param req ResourceRequest
	 * @param resp ResourceResponse
	 * 
	 * @throws ApiDispatcherException
	 */
	public void processDownloadRequest(ResourceRequest req,
							ResourceResponse resp) throws ApiDispatcherException {
		
		// API Request
		String request = req.getParameter(getReqParamName());
		Path path = (Path) processRequestRaw(request);
		
		try {
			String mtype = Files.probeContentType(path);
			if (mtype != null)
				resp.setContentType(Files.probeContentType(path));
		} catch (IOException e) {
			throw new ApiDispatcherException(30, "Unable detect mime type for file  '" + 
					path.toAbsolutePath() + "'", e);
		}
		
		resp.setProperty(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=" + path.getFileName());
		
		try {
			// Repeat same but now with output stream
			processRequest(request, resp.getPortletOutputStream());
		} catch (IOException e) {
			throw new ApiDispatcherException(31, "Error sending file to client.", e);
		}
	}
	
	public String processRequest(ResourceRequest req) throws ApiDispatcherException {
		return processRequest(req, null);
	}
	
	public String processRequest(ResourceRequest req, OutputStream out) throws ApiDispatcherException {
		// Check that we have a file upload request
		ResourceUploadContext ctx = new ResourceUploadContext(req);

		if (FileUploadBase.isMultipartContent(ctx)) {
		
			ResourceFileUpload upload = new ResourceFileUpload(factory);
			List<FileItem> items;
			try {
				items = upload.parseRequest(ctx);
			} catch (FileUploadException e) {
				throw new ApiDispatcherException(26, "Upload Request parsing error", e);
			}
			
			// Find
			InputStream fh = null;
			String apir = null;
			for (FileItem item: items) {
				if (item.isFormField()) {
					if (item.getFieldName().equals(getReqParamName()))
						apir = item.getString();
					else
						_log.warn("Expected request field '" + getReqParamName() + "' but found '" +
								item.getFieldName() + "'");
				} else {
					if (item.getFieldName().equals(getFileParamName()))
						try {
							fh = item.getInputStream();
						} catch (IOException e) {
							throw new ApiDispatcherException(27, "Upload file read error", e);
						}
					else
						_log.warn("Expected file field '" + getFileParamName() + "' but found '" +
								item.getFieldName() + "'");
				}
			}
			
			// Check if request found
			if (StringUtils.isEmpty(apir))
				throw new ApiDispatcherException(32, "Api Request not fount in multipart form");

			final InputStream fileHandle = fh;
			return fileHandle != null ? processRequest(apir, (ac) -> {
				if (ac.getQueryParams() == null)
					ac.setQueryParams(new HashMap<>());
				if (ac.getQueryParams().containsKey(getFileParamName()))
					throw new ApiDispatcherException(28, "Query parameters already have field name '" +
							getFileParamName());
				ac.getQueryParams().put(getFileParamName(), fileHandle);
			}, out) : processRequest(apir, out);
		} else {
			// Call regular API Dispatcher
			return processRequest(req.getParameter(getReqParamName()));
		}
	}

	/**
	 * Download factory initialization. Designed to use in a bean and/or portlet initialization section
	 * 
	 * @param workDir working directory to store uploaded files.
	 */
	private void init(File workDir) {
		if (workDir.exists()) {
			if (workDir.isDirectory()) {
				_log.info("Found existing upload directory " + workDir.getAbsolutePath());
			} else if (!workDir.delete()) {
				String err = "Unable delete existing file" + workDir.getAbsolutePath();
				_log.error(err);
				throw new RuntimeException(err);
			}
		} else {
			if (!workDir.mkdir()) {
				_log.error("Error creating " + workDir);
				throw new RuntimeException("Error creating " + workDir);
			}

			_log.info("Successfully created new upload directory " + workDir.getAbsolutePath());
		}
		
		_log.info("Set upload directory " + workDir.getAbsolutePath());
		factory.setRepository(workDir);
	}
	
	public String getFileParamName() {
		return fileParamName;
	}

	public void setFileParamName(String fileParamName) {
		this.fileParamName = fileParamName;
	}
	
}

