// 계정 서비스 구현  : 백엔드 API를 호출해서 회원의 계정 데이터를 처리하는 서비스
import  httpRequester from '@/libs/httpRequester.js';

// 회원가입 : HTTP POST 메소드로 회원가입 API를 호출하고 응답값을 리턴하는 기능
export const join = (args) => { // ①
  return httpRequester.post("/v1/api/account/join", args).catch(e => e.response);
};

// 로그인 : 로그인 처리 메소드, HTTP 메소드로 로그인 API를 호출하고 응답값을 리턴하는 기능
export const login = (args) => { // ②
  return httpRequester.post("/v1/api/account/login", args).catch(e => e.response);
};

// 로그인 여부 확인 : HTTP GET 메소드로 로그인 여부 확인하는 API를 호출하고 응답값을 리턴하는 기능
export const check = () => { // ③
  return httpRequester.get("/v1/api/account/check").catch(e => e.response);
};

// 로그아웃
export const logout = () => { // ④
  return httpRequester.post("/v1/api/account/logout").catch(e => e.response);
};