<script setup>
import { useAccountStore } from "@/stores/account";
import { logout } from "@/services/accountService";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import logo from '@/assets/images/logo.png';

const logoUrl = ref(logo);
const accountStore = useAccountStore();
const router = useRouter();

const logoutAccount = async () => {
  const confirmed = window.confirm("정말로 로그아웃 하시겠습니까?");
  if (!confirmed) return;

  try {
    await logout(); // 서버 로그아웃 요청
  } catch (err) {
    console.warn("로그아웃 처리 중 서버 에러 발생 (강제 로그아웃 진행):", err);
  } finally {
    accountStore.clearAccount(); // 스토어 + localStorage 정리
    window.alert("성공적으로 로그아웃되었습니다.");
    router.push("/"); // 홈으로 이동
  }
};

const welcomeMessage = computed(() => {
  return accountStore.user?.name ? `✨ ${accountStore.user.name}님 환영합니다!` : "";
});
</script>

<template>
  <header class="main-header">
    <div class="navbar text-white">
      <div class="container">
        <router-link :to="{ name: 'home' }" class="navbar-brand">
          <strong>
            <img :src="logoUrl" alt="내 로고" class="header-logo" />
            Climb Gallery
          </strong>
        </router-link>
        <div class="menus d-flex gap-4">
          <template v-if="!accountStore.loggedIn">
            <router-link to="/login">로그인</router-link>
            <router-link to="/join">회원가입</router-link>
          </template>
          <template v-else>
            <span v-if="accountStore.user?.name">{{ welcomeMessage }}</span>
            <router-link to="/orders">주문 내역</router-link>
            <router-link to="/cart">장바구니</router-link>
            <a @click="logoutAccount" style="cursor: pointer">로그아웃</a>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<style lang="scss">
@import url('https://cdn.jsdelivr.net/gh/spoqa/spoqa-han-sans@latest/css/spoqa-han-sans-kr.css');
// 구글 폰트: 강인함과 생동감을 위해 'Oswald' 또는 'Anton' 같은 폰트 추천
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@500;700&display=swap');

// --- 전역 헤더 컨테이너 스타일 ---
.main-header {
  // 짙은 산악/야외 배경색 (Dark Slate Gray 또는 짙은 Navy)
  background-color: #2c3e50; /* 짙은 푸른색 계열 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3); /* 더 강조된 그림자 */
  padding: 0.5rem 0; /* 상하 패딩으로 높이 조정 */

  .navbar {
    padding: 0; // 컨테이너 패딩만 사용
  }
}

.container {
  // 로고와 메뉴가 수직 중앙에 오도록 정렬
  align-items: center;
}

// --- 로고 영역 스타일링 ---
.navbar-brand {
  display: flex;
  align-items: center; // 로고 이미지와 텍스트를 수직 중앙 정렬
  color: #ecf0f1 !important; /* 밝은 회색 계열 */
  text-decoration: none;

  .logo-text {
    font-family: 'Oswald', sans-serif; /* 강인한 타이틀 폰트 */
    font-weight: 700;
    font-size: 1.5rem;
    letter-spacing: 1px;
  }
}
/* 💡 로고 이미지에 적용할 스타일 */
.header-logo {
  height: 2.5rem; // 메인 카드보다 조금 작게 조정
  width: auto;
  margin-right: 12px;
  // 로고 주변에 얇은 테두리를 추가하여 시각적 분리 강조
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

// --- 메뉴 영역 스타일링 ---
.menus {
  gap: 1.5rem; /* 간격 확대 (gap-4 대신 명시적 값 사용) */

  a {
    cursor: pointer;
    color: #ecf0f1; // 텍스트 색상
    text-decoration: none;
    font-family: 'Spoqa Han Sans Neo', sans-serif; /* 메뉴는 가독성이 좋은 산세리프 폰트 */
    font-weight: 500;
    transition: color 0.2s, border-bottom 0.2s;

    // 💥 역동적인 호버 효과: 등반 장비 색상(주황 또는 빨강) 사용
    &:hover {
      color: #ff6b6b; /* 생동감 있는 주황빛 빨간색 */
      // 하이라이트 밑줄 추가
      border-bottom: 2px solid #ff6b6b;
      padding-bottom: 3px; /* 밑줄과 텍스트 간격 확보 */
    }

    // 환영 메시지 스타일
    &:first-child:not([href]) {
      color: #95a5a6; /* 환영 메시지는 살짝 톤 다운 */
      cursor: default;
      &:hover {
        color: #95a5a6;
        border-bottom: none;
        padding-bottom: 0;
      }
    }
  }
}
</style>