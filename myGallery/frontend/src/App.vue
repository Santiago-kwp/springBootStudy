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

  if (res.status === 200) {
    accountStore.setChecked(true);

    // 💡 res.data는 MemberLogin 객체(로그인 시) 또는 false(로그아웃 시)입니다.
    const userData = res.data;
    const isLoggedIn = userData && userData.loginId;
    accountStore.setLoggedIn(isLoggedIn, isLoggedIn ? userData : null);

  } else {
    accountStore.setChecked(false);
    accountStore.setLoggedIn(false);
  }
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
    <Header/>
    <main>
      <!-- 라우터 뷰 -->
      <router-view></router-view>
    </main>
    <Footer/>
  </template>
</template>