package com.example.api_dispatcher.base.common;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.example.api_dispatcher.base.variable.ApiVariable;

/**
 * Class for Api Call configuration
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiCall {

	private final Object service;

	private final String methodName;
	
	// Additional path
	private String path;
	
	// Body parameter. Needs to be mapped later
	private String body;
	
	// List of method variables. It must exactly correspond the signature of
	// serviceClass.methodName function
	private ApiVariable[] methodVariables;
	
	public ApiCall(Object service, String methodName) {
		this.service = service;
		this.methodName = methodName;
	}
	
	public ApiCall(Object service, String methodName, String path) {
		this(service, methodName);
		this.path = path;
	}
	
	public ApiCall(Object service, String methodName, String path,
			ApiVariable[] methodVariables) {
		this(service, methodName, path);
		this.methodVariables = methodVariables;
	}
	
	public Object getService() {
		return service;
	}

	public String getPath() {
		return path;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public ApiVariable[] getMethodVariables() {
		return methodVariables;
	}
	
	public void setMethodVariables(ApiVariable[] methodVariables) {
		this.methodVariables = methodVariables;
	}
	
	@Override
	public String toString() {
		return (service != null ? service.getClass().getName() : null) + ":" + methodName +
			(path != null ? ":" + path : "") +
			(methodVariables != null ? ":" + Arrays.asList(methodVariables)
			.stream().map(Object::toString)
			.collect(Collectors.joining("|")) : "") + 
		(body != null ? ":[" + body + "]" : "");
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
