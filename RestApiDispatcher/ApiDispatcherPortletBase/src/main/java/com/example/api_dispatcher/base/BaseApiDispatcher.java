package com.example.api_dispatcher.base;

import java.util.List;
import java.util.Map;

import javax.portlet.ResourceRequest;

import org.springframework.http.HttpMethod;

import com.example.api_dispatcher.base.ApiDispatcher;
import com.example.api_dispatcher.base.common.*;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;

/**
 * API Request Dispatcher
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class BaseApiDispatcher extends ApiDispatcher {


  /**
   * Base constructor
   * 
   * @param config API Configuration
   */
  public BaseApiDispatcher(Map<String, Map<HttpMethod, List<ApiCall>>> config) {
    super(config);
  }
  
  /**
   * Main entry point for requiests without callback
   * 
   * @param request
   *            Encoded API Request
   * @return Process result
   * 
   * @throws ApiDispatcherException
   */
  public String processRequest(ResourceRequest req) throws ApiDispatcherException {
    return processRequest(req.getParameter(getReqParamName()));
  }
}
