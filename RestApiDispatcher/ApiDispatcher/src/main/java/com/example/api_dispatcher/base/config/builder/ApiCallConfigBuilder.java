package com.example.api_dispatcher.base.config.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.example.api_dispatcher.base.common.ApiCall;

/**
 * API Call Configuration Builder
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiCallConfigBuilder {

  // Final group
  private final Map<String, Map<HttpMethod, List<ApiCall>>> _config;

  /**
   * Default constractor
   */
  public ApiCallConfigBuilder() {
    _config = new HashMap<>();
  }

  /**
   * Constractor with some initialized configuration
   * 
   * @param config Existing API configuration
   */
  public ApiCallConfigBuilder(
      Map<String, Map<HttpMethod, List<ApiCall>>> config) {
    _config = config;
  }

  public ApiCallGroupBuilder addApi(String name) {
    // Check if method already exist
    Map<HttpMethod, List<ApiCall>> group = _config.get(name);
    if (group == null)
      group = new HashMap<>();

    _config.put(name, group);
    return new ApiCallGroupBuilder(this, group);
  }

  public Map<String, Map<HttpMethod, List<ApiCall>>> build() {
    return _config;
  }
}
