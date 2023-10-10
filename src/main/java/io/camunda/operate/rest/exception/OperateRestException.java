package io.camunda.operate.rest.exception;

import io.camunda.operate.rest.dto.ErrorResponse;

public class OperateRestException extends Exception {

  private static final long serialVersionUID = 1L;

  public OperateRestException(ErrorResponse errorResponse) {
    super(errorResponse.getMessage());
  }

  public OperateRestException(ErrorResponse errorResponse, Exception e) {
    super(errorResponse.getMessage(), e);
  }

}