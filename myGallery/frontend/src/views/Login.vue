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
  // 1. 프론트엔드 유효성 검사
  // alert을 띄우지 않고, validate 함수가 설정한 state.errors가 화면에 즉시 반영됨.
  const isEmailValid = validateEmail();
  const isPwValid = validatePassword();

  if (!isEmailValid) {
    document.getElementById("loginId")?.focus();
    return;
  }
  if (!isPwValid) {
    document.getElementById("loginPw")?.focus();
    return;
  }

  try {
    // 2. 서버 요청 시도
    const res = await login(state.form);

    // 로그인 성공 -> 스토어 업데이트
    accountStore.setChecked(true);
    accountStore.setLoggedIn(true,  res.data);// ③ 로그인 성공시 응답받은 데이터(액세스 토큰 제외)를 저장
    accountStore.setAccessToken(res.data.accessToken); // 토큰을 별도 저장

    // UX 개선: alert 대신 헤더 환영 메시지로 대체 가능
    window.alert("로그인에 성공했습니다.");

    await router.push({
      name: 'home'
    });
  } catch (err) {
    const status = err.response?.status;

    if (status === 401) {
      // 백엔드에서 ID가 없든, 비번이 틀리든 전부 401을 리턴함
      // alert 대신 입력창 아래에 에러 메시지를 표시하여 UX 개선
      const msg = "아이디 또는 비밀번호가 일치하지 않습니다.";

      state.errors.loginId = " "; // 빨간 테두리를 위해 값 설정 (메시지는 비움)
      state.errors.loginPw = msg; // 비밀번호 쪽에 메시지 표시

      state.form.loginPw = ""; // 비번 입력창 비우기
      document.getElementById("loginPw")?.focus();
    }
    else if (status === 500) {
      window.alert("서버 오류가 발생했습니다. 관리자에게 문의해주세요.");
    }
    else {
      window.alert("알 수 없는 오류가 발생했습니다.");
    }
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