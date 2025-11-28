package com.ssg.myGallery.exception;

// 401 응답을 위한 예외 (비밀번호 불일치)
public class InvalidPasswordException extends RuntimeException {
  public InvalidPasswordException(String message) {
    super(message);
  }
}