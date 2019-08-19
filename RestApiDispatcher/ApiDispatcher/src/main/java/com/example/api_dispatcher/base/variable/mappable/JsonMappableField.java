package com.example.api_dispatcher.base.variable.mappable;

import com.example.api_dispatcher.base.ApiDispatcher;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;

public class JsonMappableField extends AbstractMappableField {

  /**
   * Default constructor
   * 
   * @param mapClass Class name to map into
   */
  public JsonMappableField(Class<?> mapClass) {
    super(mapClass);
  }

  @Override
  public Object convertValue(String value, Class<?> clazz, String msg)
      throws ApiDispatcherException {
    return ApiDispatcher.readValidateValue(value, clazz, msg);
  }

}
