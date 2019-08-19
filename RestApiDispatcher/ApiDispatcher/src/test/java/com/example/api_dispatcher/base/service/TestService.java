package com.example.api_dispatcher.base.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.example.api_dispatcher.base.TestBean;

public interface TestService<T> {

	String testSimple();
	String testSinglePath();
	String testDoublePath();
	TestBean testPathParams(Integer userId, TestBean bean) throws Exception;
	TestBean testPathParams(Integer intVar, String strVar) throws Exception;
	Integer testParamPathInt(Integer intVar);
	Number testParamPathInt(Number var);
	void sendFile(String msg, OutputStream out) throws IOException;
	String findMax(Map<String, Integer> request);
	void dummyVoid(String msg);
	
	String testGenericMethod(String message, T value);
}
