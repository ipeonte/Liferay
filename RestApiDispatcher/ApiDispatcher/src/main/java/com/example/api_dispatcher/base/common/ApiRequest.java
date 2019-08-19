package com.example.api_dispatcher.base.common;

import java.io.OutputStream;

import com.example.api_dispatcher.base.dto.ApiCallDto;
import com.example.api_dispatcher.base.variable.ApiVariable;

/**
 * Internal class to handle each API Request
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiRequest {

	// ApiCall specification
	private final ApiCall apiCall;
	
	// Input DTO
	private final ApiCallDto apiCallDto;
	
	// Method arguments
	private final Object[] arguments;
	
	// Output stream
	private OutputStream output;
	
	public ApiRequest(ApiCall apiCall, ApiCallDto apiCallDto) {
		this.apiCall = apiCall;
		this.apiCallDto = apiCallDto;
		
		// Allocate arguments
		ApiVariable[] vars = apiCall.getMethodVariables();
		arguments = (vars != null && vars.length > 0) ? new Object[vars.length] : null;
	}
	
	public void addArgument(int idx, Object value) {
		arguments[idx] = value;
	}
	
	public ApiCall getApiCall() {
		return apiCall;
	}
	
	public ApiCallDto getApiCallDto() {
		return apiCallDto;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}
}
