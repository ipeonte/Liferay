package com.example.api_dispatcher.base.variable;

import java.lang.reflect.Constructor;

import org.springframework.util.StringUtils;

import com.example.api_dispatcher.base.variable.mappable.IMappableField;

/**
 * Class for in/Out ApiVariable
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class ApiVariable {

  // Variable name
  private final String name;

  // Class type of variable
  private final Class<?> clazz;

  // Required flag
  // Default: true
  private boolean required = true;

  // Optional default value if not required
  // Default: null
  private Object defValue;

  // Flag that indicates that value needs to be mapped by variable class type from JSON
  // Default: false
  private IMappableField mappable;

  // Flag that indicates that argument is generic type i.e. needs to be mapped to argument as Object
  // Default: false
  private boolean generic;

  public ApiVariable(String name, Class<?> clazz) {
    this.name = name;
    this.clazz = clazz;
  }

  public ApiVariable(String name, Class<?> clazz, boolean required) {
    this(name, clazz);
    this.required = required;
  }

  public ApiVariable(String name, Class<?> clazz, boolean required,
      Object defValue, String mappable, Class<?> mapClass, boolean generic)
      throws Exception {
    this(name, clazz);

    this.defValue = defValue;
    this.required = required && defValue == null;
    if (mappable != null)
      this.mappable = initMappableConvertor(mappable, mapClass);
    this.generic = generic;
  }

  public String getName() {
    return name;
  }

  public Class<?> getVarClass() {
    return clazz;
  }

  public Class<?> getClassType() {
    return generic ? Object.class : clazz;
  }

  public String getClassName() {
    return clazz.getName();
  }

  public boolean getRequired() {
    return required;
  }

  protected void setNotRequired() {
    this.required = false;
  }

  public Object getDefValue() {
    return defValue;
  }

  public IMappableField getMappable() {
    return mappable;
  }

  public boolean isMappable() {
    return mappable != null;
  }

  private IMappableField initMappableConvertor(String type, Class<?> mapClass)
      throws Exception {
    Class<?> clazz = Class.forName(IMappableField.class.getPackage().getName() +
        "." + StringUtils.capitalize(type) + "MappableField");
    Constructor<?> co = clazz.getConstructor(Class.class);
    Object obj = co.newInstance(mapClass);

    if (obj instanceof IMappableField)
      return (IMappableField) obj;
    else
      throw new Exception(
          obj.getClass().toString() + " is not instance of IMappableField");
  }

  @Override
  public String toString() {
    return "[" + name + ":" + clazz.getSimpleName() + ":" + required + ":" +
        mappable + (defValue != null ? ":" + defValue : "") + "]";
  }
}
