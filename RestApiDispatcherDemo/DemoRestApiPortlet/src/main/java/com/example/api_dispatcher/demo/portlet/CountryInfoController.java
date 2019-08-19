package com.example.api_dispatcher.demo.portlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.portlet.PortletContext;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import com.example.api_dispatcher.base.ApiDispatcher;
import com.example.api_dispatcher.base.common.ApiCall;
import com.example.api_dispatcher.base.config.builder.ApiCallBuilder;
import com.example.api_dispatcher.base.config.builder.ApiCallConfigBuilder;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;
import com.example.api_dispatcher.base.variable.ApiVariable;
import com.example.api_dispatcher.file.ApiDispatcherFile;
import com.example.api_dispatcher.demo.lib.country.dto.SearchParamsDto;
import com.example.api_dispatcher.demo.lib.country.service.CityService;
import com.example.api_dispatcher.demo.lib.country.service.CountryService;
import com.example.api_dispatcher.demo.lib.country.service.FileUploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Main Controller
 * 
 * @author Igor Peonte
 */
@Controller
@RequestMapping("VIEW")
public class CountryInfoController implements PortletContextAware{

	// Regular API Dispatcher library
	private ApiDispatcher _dispatcher1;
	
	// API Dispatcher library with file upload support
	private ApiDispatcherFile _dispatcher2;
	
	@Autowired
	private CityService _city_srv;
	
	@Autowired
	private CountryService _country_srv;
	
	@Autowired
	private FileUploadService _fupload;
	
	@PostConstruct
	public void initSimpleApiDispatchers() {
		
		ApiCallBuilder citySearchTemplateList = new ApiCallBuilder(_city_srv, "findCity")
				.addMethodVariables(new ApiVariable[] {
					new ApiVariable("country_code", String.class, false),
					new ApiVariable("region_code", String.class, false),
					new ApiVariable("search_params", SearchParamsDto.class, false)
				});
		
		ApiCallBuilder citySearchTemplateSearch = new ApiCallBuilder(_city_srv, "findCity")
				.addMethodVariables(new ApiVariable[] {
					new ApiVariable("country_code", String.class, false),
					new ApiVariable("region_code", String.class, false),
					new ApiVariable("search_params", SearchParamsDto.class, true)
				});
		
		_dispatcher1 = new ApiDispatcher(new ApiCallConfigBuilder()
			.addApi("countries")
				.addHttpMethod(HttpMethod.GET)
					.addApiCall(new ApiCall(_country_srv, "getCountryList"))
					.addApiCall(new ApiCallBuilder(_country_srv, "getCountryInfo")
						.addPath("{code:[A-Z]{2}}")
						.addMethodVariable(new ApiVariable("code", String.class))
					.build())
				.closeGroup()
			.addApi("cities")
				.addHttpMethod(HttpMethod.GET)
				.addApiCall(citySearchTemplateSearch
						.build())
					.addApiCall(citySearchTemplateList
						.addPath("{country_code:[A-Z]{2}}")
						.build())
					.addApiCall(citySearchTemplateList
						.addPath("{country_code:[A-Z]{2}}/{region_code:[A-Z]{2}}")
						.build())					
			.build());
	}
	
	private static final ObjectMapper _mapper = new ObjectMapper();

	static {
		_mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		_mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
	}
	
	@RenderMapping
	public String question(Model model) {
		model.addAttribute("reqParamName", _dispatcher1.getReqParamName());
		return "view";
	}

	@ResourceMapping("api_call")
	public void handleApiCall(ResourceRequest request, ResourceResponse response)
			throws IOException, ApiDispatcherException {
		// Retrieve request parameter
		response.getWriter().write(_dispatcher1.processRequest(
				request.getParameter(_dispatcher1.getReqParamName())));
	}
	
	@ResourceMapping("file_upload_call")
	public void handleFileUploadCall(ResourceRequest request, ResourceResponse response)
			throws IOException, ApiDispatcherException {
		// Dump request into external file
		// saveRequest(request);
		// Process uploaded file
		response.getWriter().write(_dispatcher2.processRequest(request));
	}
	
	@Override
	public void setPortletContext(PortletContext portletContext) {
		File tdir = (File) portletContext.getAttribute("javax.servlet.context.tempdir");
		
		_dispatcher2 = new ApiDispatcherFile(new File(tdir + File.separator + "files"), 
				new ApiCallConfigBuilder()
				.addApi("flag")
					.addHttpMethod(HttpMethod.POST)
						.addApiCall(new ApiCallBuilder(_fupload, "uploadFile")
								// String code, InputStream in
							.addPath("{code:[A-Z]{2}}")
							.addMethodVariable(new ApiVariable("code", String.class))
							.addMethodVariable(new ApiVariable("file", InputStream.class))
						.build())
				.build());
	}
	
	protected void saveRequest(ResourceRequest request) throws IOException {
		int cnt;
		byte[] buffer = new byte[1024];
		
		InputStream in = request.getPortletInputStream();
		File f = new File("request.txt");
		
		FileOutputStream out = new FileOutputStream(f);
		while((cnt = in.read(buffer)) >= 0) {
			System.out.println("== Writing " + cnt + " bytes into file " + f.getAbsolutePath());
			out.write(buffer, 0, cnt);
		}
		
		out.close();
		System.out.println("== Request successfully saved in file " + f.getAbsolutePath());
	}
	
	@ExceptionHandler(ApiDispatcherException.class)
	public void handleApiDispatcherException(ApiDispatcherException e, ResourceResponse response)
			throws JsonProcessingException, IOException {
		System.err.println("== " + e.getMessage());
		response.getWriter().write(_mapper.writeValueAsString(e));
	}
}