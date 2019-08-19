package com.example.api_dispatcher.base.variable;

import java.io.OutputStream;

/**
 * Class for Download api ApiVariable
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class OutputApiVariable extends ApiVariable {

	public static final Class<?> CLAZZ = OutputStream.class;
	
	public OutputApiVariable() {
		this("out");
	}
	
	public OutputApiVariable(String name) {
		super(name, CLAZZ);
	}
}
