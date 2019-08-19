package com.example.api_dispatcher.base.config.builder;

import com.example.api_dispatcher.base.shared.ApiDispatcherException;
import com.example.api_dispatcher.base.variable.ApiVariable;

/**
 * Class for in/Out ApiVariable
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiVariableBuilder {

  private final String name;

  private final Class<?> clazz;

  // Source class for mappable field
  private Class<?> mclazz;
  
  private Object defValue;

  private boolean required = true;

  private String mappable;

  private boolean generic;

  public ApiVariableBuilder(String name, Class<?> className) {
    this.name = name;
    this.clazz = className;
  }

  public ApiVariableBuilder required() {
    this.required = false;
    return this;
  }

  public ApiVariableBuilder defValue(Object value) {
    this.defValue = value;
    return this;
  }

  public ApiVariableBuilder mappable(String mappable, Class<?> sourceClass) {
    this.mappable = mappable;
    this.mclazz = sourceClass;
    
    return this;
  }
  
  public ApiVariableBuilder mappable(String mappable) {
    return mappable(mappable, null);
  }

  public ApiVariableBuilder generic() {
    this.generic = true;
    return this;
  }

  public ApiVariable build() throws ApiDispatcherException {
    try {
      return new ApiVariable(name, clazz, required, defValue, mappable, mclazz,
          generic);
    } catch (Exception e) {
      throw new ApiDispatcherException(21,
          "Error initializing api variable with mappable type [" + mappable +
              "]",
          e);
    }
  }
}
