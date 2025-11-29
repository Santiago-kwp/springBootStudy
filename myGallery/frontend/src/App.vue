<script setup>
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {useAccountStore} from "@/stores/account";
import {watch} from "vue";
import {useRoute} from "vue-router";
import {check} from "@/services/accountService"; // ① check 메소드만 import함

// 계정 스토어
const accountStore = useAccountStore();

// 라우트 객체
const route = useRoute();

// 로그인 여부 확인
const checkAccount = async () => { // ②
  const res = await check();

  // 1. 로그인 상태 설정 (토큰 유효성 검사 결과에 따라)
  if (res.status === 200 && res.data === true) {
    // 토큰 유효함: LocalStorage에서 복원된 user 데이터를 사용하며 loggedIn 상태만 true로 설정
    accountStore.setLoggedIn(true, accountStore.user);
  } else {
    // 토큰 유효하지 않거나 만료됨 (res.status=200, res.data=false 또는 401 등)
    accountStore.setLoggedIn(false, null);
  }

  // 2. 💡 인증 체크 완료를 알림: 요청 성공/실패와 관계없이 체크는 완료됨.
  accountStore.setChecked(true);

};

// 커스텀 생성 훅 : checkAccount()를 수행한 후 onCreated()를 수행하라(즉, 화면 렌더링해라)
(async function onCreated() {
  await checkAccount();
})();

// 라우트 경로가 바뀔 때마다 로그인 여부를 확인
watch(() => route.path, () => {
  checkAccount();
});
</script>

<template>
  <!-- 로그인 체크 여부 확인 후 출력-->
  <template v-if="accountStore.checked"> <!-- ③ -->
    <Header v-if="!route.meta.hideHeader" />
    <main>
      <!-- 라우터 뷰 -->
      <router-view></router-view>
    </main>
    <Footer v-if="!route.meta.hideFooter" />
  </template>
</template>