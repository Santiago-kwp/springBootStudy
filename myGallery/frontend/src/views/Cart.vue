<script setup>
import {getItems, removeAll, removeItem} from "@/services/cartService"
import {reactive, computed } from "vue";
import {useRouter} from "vue-router";

const router = useRouter(); // 선택한 장바구니 아이템만 가지고 OrderForm


// 반응형 상태
const state = reactive({ // ①
  items: []
});

// 장바구니 상품 조회
const load = async () => { // ②
  const res = await getItems();

  if (res.status === 200) {
    // 아이템을 가져온 후 'checked: true' 속성을 추가합니다.
    state.items = res.data.map(item => ({
      ...item,
      checked: true // 기본적으로 모두 선택된 상태로 시작
    }));
  }
}

// 선택된 아이템의 ID 목록
const computedCheckedIds = computed(() => {
  return state.items
  .filter(i => i.checked) // checked가 true인 아이템만 필터링
  .map(i => i.id); // 해당 아이템의 ID만 추출
});

// 장바구니 상품 삭제
const remove = async (itemId) => { // ③
  const res = await removeItem(itemId);

  if (res.status === 200) {
    window.alert("선택하신 장바구니의 상품을 삭제했습니다.");
    await load();
  }
}

// 장바구니 상품 전체 삭제
const removeAllItems = async () => {
  const res = await removeAll();

  if (res.status === 200) {
    window.alert("장바구니 전체 상품을 삭제했습니다.")
    await load();
  }
}

// '주문하기' 버튼 클릭 시 선택된 아이템 ID를 전달하는 함수
const navigateToOrder = () => {
  const checkedIds = computedCheckedIds.value;

  if (checkedIds.length === 0) {
    window.alert("선택된 상품이 없습니다.");
    return;
  }

  // 선택된 ID 목록을 쿼리 파라미터로 '/order' 페이지에 전달합니다.
  // 예: /order?itemIds=101,102
  router.push({
    name: 'orderForm',
    query: {
      itemIds: checkedIds.join(',')
    }
  });
};

// 커스텀 생성 훅
(async function onCreated() { // ④
  await load();
})();
</script>

<template>
  <div class="cart">
    <div class="container"> <!-- ⑤ -->
      <template v-if="state.items.length">
        <div class="d-flex justify-content-end mb-2 mt-4">
          <button class="btn btn-sm btn-outline-danger" @click="removeAllItems">
            장바구니 전체 비우기
          </button>
        </div>
        <ul class="items"> <!-- ⑥ -->
          <li v-for="i in state.items">
            <input type="checkbox" v-model="i.checked" class="form-check-input me-3">

            <img :alt="`상품 사진(${i.name})`" :src="i.imgPath"/>
            <b class="name">{{ i.name }}</b>
            <span class="price">
            {{ (i.price - i.price * i.discountPer / 100).toLocaleString() }}원
          </span>
            <span class="remove float-end" @click="remove(i.id)" title="삭제">&times;</span>
          </li>
        </ul>
        <div class="act mb-2 d-flex justify-content-center">
          <button class="btn btn-primary" @click="navigateToOrder">선택 상품 주문하기</button>
        </div>
        <div class="d-flex justify-content-end">
          <router-link :to="{ name: 'home' }" class="btn btn-secondary">갤러리 이동</router-link>
        </div>
      </template>
      <div class="text-center py-5" v-else>장바구니가 비어있습니다.</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.cart {
  .items {
    list-style: none;
    margin: 0;
    padding: 0;

    li { // ⑦
      border: 1px solid #eee;
      margin-top: 25px;
      margin-bottom: 25px;
    }

    img { // ⑧
      width: 150px;
      height: 150px;
    }

    .name {
      margin-left: 25px;
    }

    .price {
      margin-left: 25px;
    }

    .remove {
      cursor: pointer;
      font-size: 30px;
      padding: 5px 15px;
    }
  }

  .act .btn { // ⑨
    width: 300px;
    display: block;
    margin: 0 auto;
    padding: 30px 50px;
    font-size: 20px;
  }
}
</style>