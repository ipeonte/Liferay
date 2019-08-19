package com.example.api_dispatcher.base.service.impl;

import org.springframework.stereotype.Service;

import com.example.api_dispatcher.base.service.TestService;

@Service
public abstract class AbstractTestServiceImpl<T> implements TestService<T> {

  @Override
  public String testGenericMethod(String message, T value) {
    return message + "=" + value;
  }

}
