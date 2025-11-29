<script setup>
import {reactive} from "vue";
import {login} from "@/services/accountService";
import {useRouter} from "vue-router";
import {useAccountStore} from "@/stores/account"; // ① 계정 스토어 객체 생성 시 필요한 메서드 임포트

// 이메일 정규표현식 (회원가입 코드와 동일한 검증 기준 사용)
const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

// 반응형 상태
const state = reactive({ // ①
  form: {
    loginId: "",
    loginPw: "",
  },
  // 유효성 검사 에러 메시지를 관리할 객체 추가
  errors: {
    loginId: "",
    loginPw: "",
  }
});

const router = useRouter();
const accountStore = useAccountStore(); // ② 계정 스토어 객체


// 이메일 유효성 검증 (빈 값 및 형식 검증 추가)
const validateEmail = () => {
  if (!state.form.loginId?.trim()) {
    state.errors.loginId = "이메일을 입력해주세요.";
    return false;
  } else if (!EMAIL_REGEX.test(state.form.loginId)) { // 형식 검증 추가
    state.errors.loginId = "유효하지 않은 이메일 형식입니다.";
    return false;
  }
  state.errors.loginId = "";
  return true;
};

// 비밀번호 유효성 검증
const validatePassword = () => {
  if (!state.form.loginPw?.trim()) {
    state.errors.loginPw = "패스워드를 입력해주세요.";
    return false;
  }
  state.errors.loginPw = "";
  return true;
};

// 로그인 데이터 제출
const submit = async () => {
  // 1. 아이디 및 형식 검증
  if (!validateEmail()) { // validateEmail의 결과를 사용하여 검증
    window.alert(state.errors.loginId);
    document.getElementById("loginId")?.focus();
    return;
  }

  // 2. 비밀번호 입력 검증
  if (!state.form.loginPw?.trim()) {
    window.alert(state.errors.loginPw);
    document.getElementById("loginPw")?.focus();
    return;
  }

  // 모든 클라이언트 측 검증 통과 후, 서버로 데이터 전송
  const res = await login(state.form);
  console.log(res);

  switch (res.status) {
    case 200:
      window.alert("로그인에 성공했습니다.")
      accountStore.setLoggedIn(true,  res.data);// ③ 로그인 성공시 응답받은 데이터(액세스 토큰 제외)를 저장
      accountStore.setAccessToken(res.data.accessToken); // 토큰을 별도 저장
      await router.push("/");
      break;

    case 404: // 아이디 존재 여부 검증 (회원 정보 없음)
      window.alert("존재하지 않는 사용자 ID입니다.");
      state.errors.loginId = "존재하지 않는 사용자 ID입니다."; // 에러 메시지 업데이트
      document.getElementById("loginId")?.focus();
      break;

    case 400: // 비밀번호 불일치 검증 (사용자의 잘못된 요청으로 처리)
      window.alert("비밀번호가 일치하지 않습니다.");
      state.errors.loginPw = "비밀번호가 일치하지 않습니다."; // 에러 메시지 업데이트
      document.getElementById("loginPw")?.focus();
      break;

    default: // 기타 서버 오류
      window.alert("로그인 중 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      break;
  }
};
</script>

<template>
  <div class="login">
    <div class="container"> <!-- ④ -->
      <form class="py-5 d-flex flex-column gap-3" @submit.prevent="submit"> <!-- ⑤ -->
        <h1 class="h5 mb-3">로그인</h1>
        <div class="form-floating">
          <input type="email" class="form-control" id="loginId" placeholder="이메일" v-model="state.form.loginId" @blur="validateEmail" :class="{'is-invalid': state.errors.loginId}"> <!-- ⑥ -->
          <label for="loginId">이메일</label>
          <div v-if="state.errors.loginId" class="invalid-feedback">{{ state.errors.loginId }}</div>
        </div>
        <div class="form-floating">
          <input type="password" class="form-control" id="loginPw" placeholder="패스워드" v-model="state.form.loginPw" @blur="validatePassword" :class="{'is-invalid': state.errors.loginPw}"> <!-- ⑥ -->
          <label for="loginPw">패스워드</label>
          <div v-if="state.errors.loginPw" class="invalid-feedback">{{ state.errors.loginPw }}</div>
        </div>
        <button type="submit" class="w-100 h6 btn py-3 btn-primary">로그인</button> <!-- ⑦ -->
      </form>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login > .container { // ⑧
  max-width: 576px;
}
.login form {
  font-family: 'Noto Serif KR', serif;
}
</style>