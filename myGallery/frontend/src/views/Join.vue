<script setup>
import {reactive} from "vue";
import {join} from "@/services/accountService";
import {useRouter} from "vue-router";

// 정규표현식: 일반적인 이메일 형식 (user@domain.com) 검증
const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

// 반응형 상태
const state = reactive({ // ①
  form: {
    name: "",
    loginId: "",
    loginPw: "",
    loginPwConfirm: "",
  },
  // 유효성 검사 에러 메시지를 관리할 객체 추가
  errors: {
    name: "",
    loginId: "",
    loginPw: "",
    loginPwConfirm: "",
  }
});

// 라우터 객체
const router = useRouter();

// 이메일 유효성 검증
const validateEmail = () => {
  if (!state.form.loginId?.trim()) {
    state.errors.loginId = "이메일을 입력해주세요.";
    return false;
  } else if (!EMAIL_REGEX.test(state.form.loginId)) { // 정규표현식을 이용한 형식 검사
    state.errors.loginId = "유효하지 않은 이메일 형식입니다.";
    return false;
  }
  state.errors.loginId = "";
  return true;
};

// 비밀번호 유효성 검증
const validatePassword = () => {
  let isValid = true;

  // 1. 비밀번호 입력 검증
  if (!state.form.loginPw?.trim()) {
    state.errors.loginPw = "패스워드를 입력해주세요.";
    isValid = false;
  } else {
    state.errors.loginPw = "";
  }

  // 2. 비밀번호 확인 입력 검증 및 일치 검증
  if (!state.form.loginPwConfirm?.trim()) {
    state.errors.loginPwConfirm = "패스워드 확인을 입력해주세요.";
    isValid = false;
  } else if (state.form.loginPw !== state.form.loginPwConfirm) { // 비밀번호 일치 검증
    state.errors.loginPwConfirm = "패스워드가 일치하지 않습니다.";
    isValid = false;
  } else {
    state.errors.loginPwConfirm = "";
  }

  // 비밀번호 입력과 확인 모두 문제가 없어야 최종 유효성 통과
  return isValid && !state.errors.loginPw && !state.errors.loginPwConfirm;
};


// 회원가입 데이터 제출
const submit = async () => {
  // 1. 이름 입력 검증
  if (!state.form.name?.trim()) {
    window.alert("이름을 입력해주세요.");
    document.getElementById("name")?.focus();
    return;
  }

  // 2. 이메일 유효성 검사 (정규표현식 포함)
  if (!validateEmail()) {
    window.alert(state.errors.loginId);
    document.getElementById("loginId")?.focus();
    return;
  }

  // 3. 비밀번호 일치 및 입력 검증
  if (!validatePassword()) {
    // 가장 먼저 발생한 에러 메시지를 alert
    const firstError = state.errors.loginPw || state.errors.loginPwConfirm;
    window.alert(firstError);
    // 비밀번호 또는 비밀번호 확인 필드에 포커스
    document.getElementById(state.errors.loginPw ? "loginPw" : "loginPwConfirm")?.focus();
    return;
  }

  // 모든 클라이언트 측 검증 통과 후, 서버로 데이터 전송
  // 서버에는 이름, 로그인 ID, 비밀번호만 보냄 (확인용 필드는 제외)
  const submitForm = {
    name: state.form.name,
    loginId: state.form.loginId,
    loginPw: state.form.loginPw,
  };

  const res = await join(submitForm);

  if (res.status === 200) {
    window.alert("회원가입을 완료했습니다.");
    await router.push("/");
  } else if (res.status === 409) { // 409 conflict
    window.alert("이미 사용 중인 이메일입니다. 다른 값을 입력해주세요.");
    document.getElementById("loginId")?.focus();
  }
};
</script>

<template>
  <div class="join">
    <div class="container"> <!-- ④ -->
      <form class="py-5 d-flex flex-column gap-3" @submit.prevent="submit"> <!-- ⑤ -->
        <h1 class="h5 mb-3">회원가입</h1>
        <div class="form-floating">
          <input type="text" class="form-control" id="name" placeholder="이름" v-model="state.form.name">  <!-- ⑥ -->
          <label for="name">이름</label>
        </div>
        <div class="form-floating">
          <input type="email" class="form-control" id="loginId" placeholder="이메일" v-model="state.form.loginId" @blur="validateEmail"> <!-- ⑥ -->
          <label for="loginId">이메일</label>
          <div v-if="state.errors.loginId" class="text-danger small mt-1">{{ state.errors.loginId }}</div>
        </div>
        <div class="form-floating">
          <input type="password" class="form-control" id="loginPw" placeholder="패스워드" v-model="state.form.loginPw" @blur="validatePassword"> <!-- ⑥ -->
          <label for="loginPw">패스워드</label>
          <div v-if="state.errors.loginPw" class="text-danger small mt-1">{{ state.errors.loginPw }}</div>
        </div>
        <div class="form-floating">
          <input type="password" class="form-control" id="loginPwConfirm" placeholder="패스워드 확인" v-model="state.form.loginPwConfirm" @blur="validatePassword"> <!-- ⑥ -->
          <label for="loginPwConfirm">패스워드 확인</label>
          <div v-if="state.errors.loginPwConfirm" class="text-danger small mt-1">{{ state.errors.loginPwConfirm }}</div>
        </div>

        <button type="submit" class="w-100 h6 btn py-3 btn-primary">회원가입</button> <!-- ⑦ -->
      </form>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.join > .container { // ⑧
  max-width: 576px;
}
.join form {
  font-family: 'Noto Serif KR', serif;
}
</style>