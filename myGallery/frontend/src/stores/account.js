//계정 스토어 구현
//frontend/src/stores/account.js
import {defineStore} from 'pinia'

export const useAccountStore = defineStore("account", { // ①
  state: () => ({
    checked: false, // ②
    loggedIn: false, // ③
    user: null, // 사용자 정보 ~ memberLogin DTO에서 넘어온 정보  => LocalStorate에 저장
    accessToken: "", // ①  액세스 토큰 프로퍼티 문자열 타입
  }),
  actions: {
    setChecked(val) { // ④
      this.checked = val;
    },
    setLoggedIn(val, userData = null) { // ⑤
      this.loggedIn = val;

      if (val) {
        // [로그인 상황] : userData가 있으면 저장
        if (userData) {
          const { accessToken, ...userRestData } = userData;
          this.user = userRestData;
          localStorage.setItem("user", JSON.stringify(userRestData));
        }
      } else {
        // [로그아웃 상황] : ★ user 정보도 반드시 비워야 함
        this.user = null;
        localStorage.removeItem("user");
      }
    },
    setAccessToken(val) { // ② 액세스 토큰의 값 수정 메서드
      this.accessToken = val;
      // 로컬스토리지에도 저장 (새로고침 시 유지)
      if (val) {
        localStorage.setItem("accessToken", val);
      } else {
        localStorage.removeItem("accessToken");
      }
    },
    // ★ [추가] 로그아웃 시 상태를 한방에 초기화하는 메서드
    clearAccount() {
      // 전체 상태 초기화
      this.checked = false;
      this.loggedIn = false;
      this.user = null;
      this.accessToken = "";

      // 로컬스토리지 정리
      localStorage.removeItem("accessToken");
      localStorage.removeItem("user");
    }

  },
  persist: true // pinia-plugin-persistedstate 사용
});
