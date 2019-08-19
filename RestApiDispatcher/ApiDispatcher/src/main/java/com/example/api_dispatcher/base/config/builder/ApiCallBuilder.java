package com.example.api_dispatcher.base.config.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.api_dispatcher.base.common.ApiCall;
import com.example.api_dispatcher.base.variable.ApiVariable;

/**
 * API Call Builder
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiCallBuilder {

	private final Object service;

	private final String methodName;
	
	private String path;
	
	private String body;
	
	// List of method variables.
	private ApiVariable[] methodVariables;
	
	public ApiCallBuilder(Object service, String methodName) {
		this.service = service;
		this.methodName = methodName;
	}
	
	public ApiCallBuilder addPath(String path) {
		this.path = path;
		return this;
	}
	
	public ApiCallBuilder addMethodVariables(ApiVariable[] methodVariables) {
		this.methodVariables = methodVariables;
		return this;
	}
	
	public ApiCallBuilder addMethodVariable(ApiVariable apiVariable) {
		if (this.methodVariables == null)
			return addMethodVariables(new ApiVariable[] { apiVariable });
		
		// Append variable
		List<ApiVariable> list = new ArrayList<ApiVariable>( Arrays.asList(methodVariables));
		list.add(apiVariable);
		this.methodVariables = list.toArray(new ApiVariable[methodVariables.length]);
		return this;
	}
	
	public ApiCallBuilder addBody(String body) {
		this.body = body;
		return this;
	}
	
	public ApiCall build() {
		ApiCall req = new ApiCall(service, methodName);
		if (path != null)
			req.setPath(path);
		
		if (methodVariables != null)
			req.setMethodVariables(methodVariables);
		
		if (body != null)
			req.setBody(body);
		
		return req;
	}
}
