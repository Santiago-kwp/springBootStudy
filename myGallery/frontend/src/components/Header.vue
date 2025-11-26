<script setup>
import {useAccountStore} from "@/stores/account";
import {logout} from "@/services/accountService";
import {ref,  computed} from "vue";
import {useRouter} from "vue-router";
// 💡 이미지 파일을 JavaScript 모듈로 import
import logo from '@/assets/images/logoXL.png';
const logoUrl = ref(logo);


// 계정 스토어
const accountStore = useAccountStore(); // ①

// 라우터 객체
const router = useRouter(); // ②

// 로그아웃
const logoutAccount = async () => { // ③
  const res = await logout();

  if (res.status === 200) {
    accountStore.setLoggedIn(false);
    await router.push("/");
  }
};

// 💡 1. 환영 메시지를 구성하는 Computed 속성 정의
const welcomeMessage = computed(() => {
  // user 객체가 있고, user.name이 있을 때만 메시지를 구성합니다.
  if (accountStore.user && accountStore.user.name) {
    return `✨ ${accountStore.user.name}님 환영합니다!`;
  }
  return ''; // 로그인 상태가 아니거나 이름이 없을 경우 빈 문자열 반환
});

</script>

<template>
  <header>
    <div class="navbar navbar-dark bg-dark text-white shadow-sm">
      <div class="container">
        <router-link to="/" class="navbar-brand">
          <strong><img :src="logoUrl" alt="내 로고" class="header-logo"/>Climb Gallery</strong>
        </router-link>
        <div class="menus d-flex gap-3">
          <template v-if="!accountStore.loggedIn">  <!-- ④ -->
            <router-link to="/login">로그인</router-link>
            <router-link to="/join">회원가입</router-link>
          </template>
          <template v-else>
            <a>{{ welcomeMessage }}&nbsp;</a>
            <router-link to="/orders">주문 내역</router-link>
            <router-link to="/cart">장바구니</router-link>
            <a @click="logoutAccount()">로그아웃</a>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<style lang="scss">
@import url('https://cdn.jsdelivr.net/gh/spoqa/spoqa-han-sans@latest/css/spoqa-han-sans-kr.css');
header {
  strong {
    font-family: 'Playfair Display', serif;
  }
  .menus {
    a { // ⑦
      cursor: pointer;
      color: #fff;
      text-decoration: none;
      font-family: 'Noto Serif KR', serif;
    }
  }
}
/* 💡 로고 이미지에 적용할 스타일 */
.header-logo {
  height: 3rem;
  width: auto;  /* 너비는 비율에 맞춰 자동으로 조절 */
  margin-right: 15px;
}
</style>