package com.example.api_dispatcher.base;

import javax.validation.constraints.NotNull;

import com.example.api_dispatcher.base.TestBean;

public class TestBean {

	@NotNull
	private Integer intVar;
	
	private String strVar;

	public TestBean() {
	}
	
	public TestBean(Integer intVar) {
		this.intVar = intVar;
	}
	
	public TestBean(Integer intVar, String strVar) {
		this.intVar = intVar;
		this.strVar = strVar;
	}
	
	public Integer getIntVar() {
		return intVar;
	}

	public void setIntVar(Integer intVar) {
		this.intVar = intVar;
	}

	public String getStrVar() {
		return strVar;
	}

	public void setStrVar(String strVar) {
		this.strVar = strVar;
	}
	
	@Override
	public String toString() {
		return "[" + intVar + ":" + strVar + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass()))
			return false;
		
		TestBean tb = (TestBean) obj;
		return (strVar == null && tb.getStrVar() == null ||
				strVar != null && strVar.equals(tb.getStrVar())) && 
			tb.getIntVar().equals(intVar);
	}
}
