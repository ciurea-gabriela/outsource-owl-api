package com.outsourceowl.exception;

public class ForbiddenEventException extends RuntimeException {
  public ForbiddenEventException(String message) {
    super(message);
  }
  
  public ForbiddenEventException() {
    super("Access to this resource is forbidden");
  }
}
