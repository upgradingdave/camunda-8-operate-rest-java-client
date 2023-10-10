package io.camunda.operate.rest.exception;

public class OperateException extends Exception {

  private static final long serialVersionUID = 1L;

  public OperateException() {
    super();
  }

  public OperateException(Exception e) {
    super(e);
  }

  public OperateException(String message) {
    super(message);
  }

  public OperateException(String message, Exception e) {
    super(message, e);
  }

}