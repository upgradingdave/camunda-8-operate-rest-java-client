package io.camunda.operate.rest.auth;

import io.camunda.operate.rest.OperateRestClient;
import io.camunda.operate.rest.exception.OperateException;

public interface Authentication {

  public Boolean authenticate(OperateRestClient operateRestClient) throws OperateException;

}
