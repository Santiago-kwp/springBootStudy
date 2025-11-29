import { createRouter, createWebHistory } from 'vue-router'

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
      component: () => import('../views/Cart.vue')
    },
    {
      path: '/order',
      name: 'orderForm',
      component: () => import('../views/OrderForm.vue')
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('../views/Orders.vue')
    },
    {
      path: '/orders/:id',
      name: 'orderDetail',
      component: () => import('../views/OrderDetail.vue')
    },
    {
      path: '/item/:id', // 💡 ID를 파라미터로 받습니다.
      name: 'ItemDetail',
      component: () => import('../views/ItemDetail.vue')
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

export default router
