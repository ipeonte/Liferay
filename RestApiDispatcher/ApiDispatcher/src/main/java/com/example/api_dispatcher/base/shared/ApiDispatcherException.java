package com.example.api_dispatcher.base.shared;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Class for main Api Dispatcher Exception
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

@JsonRootName("error")
public class ApiDispatcherException extends Exception {

	// Default Serial Version UID
	private static final long serialVersionUID = 1L;

	private Integer code;
	
	private String info;
	
	private String[] details;
	
	private Throwable exception;
	
	public ApiDispatcherException(int code, String info) {
		super(getMessage(code, info));
		
		this.info = info;
		this.code = code;
	}
	
	public ApiDispatcherException(int code, String[] details) {
		super(getMessage(code,"[" + Arrays.asList(details).stream().
				collect(Collectors.joining("],[")) + "]"));
		
		this.code = code;
		this.details = details;
	}

  public ApiDispatcherException(int code, Throwable e) {
    super(getMessage(code, e.getClass().getName() + ":" + getExceptionMessage(e)));
    
    this.exception = e;
    this.code = code;
  }

  public ApiDispatcherException(int code, String info, String[] details) {
		super(getMessage(code, info + "[" + Arrays.asList(details).stream().
				collect(Collectors.joining("],[")) + "]"));
		
		this.code = code;
		this.info = info;
		this.details = details;
	}

	public ApiDispatcherException(int code, String info, Throwable e) {
		super(getMessage(code, info + ":" + e.getClass().getName() + ":" + getExceptionMessage(e)));
		
		this.exception = e;
		this.code = code;
		this.info = info;
	}
	
	public ApiDispatcherException(int code, Exception e, String details) {
		super(getMessage(code, details + ":" + e.getClass().getName() + "->" + getExceptionMessage(e)));
		
		this.exception = e;
		this.code = code;
		this.details = new String[] { details };
	}
	
	public Integer getCode() {
		return code;
	}
	
	public String getInfo() {
		return info;
	}
	
	public Throwable getException() {
		return exception;
	}
	
	public String[] getDetails() {
		return details;
	}
	
	private static String getMessage(int code, String message) {
		return code + " - " + message;
	}
	
	private static String getExceptionMessage(Throwable e) {
		return e.getMessage() != null 
				? e.getMessage() 
				: (e.getCause() != null 
					? e.getCause().getClass().getName() + "->" + e.getCause().getMessage() 
					: "");
	}
}
