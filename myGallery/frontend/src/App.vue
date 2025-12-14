<script setup>
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { useRoute } from "vue-router";

const route = useRoute();
</script>

<template>
  <Header v-if="!route.meta.hideHeader" />
  <main>
    <!-- 라우터 뷰 -->
    <router-view />
  </main>
  <Footer v-if="!route.meta.hideFooter" />
</template>
