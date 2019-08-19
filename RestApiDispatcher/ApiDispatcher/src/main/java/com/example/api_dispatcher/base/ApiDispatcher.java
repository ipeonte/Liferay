package com.example.api_dispatcher.base;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import com.example.api_dispatcher.base.common.*;
import com.example.api_dispatcher.base.dto.ApiCallDto;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;
import com.example.api_dispatcher.base.variable.ApiVariable;
import com.example.api_dispatcher.base.variable.OutputApiVariable;
import com.example.api_dispatcher.base.variable.RequestMapApiVariable;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * API Request Dispatcher
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiDispatcher {

  // Global name of Request Parameter
  private static final String API_REQ_PARAM_NAME = "rp";

  // Logger
  static Logger _log = LoggerFactory.getLogger(ApiDispatcher.class);

  // Path matcher
  private final AntPathMatcher matcher = new AntPathMatcher();

  // Dispatcher configuration
  private final Map<String, Map<HttpMethod, List<ApiCall>>> _config;

  // Methods cache
  private Map<String, Method> _methods = new ConcurrentHashMap<>();

  // Name of run-time request parameter
  private String reqParamName = API_REQ_PARAM_NAME;

  public static final ObjectMapper MAPPER = new ObjectMapper();
  private static final ValidatorFactory _vfactory =
      Validation.buildDefaultValidatorFactory();

  static {
    MAPPER.setSerializationInclusion(Include.NON_NULL);
    MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Base constructor
   * 
   * @param config API Configuration
   */
  public ApiDispatcher(Map<String, Map<HttpMethod, List<ApiCall>>> config) {
    _config = config;
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
  public String processRequest(String request) throws ApiDispatcherException {
    return processRequest(request, null);
  }

  /**
   * Main entry point for requests without callback that return JSON result
   * 
   * @param request
   *            Encoded API Request
   * 
   * @return String JSON result
   * 
   * @throws ApiDispatcherException
   */
  public String processRequest(String request, OutputStream out)
      throws ApiDispatcherException {
    return processRequest(request, null, out);
  }

  /**
   * Main entry point for requests with callback that return JSON result
   * 
   * @param request
   *            Encoded API Request
   * @param callback
   *            Callback on API Request parse event
   * 
   * @return String JSON result
   * 
   * @throws ApiDispatcherException
   */
  public String processRequest(String request, OnApiDtoParseCallback callback,
      OutputStream out) throws ApiDispatcherException {
    try {
      Object result = processRequestRaw(request, callback, out);
      return MAPPER.writeValueAsString(result != null ? result : "");
    } catch (JsonProcessingException e) {
      throw new ApiDispatcherException(4, "Error converting final object", e);
    }
  }

  /**
   * Main entry point for requests without callback that returns raw object from processing
   * 
   * @param request
   *            Encoded API Request
   *            
   * @return Object result
   * 
   * @throws ApiDispatcherException
   */
  public Object processRequestRaw(String request)
      throws ApiDispatcherException {
    return processRequestRaw(request, null, null);
  }

  /**
   * Main entry point for requests with callback that returns raw object from processing
   * 
   * @param request
   *            Encoded API Request
   * @param callback
   *            Callback on API Request parse event
   * 
   * @return Object result
   * 
   * @throws ApiDispatcherException
   */
  public Object processRequestRaw(String request,
      OnApiDtoParseCallback callback, OutputStream out)
      throws ApiDispatcherException {
    // 0. Null check
    if (StringUtils.isEmpty(request))
      throw new ApiDispatcherException(1, "Api Request is empty");

    // 1. Read & decode API Request
    String req = new String(Base64.getDecoder().decode(request));
    _log.trace("Orig Request: " + request);

    ApiCallDto ac = readValidateValue(req, ApiCallDto.class, "API Request");

    if (callback != null)
      callback.postProcess(ac);

    _log.trace("Mapped & Validated request to API Call: " + ac);

    return processRequest(ac, out);
  }

  /**
   * Main entry point for requests without callback that returns raw object from processing
   * 
   * @param request
   *            Encoded API Request
   * 
   * @return Object result
   * 
   * @throws ApiDispatcherException
   */
  protected Object processRequest(ApiCallDto req)
      throws ApiDispatcherException {
    return processRequest(req, null);
  }

  protected Object processRequest(ApiCallDto req, OutputStream out)
      throws ApiDispatcherException {
    _log.debug("Processing api request: " + req);

    ApiRequest request = parseRequest(req);
    request.setOutput(out);
    setArguments(request);

    return invokeServiceMethod(request);
  }

  @SuppressWarnings("unchecked")
  public static <T> T readValidateValue(String value, Class<?> T, String msg)
      throws ApiDispatcherException {
    // 0. Null check
    if (StringUtils.isEmpty(value))
      throw new ApiDispatcherException(20, msg + " is empty");

    // 1. Try JSON->Object map
    T result;
    try {
      result = (T) MAPPER.readValue(value, T);
      _log.trace("Mapped [" + value + "] -> [" + result + "]");
    } catch (IOException e) {
      throw new ApiDispatcherException(2, msg + " mapping error", e);
    }

    Set<ConstraintViolation<T>> violations;

    // 2. Validate object
    try {
      violations = _vfactory.getValidator().validate(result);
    } catch (RuntimeException e) {
      throw new ApiDispatcherException(3, msg + " validation error", e);
    }

    int cnt = violations.size();
    if (cnt > 0) {
      String err = "Found " + cnt + " error" + (cnt > 1 ? "s" : "") +
          " during validation";
      _log.error(err);
      List<String> details = new ArrayList<>();
      details.add(err);
      violations.forEach(u -> details
          .add(u.getPropertyPath().toString() + " " + u.getMessage()));

      throw new ApiDispatcherException(4, msg + " validation error",
          details.toArray(new String[details.size()]));
    }

    return result;
  }

  private ApiRequest parseRequest(ApiCallDto req)
      throws ApiDispatcherException {
    // 1. Look for api call configuration
    Map<HttpMethod, List<ApiCall>> group = _config.get(req.getApi());
    if (group == null)
      throw new ApiDispatcherException(5,
          "Configuration for api [" + req.getApi() + "] is not defined");

    HttpMethod method;
    try {
      method = HttpMethod.valueOf(req.getMethod().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ApiDispatcherException(6, e, "Error converting http method [" +
          req.getMethod().toUpperCase() + "] into Http Method");
    }

    // Get group of API Calls configured
    List<ApiCall> calls = group.get(method);
    if (calls == null)
      throw new ApiDispatcherException(7, "Configuration for api call [" +
          req.getApi() + ":" + req.getMethod() + "] is not defined");

    // Check if any API calls configured
    if (calls == null || calls.size() == 0)
      throw new ApiDispatcherException(8, "Configuration for api call [" +
          req.getApi() + ":" + req.getMethod() + "] is empty");

    _log.debug("Found group of " + calls.size() +
        " api calls for http method " + req.getMethod());

    // 2. Find API Call that matches request
    ApiCall api = null;

    for (ApiCall ac : calls) {
      if (ac.getPath() != null) {
        if (req.getPath() == null)
          continue;

        boolean fm = matcher.match(ac.getPath(), req.getPath());

        _log.debug("Trying match api pattern " + ac.getPath() + " with " +
            req.getPath() + " - " + fm);
        if (!fm)
          continue;
      } else if (req.getPath() != null) {
        continue;
      }

      // Check for duplicated match
      if (api != null)
        // Found second match
        throw new ApiDispatcherException(9, "Second match found",
            new String[] { req.getPath(), ac.getPath() });

      _log.debug("First Match found " + req + " -> " + ac);
      api = ac;
    }

    if (api == null)
      throw new ApiDispatcherException(10,
          "No API matching [" + req + "] found in configuration");

    return new ApiRequest(api, req);
  }

  /**
   * Set arguments for method invocation
   * 
   * @param request ApiRequest object
   * @throws ApiDispatcherException
   */
  private void setArguments(ApiRequest request) throws ApiDispatcherException {
    ApiCall api = request.getApiCall();
    ApiCallDto req = request.getApiCallDto();

    // Check if variable needs to be extracted
    if (api.getMethodVariables() != null) {
      // List of variables that included into path
      Map<String, String> pvars;

      if (api.getPath() != null) {
        try {
          pvars =
              matcher.extractUriTemplateVariables(api.getPath(), req.getPath());
        } catch (IllegalStateException e) {
          throw new ApiDispatcherException(11,
              "Error extracting path variables", e);
        }
      } else {
        pvars = new HashMap<>();
      }

      for (int i = 0; i < api.getMethodVariables().length; i++) {
        ApiVariable rvar = api.getMethodVariables()[i];

        Object arg = null;

        // Check for In/Out variables
        if (OutputApiVariable.CLAZZ.isAssignableFrom(rvar.getVarClass())) {
          arg = request.getOutput();
        } else if (RequestMapApiVariable.CLAZZ
            .isAssignableFrom(rvar.getVarClass())) {
          // Check for request map
          arg = req.getQueryParams();
        } else if (rvar.isMappable()) {
          // Check for a body
          String body = req.getBody();
          if (body == null) {
            if (rvar.getRequired())
              throw new ApiDispatcherException(12,
                  "Body value is missing for mappable argument [" +
                      rvar.getName() + "]");
            // If not required use default value
            arg = rvar.getDefValue();
          } else if ("".equals(body)) {
            throw new ApiDispatcherException(13,
                "Body value is empty for mappable argument [" + rvar.getName() +
                    "]");
          } else {
            // Map request JSON into object
            arg = rvar.getMappable().convert(body, rvar.getVarClass(),
                "Parameter #" + i + ":" + rvar.getName());
          }
        } else {

          Object value = pvars.get(rvar.getName());

          if (value == null) {
            // If not path then look in query variables
            if (req.getQueryParams() != null &&
                req.getQueryParams().containsKey(rvar.getName())) {
              value = req.getQueryParams().get(rvar.getName());
              arg = initVariable(rvar, value);
            } else {
              if (rvar.getRequired())
                // Variable is not found but it could be optional
                throw new ApiDispatcherException(14,
                    "Variable [" + rvar.getName() + "] not found in path");
              // If not required use default value
              arg = rvar.getDefValue();
            }
          } else {
            // Instantiate variable into object
            arg = initVariable(rvar, value);
          }
        }

        if (arg != null)
          request.addArgument(i, arg);
      }
    }
  }

  private Object initVariable(ApiVariable variable, Object value)
      throws ApiDispatcherException {
    // Check if required class types and variable class type are the same
    Class<?> clazz = variable.getVarClass();
    if (clazz.isInstance(value))
      return value;

    Constructor<?> co;
    try {
      co = clazz.getConstructor(String.class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new ApiDispatcherException(15, "Variable with name [" +
          variable.getName() + "] and class " + variable.getClassName() +
          " doesn't have an appropriate constructor with single String parameter",
          e);
    }
    try {
      return co.newInstance(value);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new ApiDispatcherException(16,
          "Error converting variable [" + variable.getName() +
              "] with value [" + value + "] into " + variable.getClassName(),
          e);

    }
  }

  private Object invokeServiceMethod(ApiRequest request)
      throws ApiDispatcherException {
    // Invoke service
    // Look in a cache first
    ApiCall api = request.getApiCall();

    // Null check
    if (api.getService() == null)
      throw new ApiDispatcherException(17, "Service object is null");

    String cname = api.getService().getClass().getName();

    // Build array of argument class names
    Class<?>[] ptypes = null;

    if (api.getMethodVariables() != null &&
        request.getApiCall().getMethodVariables().length > 0) {
      ApiVariable[] arguments = api.getMethodVariables();
      int len = arguments.length;
      ptypes = new Class<?>[len];

      for (int i = 0; i < len; i++)
        ptypes[i] = arguments[i].getClassType();
    }

    String mkey =
        cname + ":" + api.getMethodName() +
            (ptypes != null ? "(" + Arrays.asList(ptypes).stream()
                .map(Object::toString).collect(Collectors.joining(",")) + ")"
                : "");

    Method method = _methods.get(mkey);
    if (method == null) {
      try {
        _log.trace(getMethodInvokeMsg(api, ptypes));
        method =
            api.getService().getClass().getMethod(api.getMethodName(), ptypes);
      } catch (NoSuchMethodException | SecurityException e) {
        throw new ApiDispatcherException(18, e,
            "Error " + getMethodInvokeMsg(api, ptypes));
      }

      _methods.put(mkey, method);
    }

    Object res;
    Object[] args = request.getArguments();

    try {
      res = method.invoke(api.getService(), args);
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new ApiDispatcherException(19, e, "Error invoking [" + cname + ":" +
          api.getMethodName() + "(" +
          (args != null ? "(" + Arrays.asList(args).stream()
              .map(o -> o != null ? o.getClass().getName() + "=" + o.toString()
                  : "null")
              .collect(Collectors.joining("),(")) + ")" : "") +
          ")] service");
    }

    return res;
  }

  private String getMethodInvokeMsg(ApiCall api, Class<?>[] ptypes) {
    return "Invoking Method [" + api.getMethodName() + "] with parameters [" +
        (ptypes != null ? Arrays.asList(ptypes).stream().map(Object::toString)
            .collect(Collectors.joining(",")) : "") +
        "] from service " + "[" + api.getService().getClass().getName() + "]";
  }

  public String getReqParamName() {
    return reqParamName;
  }

  public void setReqParamName(String reqParamName) {
    this.reqParamName = reqParamName;
  }

}
