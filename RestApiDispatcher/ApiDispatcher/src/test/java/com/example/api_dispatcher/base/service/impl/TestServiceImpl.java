package com.example.api_dispatcher.base.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.api_dispatcher.base.TestBean;
import com.example.api_dispatcher.base.service.TestService;
import com.example.api_dispatcher.base.service.impl.AbstractTestServiceImpl;

@Service
public class TestServiceImpl extends AbstractTestServiceImpl<Integer> {

	private static Logger _log = LoggerFactory.getLogger(TestService.class);
	
	@Override
	public String testSimple() {
		return "simple";
	}

	@Override
	public TestBean testPathParams(Integer intVar, String strVar) throws Exception {
		if (intVar == null)
			throw new Exception("intVar is required");
		
		return new TestBean(intVar, strVar);
	}

	@Override
	public String testSinglePath() {
		return "single_path";
	}

	@Override
	public String testDoublePath() {
		return "double_path";
	}

	@Override
	public Integer testParamPathInt(Integer intVar) {
		return intVar * 5;
	}

	@Override
  public Number testParamPathInt(Number var) {
    return var;
  }
	
	@Override
	public TestBean testPathParams(Integer userId, TestBean bean) throws Exception {
		if (userId == null)
			throw new Exception("userId is required");
		
		if (bean == null)
			bean = new TestBean(0);
		
		bean.setIntVar(userId + bean.getIntVar());
		return bean;
	}

	@Override
	public void sendFile(String msg, OutputStream out) throws IOException {
		out.write(msg.getBytes());
	}

	@Override
	public String findMax(Map<String, Integer> request) {
		String res = null;
		
		int max = 0;
		for (Entry<String, Integer> entry: request.entrySet()) {
			if (entry.getValue() > max) {
				res = entry.getKey();
				max = entry.getValue();
			}
		}
		
		return res;
	}

	@Override
	public void dummyVoid(String msg) {
		// Do nothing
		_log.info("Test Message: " + msg);
	}

}
