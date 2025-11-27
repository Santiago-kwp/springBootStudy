package com.ssg.myGallery.common.interceptor;

import com.ssg.myGallery.account.helper.AccountHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component // ① 스프링 컨테이너에서 관리하는 컴포넌트
@RequiredArgsConstructor
public class ApiInterceptor implements HandlerInterceptor {

  private final AccountHelper accountHelper;

  @Override
  public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
    // ② 핸들러 인터셉터 인터페이스에서 정의한 메서드 구현. preHandle메서드 추후 구현 일단 true 리턴
    //   이렇게 처리하면 클라이언트의 요청이 이전과 동일하게 컨트롤러로 전달된다.
    if (accountHelper.getMemberId(req) == null) { // 로그인 회원 아이디가 없으면 401(Unauthorized)
      res.setStatus(401);
      return false;
    }
    return true;
  }
}
