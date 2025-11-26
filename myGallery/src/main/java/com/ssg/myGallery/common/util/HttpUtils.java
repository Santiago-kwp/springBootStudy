package com.ssg.myGallery.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtils {

  // 세션에 데이터 입력
  public static void setSession(HttpServletRequest request, String key, Object value) {
    request.getSession().setAttribute(key, value);
  }

  // 세션 값 조회
  public static Object getSession(HttpServletRequest request, String key){
    return request.getSession().getAttribute(key);
  }

  // 세션 삭제
  public static void removeSession(HttpServletRequest request, String key){
    request.getSession().removeAttribute(key);
  }


}

