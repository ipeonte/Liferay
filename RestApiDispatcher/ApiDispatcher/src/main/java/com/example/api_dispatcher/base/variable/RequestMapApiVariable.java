package com.example.api_dispatcher.base.variable;

import java.util.Map;

/**
 * Class for Request Parameter's Map Api ApiVariable
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class RequestMapApiVariable extends ApiVariable {

	public static final Class<?> CLAZZ = Map.class;
	
	public RequestMapApiVariable() {
		this("req_map");
	}
	
	public RequestMapApiVariable(String name) {
		super(name, CLAZZ);
	}
}
