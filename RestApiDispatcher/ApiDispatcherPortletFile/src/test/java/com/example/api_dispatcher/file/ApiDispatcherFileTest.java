package com.example.api_dispatcher.file;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.api_dispatcher.base.ApiDispatcherTestUnits;
import com.example.api_dispatcher.base.common.ApiCall;
import com.example.api_dispatcher.base.config.builder.ApiCallBuilder;
import com.example.api_dispatcher.base.config.builder.ApiCallConfigBuilder;
import com.example.api_dispatcher.base.service.impl.TestServiceImpl;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;
import com.example.api_dispatcher.base.variable.ApiVariable;
import com.example.api_dispatcher.base.variable.OutputApiVariable;
import com.example.api_dispatcher.file.ApiDispatcherFile;
import com.example.api_dispatcher.file.service.TestServiceFileHandle;
import com.example.api_dispatcher.file.service.impl.TestServiceFileHandleImpl;
import com.example.api_dispatcher.file.variable.InputApiVariable;

/**
 * API Dispatcher Tests
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestServiceImpl.class, TestServiceFileHandleImpl.class })
public class ApiDispatcherFileTest extends ApiDispatcherTestUnits {

	@Autowired
	private TestServiceFileHandle _service;

	private static ResourceRequest request;
	
	private static ResourceResponse response;
	
	// Test good file name
	private static final String TEST_FNAME_GOOD = "multipart_form_post.txt";
	
	// Test good upload file name
	private static final String TEST_FNAME_BAD = "multipart_form_no_file.txt";
	
	// Test good download file name
	private static final String TEST_DOWNLOAD_FILE = "test_file.txt";
	
	@BeforeClass
	public static void initMock() throws IOException {
		request = Mockito.mock(ResourceRequest.class);
		response = Mockito.mock(ResourceResponse.class);
		
		Mockito.when(request.getContentType()).
			thenReturn("multipart/form-data; boundary=----WebKitFormBoundarymt4LEzAmswuwXAvA");
		
		Mockito.when(request.getCharacterEncoding()).thenReturn("UTF-8");
	}
	
	public static void mockInputStream(String fname) throws IOException {
		File tfile = new File("src" + File.separator + "test" + File.separator + "resources" +
				File.separator + fname);
		
		InputStream inputStream = ApiDispatcherFileTest.class.getResourceAsStream("/" + fname);
		
		if (inputStream == null)
			fail("Test File test_file.txt not found");
		
		Mockito.when(request.getPortletInputStream()).thenReturn(inputStream);
		Mockito.when(request.getContentLength()).thenReturn((int) tfile.length());
	}
	
	@Before
	public void setUp() throws Exception {
		Map<String, Map<HttpMethod, List<ApiCall>>> config = getConfig();
		
		config.putAll(new ApiCallConfigBuilder()
			.addApi("file_upload")
				.addHttpMethod(HttpMethod.POST)
					.addApiCall(new ApiCallBuilder(_service, "testFileUpload")
						.addPath("ok")
							.addMethodVariable(new ApiVariable("int_var", Integer.class))
							.addMethodVariable(new ApiVariable("str_var", String.class))
							.addMethodVariable(new InputApiVariable())
						.build())
					.addApiCall(new ApiCallBuilder(_service, "testFileUpload")
						.addMethodVariable(new ApiVariable("int_var", Integer.class))
						.addMethodVariable(new ApiVariable("str_var", String.class))
						.addMethodVariable(new InputApiVariable(false))
						.build())
				.closeGroup()
			.addApi("file_download")
				.addHttpMethod(HttpMethod.GET)
					.addApiCall(new ApiCallBuilder(_service, "testFileDownload")
						.addMethodVariables(new ApiVariable[] {
								new ApiVariable("file_name", String.class),
								new OutputApiVariable()
						})
					.build())
				
			.build());
		
		setDispatcher(new ApiDispatcherFile(new File("target"), config));
	}

	@Test
	public void testFileUploadOk() throws ApiDispatcherException, IOException {
		mockInputStream(TEST_FNAME_GOOD);
		assertEquals("1962", getDispatcher().processRequest(request));
	}
	
	@Test
	public void testFileUploadBad() throws IOException {
		mockInputStream(TEST_FNAME_BAD);
		try {
			getDispatcher().processRequest(request);
			fail("ApiDispatcherException expected");
		} catch(ApiDispatcherException e) {
			assertEquals(19, e.getCode().intValue());
		}
	}
	
	@Test
	public void testFileDownloadOk() throws ApiDispatcherException, IOException {
		File tfile = new File("src" + File.separator + "test" + File.separator + "resources" +
				File.separator + TEST_DOWNLOAD_FILE);
		
		assertTrue("Test file '" + TEST_DOWNLOAD_FILE + "' not found.", tfile.exists());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Mockito.when(request.getParameter(getDispatcher().getReqParamName()))
			.thenReturn(encodeRequest("{" +
					"\"api\": \"file_download\"," +
					"\"method\": \"get\"," +
					"\"query_params\":{" + 
						"\"file_name\":\"" + TEST_DOWNLOAD_FILE + "\"" +
					"}" + 
				"}"));
		Mockito.when(response.getPortletOutputStream()).thenReturn(out);
		getDispatcher().processDownloadRequest(request, response);
		
		// Just compare the size of downloaded file
		assertEquals(tfile.length(), out.size());
	}
	
	@Override
	public ApiDispatcherFile getDispatcher() {
		return (ApiDispatcherFile) super.getDispatcher();
	}
}