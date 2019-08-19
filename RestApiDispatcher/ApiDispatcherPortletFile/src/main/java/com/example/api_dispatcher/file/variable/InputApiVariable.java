package com.example.api_dispatcher.file.variable;

import java.io.InputStream;

import com.example.api_dispatcher.base.variable.ApiVariable;
import com.example.api_dispatcher.file.ApiDispatcherFile;

/**
 * Class for upload api ApiVariable
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class InputApiVariable extends ApiVariable {

	public static final Class<?> CLAZZ = InputStream.class;
	
	public InputApiVariable() {
		this(ApiDispatcherFile.FNAME);
	}
	
	public InputApiVariable(String name) {
		super(name, CLAZZ);
	}
	
	public InputApiVariable(boolean requred) {
    this();
    setNotRequired();
  }
	
	public InputApiVariable(String name, boolean requred) {
    super(name, CLAZZ);
    setNotRequired();
  }
}
