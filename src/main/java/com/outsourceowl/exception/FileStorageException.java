package com.outsourceowl.exception;

public class FileStorageException extends RuntimeException {
  public FileStorageException(String s) {}

  public FileStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
