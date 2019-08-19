package com.example.api_dispatcher.base.variable.mappable;

import com.example.api_dispatcher.base.shared.ApiDispatcherException;

/**
 * Mappable interface for Api Variable field
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public interface IMappableField {

  Object convert(String value, Class<?> clazz, String msg)
      throws ApiDispatcherException;
}
