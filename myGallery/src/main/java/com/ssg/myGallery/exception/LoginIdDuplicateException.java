package com.ssg.myGallery.exception;

public class LoginIdDuplicateException extends RuntimeException {
  public LoginIdDuplicateException(String message) {
    super(message);
  }
}
