package com.example.api_dispatcher.base.config.builder;

import java.util.*;

import org.springframework.http.HttpMethod;

import com.example.api_dispatcher.base.common.ApiCall;

/**
 * API Call Method Builder
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiCallMethodBuilder {
	private ApiCallGroupBuilder _parent;
	private List<ApiCall> _list = new ArrayList<>();
	
	public ApiCallMethodBuilder(ApiCallGroupBuilder parent, List<ApiCall> list) {
		_parent = parent;
		_list = list;
	}
	
	public ApiCallMethodBuilder addApiCall(ApiCall apiCall) {
		_list.add(apiCall);
		return this;
	}
	
	public ApiCallGroupBuilder and() {
		return _parent;
	}
	
	public Map<String, Map<HttpMethod, List<ApiCall>>> build() { 
		return _parent.getParent().build();
	}
	
	public ApiCallConfigBuilder closeGroup() {
		return _parent.getParent();
	}
}
