<script setup>
import { computed } from "vue";
import { addItem } from "@/services/cartService";
import { useRouter } from "vue-router";

// 프로퍼티 객체 : 부모컴포넌트로부터 전달받을 데이터 정의, 이름과 데이터 타입을 지정한다.
const props = defineProps({
  // ①
  item: {
    id: Number,
    imgPath: String,
    name: String,
    price: Number,
    discountPer: Number,
  },
});

// 상품 할인가 : 상품의 할인가 컴퓨티드 , 프로퍼티의 데이터를 기준으로 상품의 할인가를
// 자동 계산하고 결괏값을 반환한다. 결괏값에 쉼표를 추가한다.
const computedItemDiscountPrice = computed(() => {
  // ②
  return (
    (
      props.item.price -
      (props.item.price * props.item.discountPer) / 100
    ).toLocaleString() + "원"
  );
});

// 라우터 객체
const router = useRouter(); // ①

// 장바구니에 상품 담기
const put = async () => {
  // ②
  const res = await addItem(props.item.id);

  if (
    res.status === 200 &&
    window.confirm("장바구니에 상품을 담았습니다. 장바구니로 이동하시겠습니까?")
  ) {
    await router.push("/cart");
  }
};
</script>

<template>
  <div class="card shadow-sm">
    <!-- 상품의 사진, 이름, 할인율, 금액 출력 -->
    <!-- 상품 사진 출력 -->
    <span
      class="img"
      :style="{ backgroundImage: `url(${props.item.imgPath})` }"
      :aria-label="`상품 사진(${props.item.name})`"
    ></span>
    <div class="card-body">
      <p class="card-text">
        <!-- 상품 이름 -->
        <span class="me-2">{{ props.item.name }}</span>
        <!-- 상품 할인율 -->
        <span class="discount badge bg-danger"
          >{{ props.item.discountPer }}%</span
        >
      </p>
      <div class="d-flex justify-content-between align-items-center">
        <button class="btn btn-primary btn-sm" @click="put()">
          장바구니 담기
        </button>
        <!-- 상품 정가(숫자 데이터에 3자리마다 쉼표 표기) -->
        <small class="price text-muted"
          >{{ props.item.price.toLocaleString() }}원</small
        >
        <!-- 상품 할인가 -->
        <small class="real text-danger">{{ computedItemDiscountPrice }}</small>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.card {
  .img {
    // ⑤
    display: inline-block;
    width: 100%;
    height: 250px;
    background-size: cover;
    background-position: center;
  }

  .card-body .price {
    // ⑥
    text-decoration: line-through;
  }
}
</style>
