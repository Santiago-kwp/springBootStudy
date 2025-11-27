<script setup>
  import { reactive, onMounted, computed } from 'vue';
  import { useRoute } from 'vue-router';
  import { getItem } from "@/services/itemService"; // 💡 상세 조회 API 호출 함수 (가정)

  // 라우트 객체
  const route = useRoute();
  // 현재 상품 ID
  const itemId = route.params.id;

  // 반응형 상태
  const state = reactive({
  item: null, // ItemDetail DTO 객체를 저장
  loading: true,
  error: null,
});

  // 할인 적용된 실제 가격 계산
  const computedRealPrice = computed(() => {
  if (!state.item) return '0원';
  const realPrice = state.item.price - (state.item.price * state.item.discountPer / 100);
  return realPrice.toLocaleString() + '원';
});

  // 데이터 로딩 함수
  const fetchItemDetail = async () => {
  try {
  state.loading = true;
  const res = await getItem(itemId); // ItemService에 getItemDetail 함수가 구현되어 있어야 합니다.

  if (res.status === 200) {
  state.item = res.data;
} else {
  state.error = '작품 정보를 불러오는데 실패했습니다.';
}
} catch (e) {
  state.error = '서버 통신 오류가 발생했습니다.';
} finally {
  state.loading = false;
}
};

  // 컴포넌트가 마운트될 때 데이터 로딩 시작
  onMounted(() => {
  fetchItemDetail();
});
</script>

<template>
  <div class="item-detail-view py-5">
    <div class="container">

      <div v-if="state.loading" class="text-center py-5">작품 정보를 불러오는 중입니다...</div>
      <div v-else-if="state.error" class="alert alert-danger">{{ state.error }}</div>

      <div v-else-if="state.item" class="row">

        <div class="col-md-6 mb-4">
          <img :src="state.item.imgPath" :alt="state.item.name" class="img-fluid rounded shadow-sm detail-image"/>
        </div>

        <div class="col-md-6">
          <h1 class="mb-3">{{ state.item.name }}</h1>
          <hr>

          <div class="d-flex align-items-baseline mb-4">
            <h2 class="text-danger me-3">{{ computedRealPrice }}</h2>
            <span class="text-muted text-decoration-line-through me-2">
              {{ state.item.price.toLocaleString() }}원
            </span>
            <span class="badge bg-success fs-6">{{ state.item.discountPer }}% 할인</span>
          </div>

          <div class="mb-5">
            <button class="btn btn-lg btn-primary me-2">바로 구매</button>
            <button class="btn btn-lg btn-outline-secondary">장바구니 담기</button>
          </div>

          <h4 class="mb-3 border-bottom pb-2">작품 해설 및 스토리 📜</h4>
          <p class="description-text lead text-break">{{ state.item.description }}</p>

        </div>
      </div>

      <div class="text-center mt-5">
        <router-link to="/" class="btn btn-outline-dark">갤러리 목록으로 돌아가기</router-link>
      </div>

    </div>
  </div>
</template>

<style scoped>
.detail-image {
  max-height: 70vh; /* 뷰포트 높이의 70%를 넘지 않게 제한 */
  width: 100%;
  object-fit: cover; /* 이미지가 잘리더라도 영역을 채우도록 */
}

/* 설명 텍스트 스타일 */
.description-text {
  white-space: pre-wrap; /* 줄바꿈을 포함하여 렌더링 */
  line-height: 1.8;
  color: #333;
  font-size: 1.1rem;
}

/* 제목 스타일 */
h1 {
  font-family: 'Noto Serif KR', serif;
  font-weight: 700;
}
</style>