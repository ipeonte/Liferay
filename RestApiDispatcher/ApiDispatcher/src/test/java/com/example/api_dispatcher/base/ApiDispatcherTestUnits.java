package com.example.api_dispatcher.base;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import com.example.api_dispatcher.base.ApiDispatcher;
import com.example.api_dispatcher.base.common.ApiCall;
import com.example.api_dispatcher.base.config.builder.ApiCallBuilder;
import com.example.api_dispatcher.base.config.builder.ApiCallConfigBuilder;
import com.example.api_dispatcher.base.config.builder.ApiVariableBuilder;
import com.example.api_dispatcher.base.dto.ApiCallDto;
import com.example.api_dispatcher.base.service.TestService;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;
import com.example.api_dispatcher.base.variable.ApiVariable;
import com.example.api_dispatcher.base.variable.OutputApiVariable;
import com.example.api_dispatcher.base.variable.RequestMapApiVariable;
import com.example.api_dispatcher.base.ApiDispatcherTestUnits;
import com.example.api_dispatcher.base.TestBean;

/**
 * API Dispatcher Test Units
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiDispatcherTestUnits {

	// Logger
	private Logger _log = LoggerFactory.getLogger(ApiDispatcherTestUnits.class);
			
	// Api Dispatcher Handle
	private ApiDispatcher dispatcher;

	@Autowired
	private TestService<?> _service;

	protected Map<String, Map<HttpMethod, List<ApiCall>>> getConfig() throws ApiDispatcherException {
		Map<String, Map<HttpMethod, List<ApiCall>>> config = new ApiCallConfigBuilder()
			.addApi("test_simple")
				.addHttpMethod(HttpMethod.GET)
						.addApiCall(new ApiCall(_service, "testSimple")).closeGroup()
			.addApi("test_path")
				.addHttpMethod(HttpMethod.POST)
					.addApiCall(new ApiCall(_service, "testSinglePath", "first"))
					.addApiCall(new ApiCall(_service, "testSinglePath", "first/second"))
					.and()
				.addHttpMethod(HttpMethod.PUT)
					.addApiCall(new ApiCall(_service, "testDoublePath", "first/second"))
					.and()
				
				.addHttpMethod(HttpMethod.PATCH)
					.addApiCall(new ApiCallBuilder(_service, "testParamPathInt")
						.addPath("users/{user_id}")
						.addMethodVariable(new ApiVariable("user_id", Integer.class))
						.build())
					.addApiCall(new ApiCallBuilder(_service, "testPathParams")
						.addPath("users/{user_type:[a-z]{5}}/{user_id}")
						.addMethodVariable(new ApiVariable("user_id", Integer.class))
						.addMethodVariable(new ApiVariable("user_type", String.class))
						.build())
				.and()
				
				.addHttpMethod(HttpMethod.DELETE)
				.addApiCall(new ApiCallBuilder(_service, "testPathParams")
						.addPath("users/{user_type:[a-z]{5}}")
						.addMethodVariables(new ApiVariable[] {
								new ApiVariable("user_id", Integer.class),
								new ApiVariable("user_type", String.class)
						})
						.build())
				.closeGroup()
			.addApi("test_opt_path")
				.addHttpMethod(HttpMethod.POST)
				.addApiCall(new ApiCallBuilder(_service, "testPathParams")
						.addPath("users/{user_id}")
						.addMethodVariables(new ApiVariable[] {
								new ApiVariable("user_id", Integer.class),
								new ApiVariable("user_type", String.class, false)
						}).build())
				.closeGroup()
			.addApi("body_path_args")
				.addHttpMethod(HttpMethod.POST)
				  .addApiCall(new ApiCallBuilder(_service, "testPathParams")
						.addPath("{arg_id}")
						.addMethodVariables(new ApiVariable[] {
								new ApiVariable("arg_id", Integer.class),
								new ApiVariableBuilder("test_bean", TestBean.class)
									.mappable("json").build()
						}).build())
				
				.and()
        .addHttpMethod(HttpMethod.PUT)
          .addApiCall(new ApiCallBuilder(_service, "testParamPathInt")
            .addMethodVariables(new ApiVariable[] {
                new ApiVariableBuilder("user_id", Integer.class)
                  .mappable("string").build()
            }).build())
				
        .and()
        .addHttpMethod(HttpMethod.PATCH)
          .addApiCall(new ApiCallBuilder(_service, "testParamPathInt")
            .addMethodVariables(new ApiVariable[] {
                new ApiVariableBuilder("user_id", Number.class)
                  .mappable("string", Double.class).build()
            }).build())
            
				.closeGroup()
			.addApi("def_args")
				.addHttpMethod(HttpMethod.POST)
				.addApiCall(new ApiCallBuilder(_service, "testPathParams")
						.addMethodVariables(new ApiVariable[] {
								new ApiVariableBuilder("user_id", Integer.class)
									.defValue(5).build(),
								new ApiVariableBuilder("test_bean", TestBean.class)
									.mappable("json")
							.build()
						}).build())
				.closeGroup()
			.addApi("download")
				.addHttpMethod(HttpMethod.POST)
				.addApiCall(new ApiCallBuilder(_service, "sendFile")
						.addMethodVariables(new ApiVariable[] {
								new ApiVariable("msg", String.class),
								new OutputApiVariable()
						}).build())
				.closeGroup()
			.addApi("find_max")
				.addHttpMethod(HttpMethod.GET)
				.addApiCall(new ApiCallBuilder(_service, "findMax")
						.addMethodVariables(new ApiVariable[] {
								new RequestMapApiVariable("map")
						}).build())
				.closeGroup()
			.addApi("test_void")
					.addHttpMethod(HttpMethod.POST)
					.addApiCall(new ApiCallBuilder(_service, "dummyVoid")
						.addMethodVariable(
							new ApiVariable("msg", String.class)).build())
					.closeGroup()
					
      .addApi("test_generic")
        .addHttpMethod(HttpMethod.GET)
        .addApiCall(new ApiCallBuilder(_service, "testGenericMethod")
            .addMethodVariables(new ApiVariable[] {
                new ApiVariable("msg", String.class),
                new ApiVariableBuilder("id", Integer.class).generic().build(),
            }).build())
			.build();

		Map<HttpMethod, List<ApiCall>> dummy = new HashMap<>();
		dummy.put(HttpMethod.POST, null);
		config.put("dummy", dummy);

		Map<HttpMethod, List<ApiCall>> empty = new HashMap<>();
		empty.put(HttpMethod.PUT, new ArrayList<>());
		config.put("empty", empty);

		// null_service
		Map<HttpMethod, List<ApiCall>> nullService = new HashMap<>();
		List<ApiCall> calls = new ArrayList<>();
		calls.add(new ApiCall(null, ""));
		nullService.put(HttpMethod.DELETE, calls);
		config.put("null_service", nullService);
    
		config.put("bad_path_params",
				getTestBadApiGroupConfig(HttpMethod.PATCH, 
				            "testParamPathInt", "users/foo", "user_id", Integer.class));
		config.put("empty_path_params", 
		            getTestApiGroupConfig(HttpMethod.PATCH, "testParamPathInt",
				"users/{user_id}"));
		config.put("missing_path_param", 
		            getTestBadApiGroupConfig(HttpMethod.PATCH, "testParamPathInt",
			"users/{user_type:[a-z]{5}}/{user_id}", "user_id", Integer.class));

		// bad_path_args
		Map<HttpMethod, List<ApiCall>> badArgs = 
		              getTestApiGroupConfig(HttpMethod.PATCH, "testPathParams",
				"customers/{cust_name}");
		badArgs.get(HttpMethod.PATCH).get(0)
			.setMethodVariables(new ApiVariable[] { 
					new ApiVariable("cust_id", Integer.class, false), 
					new ApiVariable("cust_name", String.class) 
			});
		config.put("bad_path_args", badArgs);

		// missing_path_params
		Map<HttpMethod, List<ApiCall>> missingArgs = getTestApiGroupConfig(
				HttpMethod.PATCH, "testPathParams",
				"users/{user_type:[a-z]{5}}/{user_id}");
		missingArgs.get(HttpMethod.PATCH).
		                      get(0).setMethodVariables(new ApiVariable[] {
				new ApiVariable("user_id", Integer.class),
				new ApiVariable("user_type", String.class) });
		config.put("missing_path_params", missingArgs);
		
		// duplicated_request
		Map<HttpMethod, List<ApiCall>> dupReq = 
		              getTestBadApiGroupConfig(HttpMethod.GET, "testPathParams",
				"duplicated_call/{user_type:[a-z]{2}}", "user_type", String.class);
		dupReq.get(HttpMethod.GET).add(new ApiCall(_service, "testPathParams",
				"duplicated_call/qq"));
		config.put("duplicated_request", dupReq);

		// opt_body_path_args
		Map<HttpMethod, List<ApiCall>> optBodyPathArgs = getTestApiGroupConfig(
				HttpMethod.POST, "testPathParams",
				"{arg_id}");
		optBodyPathArgs.get(HttpMethod.POST).get(0)
			.setMethodVariables(new ApiVariable[] { 
					new ApiVariable("arg_id", Integer.class, false),
					new ApiVariableBuilder("test_bean",
					              TestBean.class).required().mappable("json").build()
			});
		config.put("opt_body_path_args", optBodyPathArgs);
		
		// no_path_args
		Map<HttpMethod, List<ApiCall>> noPathArgs = getTestApiGroupConfig(
				HttpMethod.POST, "testPathParams");
		noPathArgs.get(HttpMethod.POST).get(0)
			.setMethodVariables(new ApiVariable[] { 
					new ApiVariable("arg_id", Integer.class, false), 
					new ApiVariableBuilder("test_bean",
					              TestBean.class).required().mappable("json").build()
			});
		config.put("no_path_args", noPathArgs);
		return config;
	}

	private Map<HttpMethod, List<ApiCall>> 
	                        getTestApiGroupConfig(HttpMethod httpMethod, 
			String serviceMethod) {
		Map<HttpMethod, List<ApiCall>> group = new HashMap<>();
		List<ApiCall> calls = new ArrayList<>();
		ApiCall call = new ApiCall(_service, serviceMethod);
		
		calls.add(call);
		group.put(httpMethod, calls);

		return group;
	}
	
	private Map<HttpMethod, List<ApiCall>> 
	                        getTestApiGroupConfig(HttpMethod httpMethod, 
			String serviceMethod, String path) {
		Map<HttpMethod, List<ApiCall>> group = new HashMap<>();
		List<ApiCall> calls = new ArrayList<>();
		ApiCall call = new ApiCall(_service, serviceMethod, path);
		
		calls.add(call);
		group.put(httpMethod, calls);

		return group;
	}
	
	private Map<HttpMethod, List<ApiCall>> 
	                  getTestBadApiGroupConfig(HttpMethod httpMethod, 
			String serviceMethod, String path, String varName, Class<?> varClass) {
		Map<HttpMethod, List<ApiCall>> group = new HashMap<>();
		List<ApiCall> calls = new ArrayList<>();
		ApiCall call = new ApiCall(_service, serviceMethod, path);
		ApiVariable[] variables = new ApiVariable[] {
				new ApiVariable(varName, varClass)
		};
		
		call.setMethodVariables(variables);
		calls.add(call);
		group.put(httpMethod, calls);

		return group;
	}

	@Test
	public void testSuccessProcessRequests() throws ApiDispatcherException {
		testSuccessProcessRequest("{" +
				"\"api\": \"test_simple\"," +
				"\"method\": \"get\"" +
			"}", "\"simple\"");
		testSuccessProcessRequest(getApiRequest("test_simple", "get"), "simple");
		
		testSuccessProcessRequest("{" +
					"\"api\": \"test_path\"," +
					"\"method\": \"post\"," +
					"\"path_params\":[\"first\"]" +
			"}", "\"single_path\"");
		testSuccessProcessRequest(getApiRequest("test_path", "post",
		                              new String[] { "first" }), "single_path");
		
		testSuccessProcessRequest("{" +
				"\"api\": \"test_path\"," +
				"\"method\": \"post\"," +
				"\"path_params\":[\"first\", \"second\"]" +
		"}", "\"single_path\"");
	testSuccessProcessRequest(getApiRequest("test_path", "post",
	                      new String[] { "first", "second" }), "single_path");
		
		testSuccessProcessRequest("{" +
					"\"api\": \"test_path\"," +
					"\"method\": \"put\"," +
					"\"path_params\":[\"first\",\"second\"]" +
			"}", "\"double_path\"");
		testSuccessProcessRequest(getApiRequest("test_path", "put",
		                    new String[] { "first", "second" }), "double_path");
		
		Integer param = 55;
		Integer res = param * 5;
		testSuccessProcessRequest("{" +
				"\"api\": \"test_path\"," +
				"\"method\": \"patch\"," +
				"\"path_params\":[\"users\",\"" + param + "\"]" +
		"}", res.toString());
		testSuccessProcessRequest(getApiRequest("test_path", "patch",
				new String[] { "users", param.toString() }), res);
		
		testSuccessProcessRequest("{" +
				"\"api\": \"test_path\"," +
				"\"method\": \"patch\"," +
				"\"path_params\":[\"users\",\"valid\",\"55\"]" +
		"}", "{\"int_var\":55,\"str_var\":\"valid\"}");
		testSuccessProcessRequest(getApiRequest("test_path", "patch",
		                              new String[] { "users", "valid", "55" }),
				new TestBean(55,"valid"));
		
		testSuccessProcessRequest("{" +
				"\"api\": \"test_path\"," +
				"\"method\": \"delete\"," +
				"\"path_params\":[\"users\",\"valid\"]," +
				"\"query_params\":{" +
					"\"user_id\":\"55\"" +
				"}" +
		"}", "{\"int_var\":55,\"str_var\":\"valid\"}");
		testSuccessProcessRequest(
				getApiRequest("test_path", "delete",
				              new String[] { "users", "valid" }, "user_id", "55"),
				new TestBean(55,"valid"));
		
		testSuccessProcessRequest("{" +
				"\"api\": \"test_opt_path\"," +
				"\"method\": \"post\"," +
				"\"path_params\":[\"users\",\"55\"]" +
		"}", "{\"int_var\":55}");
		testSuccessProcessRequest(
				getApiRequest("test_opt_path", "post", new String[] { "users", "55" }),
				new TestBean(55));
		
		testSuccessProcessRequest("{" +
				"\"api\": \"body_path_args\"," +
				"\"method\": \"post\"," +
				"\"path_params\":[\"88\"]," +
				"\"body\":\"{\\\"int_var\\\":22,\\\"str_var\\\":\\\"hello\\\"}\"" +
		"}", "{\"int_var\":110,\"str_var\":\"hello\"}");
		ApiCallDto req = getApiRequest("body_path_args", "post",
				new String[] {"88"});
		req.setBody("{\"int_var\": 22, \"str_var\":\"hello\"}");
		testSuccessProcessRequest(req, new TestBean(22 + 88, "hello"));
		
		Integer param2 = 45;
		Integer res2 = new Integer(param2 * 5);
		testSuccessProcessRequest("{" +
        "\"api\": \"body_path_args\"," +
        "\"method\": \"put\"," +
        "\"body\":\"" + param2 + "\"}", res2.toString());
    req = getApiRequest("body_path_args", "put");
    req.setBody(param2.toString());
    testSuccessProcessRequest(req, res2);
    
    Double param3 = 45.3;
    Double res3 = 45.3;
    testSuccessProcessRequest("{" +
        "\"api\": \"body_path_args\"," +
        "\"method\": \"patch\"," +
        "\"body\":\"" + param3 + "\"}", res3.toString());
    req = getApiRequest("body_path_args", "patch");
    req.setBody(param3.toString());
    testSuccessProcessRequest(req, res3);
    
		testSuccessProcessRequest("{" +
				"\"api\": \"opt_body_path_args\"," +
				"\"method\": \"post\"," +
				"\"path_params\":[\"33\"]" +
		"}", "{\"int_var\":33}");
		testSuccessProcessRequest(getApiRequest("opt_body_path_args", "post",
				new String[] {"11"}), new TestBean(11));
		
		testSuccessProcessRequest("{" +
				"\"api\": \"no_path_args\"," +
				"\"method\": \"post\"," +
				"\"query_params\":{\"arg_id\": 54}," +
				"\"body\":\"{\\\"int_var\\\":45,\\\"str_var\\\":\\\"bye\\\"}\"" +
		"}", "{\"int_var\":99,\"str_var\":\"bye\"}");
		req = getApiRequest("no_path_args", "post", "arg_id", "54");
		req.setBody("{\"int_var\": 45, \"str_var\":\"bye\"}");
		testSuccessProcessRequest(req, new TestBean(99, "bye"));
	}

	@Test
	public void testDownloadRequest() throws ApiDispatcherException {
		String msg = "Hello";
		
		ByteArrayOutputStream out = new  ByteArrayOutputStream();
		ApiCallDto req = getApiRequest("download", "post", "msg", msg);
		Object o = dispatcher.processRequest(req, out);
		assertNull(o);
		assertArrayEquals(msg.getBytes(), out.toByteArray());
	}
	
	@Test
	public void testReqParamsMap() throws ApiDispatcherException {
		int len = 5;
		String msg = "test";
		
		ByteArrayOutputStream out = new  ByteArrayOutputStream();
		ApiCallDto req = getApiRequest("find_max", "get");
		Map<String, Object> qparams = new HashMap<>();
		
		String qstr = "";
		for (int i = 1; i <= len; i++) {
			String key = msg + i;
			qparams.put(key, i);
			qstr += ",\"" + key + "\":" + i;
		}
		
		req.setQueryParams(qparams);
		
		Object o = dispatcher.processRequest(req, out);
		assertNotNull(o);
		assertEquals(msg + len, o.toString());
		
		testSuccessProcessRequest("{" +
				"\"api\": \"find_max\"," +
				"\"method\": \"get\"," +
				"\"query_params\":{" + qstr.substring(1) + "}" +
		"}", "\"" + msg + len + "\"");
	}
	
	@Test
	public void testFailProcessRequests() {
	  testFailProcessRequestEncoded(1, null);
	  testFailProcessRequestEncoded(1, "");
		testFailProcessRequest(1, "");
		testFailProcessRequest(2, "qq");
		testFailProcessRequest(4, "{\"api\": \"test_simple\"}");
		
		// Try send some garbage
		try {
			dispatcher.processRequest("qqq");
			fail("ApiDispatcherException expected");
		} catch (ApiDispatcherException e) {
			assertEquals(2, e.getCode().intValue());
		}
	}
	
	@Test
	public void testBadProcessRequests() {
		testFailProcessRequest(5, getApiRequest("foo", "get"));
		testFailProcessRequest(6, getApiRequest("test_simple", "foo"));
		testFailProcessRequest(7, getApiRequest("test_simple", "post"));
		testFailProcessRequest(7, getApiRequest("dummy", "post"));
		testFailProcessRequest(8, getApiRequest("empty", "put"));
		testFailProcessRequest(9, getApiRequest("duplicated_request",
		              "get", new String[] { "duplicated_call", "qq" }));
		
		testFailProcessRequest(10, getApiRequest("test_path", "patch",
		                                          new String[] { "users" }));
		testFailProcessRequest(10, getApiRequest("test_path", "post"));
		testFailProcessRequest(10, getApiRequest("test_path", "post",
		                                               new String[] { "foo" }));
		testFailProcessRequest(10, getApiRequest("bad_path_params", "patch",
		                                              new String[] { "users" }));
		testFailProcessRequest(10, getApiRequest("bad_path_params", "patch",
		                                      new String[] { "users", "dummy" }));
		testFailProcessRequest(10, getApiRequest("test_path", "patch",
		                            new String[] { "users", "invalid", "55" }));
		
		testFailProcessRequest(14, getApiRequest("bad_path_params", "patch",
		                                      new String[] { "users", "foo" }));

		testFailProcessRequest(16,
				getApiRequest("test_path", "delete",
				              new String[] { "users", "valid" }, "user_id", "me"));
		
		testFailProcessRequest(16, getApiRequest("test_path", "patch",
		                                new String[] { "users", "empty" }));

		testFailProcessRequest(17, getApiRequest("null_service", "delete"));

		testFailProcessRequest(18, getApiRequest("empty_path_params", "patch",
		                                        new String[] { "users", "1" }));
	}

	@Test
	public void testErrorInvokingService() {
		testFailProcessRequest(19, getApiRequest("bad_path_args", "patch",
		                                new String[] { "customers", "john" }));	
	}
	
	@Test
	public void testBodyValidationError() {
		ApiCallDto req = getApiRequest("body_path_args", "post",
				new String[] {"88"});
		req.setBody("{\"str_var\":\"hello\"}");
		
		testFailProcessRequest(4, req);
	}
	
	@Test
	public void testMissingBodyParam() {
		ApiCallDto req = getApiRequest("body_path_args", "post",
				new String[] {"88"});
		
		testFailProcessRequest(12, req);
	}
	
	@Test
	public void testEmptyBodyParam() {
		ApiCallDto req = getApiRequest("body_path_args", "post",
				new String[] {"88"});
		req.setBody("");
		
		testFailProcessRequest(13, req);
	}
	
	@Test
	public void testMethodInvokeError() {
		ApiCallDto req = getApiRequest("no_path_args", "post");
		req.setBody("{\"int_var\":\"99\"}");
		testFailProcessRequest(19, req);
	}
	
	@Test
	public void testDefParamValue() throws ApiDispatcherException {
		ApiCallDto req = getApiRequest("def_args", "post");
		req.setBody("{\"int_var\":\"88\"}");
		
		// Expecting 93 = 88 + 5 (default)
		testSuccessProcessRequest(req, new TestBean(93));
		
	}
	
	@Test
	public void testVoidMethodReturn() throws ApiDispatcherException {
		ApiCallDto req = getApiRequest("test_void", "post", "msg", "Hello");
		
		Object o = dispatcher.processRequest(req);
		assertNull(o);
		
		testSuccessProcessRequest("{" +
				"\"api\": \"test_void\"," +
				"\"method\": \"post\"," +
				"\"query_params\":{\"msg\": \"Hello\"}" +
			"},", "\"\"");
		
	}
	
	@Test
	public void testGenericMethod() throws ApiDispatcherException {
	  testSuccessProcessRequest("{" +
        "\"api\": \"test_generic\"," +
        "\"method\": \"get\"," +
        "\"query_params\":{" +
          "\"msg\": \"Hello\"," +
          "\"id\": 89" +
        "}" +
      "},", "\"Hello=89\"");
	}
	
	private void testSuccessProcessRequest(ApiCallDto req, Object res)
	                                      throws ApiDispatcherException {
		Object o = dispatcher.processRequest(req);
		assertNotNull(o);
		assertEquals(res, o);

	}

	private void testSuccessProcessRequest(String req, String res)
	                                      throws ApiDispatcherException {
		Object o = dispatcher.processRequest(encodeRequest(req));
		assertNotNull(o);
		assertEquals(res, o.toString());

	}

	protected String encodeRequest(String req) {
		return new String(Base64.getEncoder().encode(req.getBytes()));
	}
	
	protected ApiCallDto getApiRequest(String name, String method) {
		ApiCallDto req = new ApiCallDto();
		req.setApi(name);
		req.setMethod(method);

		return req;
	}

	protected ApiCallDto getApiRequest(String name,
	                              String method, String[] pathParams) {
		ApiCallDto req = getApiRequest(name, method);
		req.setPathParams(pathParams);

		return req;
	}

	protected ApiCallDto getApiRequest(String name, 
	      String method, String[] pathParams, String qname, String qvalue) {
		ApiCallDto req = getApiRequest(name, method, pathParams);

		Map<String, Object> qparams = new HashMap<>();
		qparams.put(qname, qvalue);
		req.setQueryParams(qparams);

		return req;
	}

	protected ApiCallDto getApiRequest(String name, String method,
	                                            String qname, String qvalue) {
		ApiCallDto req = getApiRequest(name, method);
		
		Map<String, Object> qparams = new HashMap<>();
		qparams.put(qname, qvalue);
		req.setQueryParams(qparams);
		
		return req;
	}
	
	private void testFailProcessRequest(int code, ApiCallDto api) {
		try {
			dispatcher.processRequest(api, null);
			fail("ApiDispatcherException expected");
		} catch (ApiDispatcherException e) {
			assertEquals(e.getMessage(), code, e.getCode().intValue());
		}
	}

	public void testFailProcessRequest(int code, String req) {
	  testFailProcessRequestEncoded(code, new String(
        Base64.getEncoder().encode(req.getBytes())));
	}
	
	 public void testFailProcessRequestEncoded(int code, String req) {
	    try {
	      dispatcher.processRequest(req);
	      fail("ApiDispatcherException expected");
	    } catch (ApiDispatcherException e) {
	      assertEquals(code, e.getCode().intValue());
	      _log.error(e.getMessage());
	    }
	  }
	public ApiDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public void setDispatcher(ApiDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
}
