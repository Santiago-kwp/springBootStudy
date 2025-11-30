<script setup>
import {getItems} from "@/services/itemService";
import {reactive, onMounted, onUnmounted, ref} from "vue"; // 💡 onMounted, onUnmounted, ref 추가
import Card from "@/components/Card.vue";

// 반응형 상태
// 💡 1. 페이지네이션 상태 관리
const state = reactive({
  items: [],
  args: {
    page: 0,
    size: 8
  },
  isLast: false, // 마지막 페이지인지 여부
  isLoading: false, // 로딩 상태 플래그
});

// 💡 2. 데이터 로드 함수 정의
const loadItems = async () => {
  // 이미 로딩 중이거나 마지막 페이지라면 추가 로드 방지
  if (state.isLoading || state.isLast) return;

  state.isLoading = true; // 로딩 시작

  try {
    const res = await getItems(state.args);

    if (res.status === 200) {

      const newItems = res.data.content;
      state.items.push(...newItems);

      state.isLast = res.data.last;
      state.args.page++; // 다음 페이지 번호 준비
    }
  } catch (error) {
    console.error("상품 로드 중 오류 발생:", error);
  } finally {
    state.isLoading = false; // 로딩 완료
  }
};

// 💡 4. 스크롤 이벤트 리스너 정의
const handleScroll = () => {
  // 문서의 전체 높이
  const documentHeight = document.documentElement.scrollHeight;
  // 현재 스크롤 위치 (뷰포트 상단에서 문서 상단까지의 거리)
  const scrollTop = document.documentElement.scrollTop;
  // 뷰포트의 높이
  const clientHeight = document.documentElement.clientHeight;

  // 뷰포트가 문서의 맨 아래에 도달했는지 확인하는 조건
  // (scrollTop + clientHeight >= documentHeight)
  // 일반적으로 여유 공간을 두어 미리 로드합니다. (예: 맨 아래에서 100px 위)
  const nearBottom = (scrollTop + clientHeight + 100) >= documentHeight;

  if (nearBottom) {
    loadItems();
  }
};

// 💡 5. 컴포넌트 마운트 시 초기 데이터 로드 및 이벤트 리스너 등록
onMounted(() => {
  loadItems(); // 첫 페이지 데이터 로드
  window.addEventListener('scroll', handleScroll); // 스크롤 이벤트 리스너 등록
});

// 💡 6. 컴포넌트 언마운트 시 이벤트 리스너 제거 (메모리 누수 방지)
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<template>
  <div class="home">
    <div class="album py-5 bg-light"> <!-- ③ 상품 목록 출력 요소 상품개수만큼 반복하여 카드 컴포넌트에 출력하고, 카드 컴포넌트에 상품 데이터 전달-->
      <div class="container"> <!-- ④  템플릿의 핵심요소를 감싸는 컨테이너 max-width 설정-->
        <div class="row row-cols-1 row-cols-lg-2 row-cols-xl-4 g-4"> <!-- ⑤ 열을 나타내는 요소 row-cols-* 클래스로 화면크기에 따라 하나의 열에 몇개의 컬럼을 출력할지 지정, g-3는 열간의 수직,수평 간격 적용-->
          <div class="col" v-for="item in state.items"> <!-- ⑥ 상품 목록 출력-->
            <Card :item="item"/> <!-- ⑦ item 속성에 각 상품 데이터(item)을 입력해 전달-->
          </div>
        </div>
      </div>
      <div class="text-center py-4">
        <div v-if="state.isLoading" class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p v-else-if="state.isLast && state.items.length > 0" class="text-muted">
          더 이상 작품이 없습니다. 🖼️
        </p>
      </div>
    </div>
  </div>
</template>