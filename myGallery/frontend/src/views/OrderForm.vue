<script setup>
import {computed, reactive} from "vue";
import {addOrder} from "@/services/orderService";
import {useRoute,  useRouter} from "vue-router";
// import {getItems} from "@/services/cartService"; // 카트에서 상품 목록 조회..
import {getItemList} from "@/services/itemService"; // 아이템 서비스에서 상품 목록 조회로 임시 변경
import {useAccountStore} from "@/stores/account";

const route = useRoute(); // useRoute 임포트 및 사용
const accountStore = useAccountStore();
// 라우터 객체
const router = useRouter(); // ①

// 반응형 상태
const state = reactive({ // ②
  items: [],
  form: {
    name: accountStore.user.name,
    address: "",
    payment: "card",
    cardNumber: "",
  }
});

// 최종 결제 금액
const computedTotalPrice = computed(() => { // ③
  let result = 0;

  state.items.forEach((i) => {
    result += i.price - i.price * i.discountPer / 100;
  });

  return result;
});

// 주문 데이터 제출
const submit = async () => { // ④
  if (state.form.payment !== "card") { // 결제 수단이 카드가 아니면
    state.form.cardNumber = "";
  }

  state.form.itemIds = state.items.map(i => i.id);
  const res = await addOrder(state.form);

  if (res.status === 200) {
    const messages = ["주문이 완료되었습니다."];

    if (state.form.payment === "bank") {
      const price = computedTotalPrice.value.toLocaleString();
      messages.push(`한국은행 123-456789-777 계좌로 ${price}원을 입금해주시기 바랍니다.`);
    }

    window.alert(messages.join("\n"));
    await router.push({
      name: 'home',
    });
  }
};

// 커스텀 생성 훅
(async function onCreated() { // ⑤
  // 쿼리 파라미터에서 itemIds 문자열('1,2')을 가져와 배열로 변환
  const selectedItemIds = route.query.itemIds ? route.query.itemIds.split(',') : [];

  if (selectedItemIds.length === 0) {
    // 경고 메시지 또는 장바구니 페이지로 리다이렉트 처리
    // 예: router.push('/cart');
    console.error("주문할 상품 ID가 없습니다.");
    return;
  }

  const res = await getItemList(selectedItemIds); // 임시로 전체를 가져온 후 필터링 => 여기서 getItems는 itemService의 getItems가 아니라 cartService의 getItems 였음.. ㅠㅠ
  if (res.status === 200) {
    state.items = res.data;
  }
})();
</script>

<template>
  <form class="order-form" @submit.prevent="submit"> <!-- ⑥ -->
    <div class="container"> <!-- ⑦ -->
      <div class="py-5 text-center">
        <div class="h4">
          <b>주문하기</b>
        </div>
        <p class="h6 font-lg mt-3">주문 내역을 확인하시고 주문하기 버튼을 클릭해주세요</p>
      </div>
      <div class="row g-5">
        <div class="col-md-5 col-lg-4 order-md-last">
          <div class="mb-3">
            <span class="h5 mb-3 align-middle me-2">
              <b>구입 목록</b>
            </span>
            <span class="badge bg-primary rounded-pill align-middle">{{ state.items.length }}</span>
          </div>
          <ul class="items list-group mb-3"> <!-- ⑧ -->
            <li class="p-3 list-group-item d-flex justify-content-between" v-for="i in state.items">
              <div>
                <h6 class="my-0">{{ i.name }}</h6>
              </div>
              <span class="text-muted">
                {{ (i.price - i.price * i.discountPer / 100).toLocaleString() }}원
              </span>
            </li>
          </ul>
          <div class="border p-4 bg-light h5 rounded text-center total-price">
            <span>합계 </span>
            <b>{{ computedTotalPrice.toLocaleString() }}원</b>
          </div>
          <button type="submit" class="w-100 btn btn-primary py-4 mt-4">결제하기</button> <!-- ⑨ -->
        </div>
        <div class="col-md-7 col-lg-8">
          <div class="h5 mb-3">
            <b>주문자 정보</b>
          </div>
          <div class="row g-3">
            <div class="col-12">
              <label for="name" class="form-label">이름</label>
              <input type="text" class="form-control p-3" id="name" v-model="state.form.name"/> <!-- ⑩ -->
            </div>
            <div class="col-12 pt-1">
              <label for="address" class="form-label">주소</label>
              <input type="text" class="form-control p-3" id="address" v-model="state.form.address"/> <!-- ⑩ -->
            </div>
          </div>
          <div class="h5 mt-5 mb-3">
            <b>결제 수단</b>
          </div>
          <div class="my-3">
            <div class="form-check">
              <input id="card" name="paymentMethod" type="radio" class="form-check-input" value="card" v-model="state.form.payment"> <!-- ⑩ -->
              <label class="form-check-label" for="card">신용카드</label>
            </div>
            <div class="form-check">
              <input id="bank" name="paymentMethod" type="radio" class="form-check-input" value="bank" v-model="state.form.payment"> <!-- ⑩ -->
              <label class="form-check-label" for="bank">무통장입금</label>
            </div>
          </div>
          <div class="pt-1" v-if="state.form.payment === 'card'">
            <label for="cardNum" class="form-label">카드 번호</label>
            <input type="text" class="form-control p-3" id="cardNum" v-model="state.form.cardNumber"> <!-- ⑩ -->
          </div>
        </div>
      </div>
    </div>
  </form>
</template>