package com.example.api_dispatcher.base.variable.mappable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.example.api_dispatcher.base.shared.ApiDispatcherException;

public class StringMappableField extends AbstractMappableField {

  /**
   * Default constructor
   * 
   * @param mapClass Class name to map into
   */
  public StringMappableField(Class<?> mapClass) {
    super(mapClass);
  }

  @Override
  public Object convertValue(String value, Class<?> clazz, String msg)
      throws ApiDispatcherException {
    Constructor<?> co;
    
    try {
      co = clazz.getConstructor(String.class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new ApiDispatcherException(24, e);
    }
    
    try {
      return co.newInstance(value);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new ApiDispatcherException(25, e);
    }
  }
}
