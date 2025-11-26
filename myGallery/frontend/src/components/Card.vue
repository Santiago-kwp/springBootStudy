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
    <span class="img" :style="{backgroundImage: `url(${props.item.imgPath})`}"
          :aria-label="`상품 사진(${props.item.name})`"></span>
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
.card {
  .img {
    display: inline-block;
    width: 100%;
    height: 250px;
    background-size: cover;
    background-position: center;
  }

  .card-body .price {
    text-decoration: line-through;
  }

  .card-body .card-text {
    font-family: 'Noto Serif KR', serif;
    font-weight: bold;
  }
  /* 💡 요약문(Summary) 스타일링 */
  .card-summary {
    font-size: 0.9em; /* 텍스트 크기 약간 줄이기 */
    line-height: 1.4; /* 줄 간격 설정 */
    height: 40px; /* 요약문 영역의 높이를 고정하여 카드의 높이 통일 */
    overflow: hidden; /* 혹시 모를 오버플로우 방지 */
    color: #555 !important;
  }
}
</style>