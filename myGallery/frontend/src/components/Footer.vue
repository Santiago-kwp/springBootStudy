<template>
  <footer class="mt-4 py-5 bg-light border-top"> <div class="container">
    <div class="row">

      <div class="col-md-4 mb-3">
        <h5 class="fw-bold">프로젝트 정보 ✨</h5>
        <p class="text-muted small">
          본 프로젝트는 KDT-SSG9th 교육 과정의 결과물입니다. <br>
          저작권 및 문의사항은 아래 연락처를 이용해주시기 바랍니다.
        </p>
        <ul class="list-unstyled small">
          <li>📧 Email: support@example.com</li>
          <li>📞 Phone: 02-1234-5678</li>
        </ul>
      </div>

      <div class="col-md-4 mb-3">
        <h5 class="fw-bold">바로가기</h5>
        <ul class="list-unstyled">
          <li><router-link to="/" class="text-muted text-decoration-none">갤러리 메인</router-link></li>
          <li><router-link to="/cart" class="text-muted text-decoration-none">장바구니</router-link></li>
          <li><router-link to="#" class="text-muted text-decoration-none">마이페이지</router-link></li>
          <li><router-link to="#" class="text-muted text-decoration-none">이용약관</router-link></li>
        </ul>
      </div>

      <div class="col-md-4 mb-3">
        <h5 class="fw-bold">기술 스택 💻</h5>
        <p class="text-muted small">
          Vue 3, Pinia, Vite, Spring Boot, MySQL.
        </p>

        <h5 class="fw-bold mt-4">Follow Us</h5>
        <ul class="list-inline">
          <li class="list-inline-item me-3"><a href="https://github.com/Santiago-kwp/springBootStudy/tree/main/myGallery" target="_blank" class="text-muted">Github</a></li>
        </ul>
      </div>
    </div>

    <hr> <div class="text-center small text-muted pt-3">
    &copy; {{ new Date().getFullYear() }}. KDT-SSG9th. & KWP All rights reserved.
  </div>
  </div>
  </footer>
</template>