package com.example.api_dispatcher.base.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * DTO class for Api Call
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiCallDto {
	
	@NotEmpty
	private String api;
	
	@NotEmpty
	private String method;
	
	private String path;
	
	private String[] pathParams;

	private Map<String, Object> queryParams;
	
	private String body;
	
	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}
	
	public String[] getPathParams() {
		return pathParams;
	}

	public void setPathParams(String[] pathParams) {
		this.pathParams = pathParams;
		path = Arrays.asList(pathParams).stream().map(Object::toString)
				.collect(Collectors.joining("/"));
	}

	public Map<String, Object> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, Object> queryParams) {
		this.queryParams = queryParams;
	}

	public String toShortString() {
		return api + ":" + method + (path != null 
			? ":" + path : "") + 
		(queryParams != null 
			? ":" + queryParams.entrySet().stream().map(e -> e.getKey()+"=" + e.getValue())
					.collect(Collectors.joining("&"))
			: "");
	}
	
	@Override
	public String toString() {
		return toShortString() +
				(body != null ? ":body:" + body : "");
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
