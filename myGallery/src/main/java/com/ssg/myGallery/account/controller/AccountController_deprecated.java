//package com.ssg.myGallery.account.controller;
//
//import com.ssg.myGallery.account.dto.AccountJoinRequests;
//import com.ssg.myGallery.account.dto.AccountLoginRequests;
//import com.ssg.myGallery.account.helper.AccountHelper;
//import com.ssg.myGallery.member.dto.MemberLogin;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/v1") // 모든 HTTP 메소드의 요청을 매핑하기 위한 애너테이션
//public class AccountController_deprecated {
//
//  private final AccountHelper accountHelper;
//
//
//  // 회원가입
//  @PostMapping("/api/account/join")
//  public ResponseEntity<?> join(@RequestBody AccountJoinRequests joinReq) {
//    // 입력값이 비어 있다면
//    if(joinReq.getName() == null || joinReq.getLoginId() == null || joinReq.getLoginPw() == null) {
//      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }
//
//    accountHelper.join(joinReq);
//    return new ResponseEntity<>(HttpStatus.OK);
//  }
//
//  // 로그인
//  @PostMapping("/api/account/login")
//  public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response,
//      @RequestBody AccountLoginRequests loginReq) {
//
//    // 입력값이 비어 있다면
//    if(loginReq.getLoginId() == null || loginReq.getLoginPw() == null) {
//      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }
//
//    MemberLogin output = accountHelper.login(loginReq, request, response);
//    if(output == null) {
//      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    return new ResponseEntity<>(output, HttpStatus.OK);
//  }
//
//  // 로그인 여부 확인
//  @GetMapping("/api/account/check")
//  public ResponseEntity<?> check(HttpServletRequest request) {
//    // 💡 getLoginUser를 호출하여 MemberLogin 객체(세션 유효 시) 또는 null(세션 무효 시)을 받습니다.
//    MemberLogin loginUser = accountHelper.getLoginUser(request);
//
//    if (loginUser == null) {
//      // 로그인 상태가 아닐 경우: 200 OK와 false(혹은 null) 반환
//      return new ResponseEntity<>(false, HttpStatus.OK);
//    }
//
//// 💡 로그인 상태일 경우: 200 OK와 MemberLogin DTO를 JSON으로 반환
//    return new ResponseEntity<>(loginUser, HttpStatus.OK);
//  }
//
//  @PostMapping("/api/account/logout")
//  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
//    accountHelper.logout(request, response);
//    return new ResponseEntity<>(HttpStatus.OK);
//  }
//
//
//
//}
//
