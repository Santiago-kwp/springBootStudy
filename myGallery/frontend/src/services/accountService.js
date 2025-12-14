// 계정 서비스 구현 : 백엔드 API를 호출해서 회원의 계정 데이터를 처리하는 서비스
import httpRequester from '@/libs/httpRequester.js';

// 회원가입
export const join = (args) => {
  return httpRequester.post("/v1/api/account/join", args);
};

// 로그인
export const login = (args) => {
  return httpRequester.post("/v1/api/account/login", args);
};

// 로그인 여부 확인
export const check = () => {
  return httpRequester.get("/v1/api/account/check");
};

// 로그아웃
export const logout = () => {
  return httpRequester.post("/v1/api/account/logout");
};
