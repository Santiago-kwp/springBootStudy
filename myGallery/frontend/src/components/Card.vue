<script setup>
import {addItem} from "@/services/cartService";
import {useRouter} from "vue-router";
import {computed} from "vue";
import { useAccountStore } from "@/stores/account"; // 💡 Pinia 스토어 임포트
import { storeToRefs } from "pinia"; // pinia의 storeToRefs 임포트

// 💡 1. Pinia 스토어에서 loggedIn 상태 가져오기
const accountStore = useAccountStore();
const { loggedIn } = storeToRefs(accountStore); // loggedIn을 반응적으로 가져옴

const props = defineProps({
  item: {
    id: Number,
    imgPath: String,
    name: String,
    summary: String,
    price: Number,
    discountPer: Number
  }
});

// 상품 할인가
const computedItemDiscountPrice = computed(() => {
  return (props.item.price - (props.item.price * props.item.discountPer / 100)).toLocaleString() + '원';
})

// 라우터 객체
const router = useRouter(); // ①

// 장바구니에 상품 담기
const put = async () => { // ②
  const res = await addItem(props.item.id);

  if (res.status === 200 && window.confirm('장바구니에 상품을 담았습니다. 장바구니로 이동하시겠습니까?')) {
    await router.push("/cart");
  }
};
</script>

<template>
  <div class="card shadow-sm">
    <!-- 상품 사진 출력 -->
    <router-link :to="{ name: 'ItemDetail', params: { id: props.item.id } }">
      <span class="img" :style="{backgroundImage: `url(${props.item.imgPath})`}"
            :aria-label="`상품 사진(${props.item.name})`"></span>
    </router-link>

    <div class="card-body">
      <p class="card-text">
        <!-- 상품 이름 -->
        <span class="me-2">{{ props.item.name }}</span>
        <!-- 상품 할인율 -->
        <span class="discount badge bg-danger">{{ props.item.discountPer }}%</span>
      </p>

      <p class="card-summary text-muted mb-3">
        {{ props.item.summary }}
      </p>

      <div class="d-flex justify-content-between align-items-center">
        <button v-if="loggedIn" class="btn btn-primary btn-sm" @click="put()">🛒</button>
        <!-- 상품 정가(숫자 데이터에 3자리마다 쉼표 표기) -->
        <small class="price text-muted">{{ props.item.price.toLocaleString() }}원</small>
        <!-- 상품 할인가 -->
        <small class="real text-danger">{{ computedItemDiscountPrice }}</small>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
// 폰트 임포트
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@500;700&display=swap');
.card {
  // 기본 카드 스타일링
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  overflow: hidden; // 호버 시 이미지 확대 효과를 위해 필요
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  // 💥 호버 시 역동적인 효과: 카드가 살짝 떠오름
  &:hover {
    transform: translateY(-5px); /* 약간 위로 이동 */
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2); /* 더 짙은 그림자 */
  }

  // --- 이미지 영역 스타일링 ---
  a {
    display: block;
    overflow: hidden; // 자식 요소의 확대 효과를 가두기
  }

  .img {
    display: inline-block;
    width: 100%;
    // 💡 이미지 비율 변경: 등반 사진의 광활함을 표현하기 위해 가로가 긴 비율 (예: 4:3 대신 16:9)
    height: 0;
    padding-bottom: 65%; // 4:3 (75%) 보다는 16:10 (62.5%) 또는 16:9 (56.25%)가 더 와이드함.
    // 65% (약 1.5:1) 비율로 설정
    background-size: cover;
    background-position: center;
    transition: transform 0.4s ease; /* 확대 효과에 트랜지션 추가 */
  }

  // 💥 호버 시 역동적인 효과: 이미지 확대 (Zoom In)
  &:hover .img {
    transform: scale(1.05); /* 5% 확대 */
  }

  .card-body {
    padding: 1rem;
    // 상품 이름 및 할인율
    .price {
      text-decoration: line-through;
      font-size: 0.85em;
      color: #aaa !important;
    }

    .card-text {
      font-family: 'Noto Sans KR', sans-serif; // 폰트 변경
      font-weight: 700; // 더 두껍게
      margin-bottom: 0.5rem;

      .discount {
        // 할인율 뱃지 생동감 부여 (주황색 계열로 대비 강조)
        background-color: #ff5722 !important; /* Material Design Orange A400 */
        font-weight: 700;
        border-radius: 4px;
      }

    }
  }

  .real {
    font-size: 1.1em;
    font-weight: 700;
    color: #d32f2f !important; /* 더 강조된 빨간색 */
  }

  .btn-primary {
    // 장바구니 버튼 생동감 부여
    background-color: #4CAF50; /* 등반/자연과 어울리는 녹색 계열 */
    border-color: #4CAF50;
    font-weight: bold;
    transition: background-color 0.2s;

    &:hover {
      background-color: #388E3C;
      border-color: #388E3C;
    }
  }

  /* 💡 요약문(Summary) 스타일링 */
  .card-summary {
    font-size: 0.85em;
    font-family: 'Noto Sans KR', sans-serif;
    font-weight: 500;
    line-height: 1.4;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2; /* 2줄로 제한 */
    -webkit-box-orient: vertical;
    color: #666 !important;
    height: 2.4em; /* 2줄 높이 확보 */
  }

}
</style>