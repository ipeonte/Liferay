package com.example.api_dispatcher.base.config.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.example.api_dispatcher.base.common.ApiCall;

/**
 * API Call Group Builder
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiCallGroupBuilder {

	private final ApiCallConfigBuilder _parent;

	// Final group
	private Map<HttpMethod, List<ApiCall>> _group;

	public ApiCallGroupBuilder(ApiCallConfigBuilder parent, Map<HttpMethod, List<ApiCall>> group) {
		_parent = parent;
		_group = group;
	}

	public ApiCallMethodBuilder addHttpMethod(HttpMethod method) {
		// Check if method already exist
		List<ApiCall> list = _group.get(method);
		if (list == null)
			list = new ArrayList<>();

		_group.put(method, list);
		return new ApiCallMethodBuilder(this, list);
	}
	
	public ApiCallConfigBuilder and() {
		return _parent;
	}
	
	public ApiCallConfigBuilder getParent() {
		return _parent;
	}
}
