package com.example.api_dispatcher.base.variable.mappable;

import org.springframework.util.StringUtils;

import com.example.api_dispatcher.base.shared.ApiDispatcherException;

public abstract class AbstractMappableField implements IMappableField {

  private final Class<?> _mclazz;
  
  public AbstractMappableField(Class<?> sourceClassName) {
    _mclazz = sourceClassName;
  }
  
  abstract Object convertValue(String value, Class<?> clazz, String msg)
      throws ApiDispatcherException;
  
  @Override
  public Object convert(String value, Class<?> clazz, String msg)
      throws ApiDispatcherException {
    // 0. Null check
    if (StringUtils.isEmpty(value))
      throw new ApiDispatcherException(22, msg + " is empty");
    
    Object obj = convertValue(value, _mclazz != null ? _mclazz : clazz, msg);
    
    // Check if object class can be converted into map class
    if (_mclazz != null && !clazz.isInstance(obj))
      throw new ApiDispatcherException(23, "Value [" + value + "] of type [" +
          obj.getClass().getName() + "] cannot be cast to [" + clazz.getName());
    
    return obj;
  }
}
