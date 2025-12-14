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

      if (userData) {
        const { accessToken, ...userRestData } = userData;
        this.user = userRestData;
      }

    },
    setAccessToken(val) { // ② 액세스 토큰의 값 수정 메서드
      this.accessToken = val;
    },
  },
  // 💡 [추가] 상태 영속성 설정
  persist: {
    storage: localStorage, // LocalStorage에 저장
    paths: ['user', 'accessToken', 'loggedIn'],
  }
});
