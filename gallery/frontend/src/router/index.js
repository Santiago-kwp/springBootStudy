// 회원 가입 컴포넌트를 라우터에 연결 : 사용자가 /join 접속시 회원가입페이지 출력
// http://localhost:5173/join

import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: () => import("../views/Home.vue"),
    },
    {
      path: "/join",
      name: "join",
      component: () => import("../views/Join.vue"),
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/Login.vue"),
    },
  ],
});

export default router;
