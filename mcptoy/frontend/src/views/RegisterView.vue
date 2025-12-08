<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <h2 class="mt-5">회원가입</h2>
        <form @submit.prevent="register">
          <div class="mb-3">
            <label for="name" class="form-label">이름</label>
            <input type="text" class="form-control" id="name" v-model="form.name" required>
          </div>
          <div class="mb-3">
            <label for="loginId" class="form-label">로그인 ID</label>
            <input type="text" class="form-control" id="loginId" v-model="form.loginId" required>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">비밀번호</label>
            <input type="password" class="form-control" id="password" v-model="form.loginPw" required>
          </div>
          <button type="submit" class="btn btn-primary">가입하기</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const form = reactive({
  name: '',
  loginId: '',
  loginPw: ''
});

const register = async () => {
  try {
    await axios.post('/v1/api/account/join', form);
    alert('회원가입이 완료되었습니다.');
    router.push('/login');
  } catch (error) {
    console.error('Error registering:', error);
    alert('회원가입에 실패했습니다.');
  }
};
</script>

<style scoped>
</style>
