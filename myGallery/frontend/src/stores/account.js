//계정 스토어 구현
//frontend/src/stores/account.js
import {defineStore} from 'pinia'

export const useAccountStore = defineStore("account", { // ①
  state: () => ({
    checked: false, // ②
    loggedIn: false, // ③
    user: null, // 사용자 정보 ~ 어디서 받아오지??
  }),
  actions: {
    setChecked(val) { // ④
      this.checked = val;
    },
    setLoggedIn(val, userData = null) { // ⑤
      this.loggedIn = val;
      this.user = userData;
    },
  },
});
