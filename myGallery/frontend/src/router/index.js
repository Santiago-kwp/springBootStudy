import { createRouter, createWebHistory } from 'vue-router'
import { useAccountStore } from '@/stores/account'
import { check } from '@/services/accountService'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/',
      name: 'landingPage',
      component: () => import('../views/LandingPage.vue'),
      // 💡 여기에 meta 정보를 추가합니다.
      meta: {
        hideHeader: true,
        hideFooter: true // 푸터도 숨기고 싶다면 추가
      }
    },
    {
      path: '/join',
      name: 'join',
      component: () => import('../views/Join.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('../views/Cart.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/order',
      name: 'orderForm',
      component: () => import('../views/OrderForm.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('../views/Orders.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/:id',
      name: 'orderDetail',
      component: () => import('../views/OrderDetail.vue'),
    },
    {
      path: '/item/:id', // 💡 ID를 파라미터로 받습니다.
      name: 'ItemDetail',
      component: () => import('../views/ItemDetail.vue'),
    }
  ],
  // 💡 스크롤 동작을 강제로 맨 위로 리셋하도록 명시
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      // 뒤로 가기/앞으로 가기 시 저장된 위치 복원
      return savedPosition;
    } else {
      // 새 페이지 이동 시 스크롤을 맨 위로 설정
      return { top: 0, behavior: 'smooth' };
    }
  }
})

// 전역 라우터 가드
router.beforeEach(async (to, from, next) => {
  const accountStore = useAccountStore();

  if (!to.meta.requiresAuth) {
    return next();
  }

  // 토큰 존재만 확인
  const token = accountStore.accessToken || localStorage.getItem('accessToken');
  if (!token) {
    accountStore.clearAccount();
    return next('/login');
  }

  try {
    const res = await check();
    if (res.status === 200 && res.data.loggedIn === true) {
      accountStore.setLoggedIn(true, accountStore.user || {});
      return next();
    } else {
      window.alert("로그인 세션이 만료되었습니다.");
      accountStore.clearAccount(); // 세션 만료 시 초기화
      return next('/login');
    }
  } catch (err) {
    accountStore.clearAccount();   // 에러 시에도 초기화
    return next('/login');
  }
});

export default router
