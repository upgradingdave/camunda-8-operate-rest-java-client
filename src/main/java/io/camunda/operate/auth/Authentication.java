package io.camunda.operate.auth;

import io.camunda.operate.OperateRestClient;
import io.camunda.operate.exception.OperateException;

public interface Authentication {

  public Boolean authenticate(OperateRestClient operateRestClient) throws OperateException;

}
