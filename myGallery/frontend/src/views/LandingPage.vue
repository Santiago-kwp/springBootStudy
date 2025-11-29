<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 1. 랜딩 페이지에 보여줄 하이라이트 작품 데이터 (정적인 베스트 컷)
const slides = [
  {
    id: 1,
    title: "한계, 그 너머의 세상",
    subTitle: "Limitless Horizon",
    description: "숨이 턱 끝까지 차오르는 순간, 비로소 마주하는 압도적인 풍경. 당신의 도전을 기다립니다.",
    image: "/img/001.jpg", // 야간등반
    position: "center"
  },
  {
    id: 2,
    title: "침묵하는 설산의 위로",
    subTitle: "Silent Winter",
    description: "차가운 바람만이 맴도는 고요한 설산. 그 순백의 시간 속에서 내면의 평화를 찾으세요.",
    image: "/img/002.jpg", // 겨울산
    position: "center"
  },
  {
    id: 3,
    title: "중력을 거스르는 춤",
    subTitle: "Vertical Dance",
    description: "손끝에 전해지는 바위의 거친 질감. 오직 당신과 벽만이 존재하는 몰입의 순간입니다.",
    image: "/img/004.jpg", // 절벽등반
    position: "top"
  },
  {
    id: 4,
    title: "함께 오르는 믿음",
    subTitle: "Trust & Teamwork",
    description: "로프 하나에 의지한 채 서로를 믿고 오르는 여정. 협동이 만들어내는 아름다운 드라마.",
    image: "/img/005.jpg", // 멀티등반
    position: "center"
  }
];

// 2. 슬라이더 상태 관리
const currentIndex = ref(0);
const timer = ref(null);

// 모든 슬라이드 이미지 경로
const imagePaths = slides.map(slide => slide.image);

// 이미지 프리로딩 함수
const preloadImages = () => {
  imagePaths.forEach(path => {
    const img = new Image();
    img.src = path;
    // 이미지 로드가 완료되면 브라우저 캐시에 저장됩니다.
  });
};

// 현재 슬라이드 데이터
const currentSlide = computed(() => slides[currentIndex.value]);

// 다음 슬라이드로 이동
const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % slides.length;
};

// 이전 슬라이드로 이동
const prevSlide = () => {
  currentIndex.value = (currentIndex.value - 1 + slides.length) % slides.length;
};

// 자동 슬라이드 시작
const startAutoSlide = () => {
  timer.value = setInterval(nextSlide, 5000); // 5초마다 변경
};

// 자동 슬라이드 정지 (마우스 호버 시 등)
const stopAutoSlide = () => {
  if (timer.value) {
    clearInterval(timer.value);
    timer.value = null;
  }
};

// 갤러리 메인으로 입장
const enterGallery = () => {
  router.push({
    name: 'home',
  });
};

// 생명주기 훅
onMounted(() => {
  preloadImages(); // 💡 마운트되자마자 모든 이미지 로드 시작
  startAutoSlide();
});

onUnmounted(() => {
  stopAutoSlide();
});
</script>

<template>
  <div class="landing-container">
    <div class="background-blur" :style="{ backgroundImage: `url(${currentSlide.image})` }"></div>
    <div class="overlay-gradient"></div>

    <div class="hero-card shadow-lg" @mouseenter="stopAutoSlide" @mouseleave="startAutoSlide">

      <div class="card-image-area">
        <transition name="fade" mode="out-in">
          <img :key="currentSlide.id"
               :src="currentSlide.image"
               :alt="currentSlide.title"
               class="slide-img-tag"
               loading="eager" />
        </transition>
      </div>

      <div class="card-content-area">
        <transition name="slide-up" mode="out-in">
          <div :key="currentSlide.id" class="text-wrapper">
            <h3 class="subtitle">{{ currentSlide.subTitle }}</h3>
            <h1 class="title">{{ currentSlide.title }}</h1>
            <div class="divider"></div>
            <p class="description">{{ currentSlide.description }}</p>

            <button class="btn-enter" @click="enterGallery">
              갤러리 입장하기 <i class="fas fa-arrow-right ms-2"></i>
            </button>
          </div>
        </transition>

        <div class="controls">
          <button class="nav-btn prev" @click="prevSlide">❮</button>
          <div class="indicators">
            <span v-for="(slide, index) in slides"
                  :key="slide.id"
                  class="dot"
                  :class="{ active: index === currentIndex }"
                  @click="currentIndex = index"></span>
          </div>
          <button class="nav-btn next" @click="nextSlide">❯</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Oswald:wght@300;500;700&family=Noto+Serif+KR:wght@400;700&display=swap');

// --- 1. 전체 컨테이너 및 배경 ---
.landing-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #1a1a1a;
}

.background-blur {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-size: cover;
  background-position: center;
  filter: blur(20px) brightness(0.4);
  transform: scale(1.1);
  transition: background-image 1s ease-in-out;
  z-index: 0;
}

.overlay-gradient {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(135deg, rgba(0,0,0,0.6) 0%, rgba(0,0,0,0.2) 100%);
  z-index: 1;
}

// --- 2. 대형 히어로 카드 ---
.hero-card {
  position: relative;
  z-index: 10;
  width: 85%;
  max-width: 1200px;
  height: 600px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 4px; // 갤러리 느낌을 위해 둥근 모서리 최소화
  display: flex;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);

  @media (max-width: 992px) {
    flex-direction: column;
    height: 90vh;
    width: 90%;
  }
}

// 이미지 영역 (왼쪽/상단)
.card-image-area {
  flex: 1.5;
  position: relative;
  overflow: hidden;
  background-color: #000;

  .slide-img {
    width: 100%;
    height: 100%;
    object-fit: cover; /* background-size: cover와 동일 */
    // 💡 이미지 확대 애니메이션 (Ken Burns Effect 느낌)
    animation: zoomEffect 6s infinite alternate;
  }
}

@keyframes zoomEffect {
  0% { transform: scale(1); }
  100% { transform: scale(1.05); }
}

// 텍스트 영역 (오른쪽/하단)
.card-content-area {
  flex: 1;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  background-color: #fff;

  .text-wrapper {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .subtitle {
    font-family: 'Oswald', sans-serif;
    color: #FF6B6B; /* 포인트 컬러 */
    font-size: 1rem;
    letter-spacing: 3px;
    text-transform: uppercase;
    margin-bottom: 0.5rem;
  }

  .title {
    font-family: 'Noto Serif KR', serif;
    font-size: 2.5rem;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 1.5rem;
    line-height: 1.2;
    word-break: keep-all;
  }

  .divider {
    width: 60px;
    height: 4px;
    background-color: #2c3e50;
    margin-bottom: 1.5rem;
  }

  .description {
    font-family: 'Noto Serif KR', serif;
    color: #666;
    line-height: 1.8;
    margin-bottom: 2.5rem;
    font-size: 1rem;
    word-break: keep-all;
  }

  .btn-enter {
    padding: 12px 30px;
    background-color: #2c3e50;
    color: white;
    border: none;
    font-family: 'Oswald', sans-serif;
    font-size: 1.1rem;
    cursor: pointer;
    transition: all 0.3s;
    letter-spacing: 1px;

    &:hover {
      background-color: #FF6B6B;
      transform: translateX(5px); /* 오른쪽으로 살짝 이동 */
    }
  }
}

// --- 3. 컨트롤러 (화살표 & 인디케이터) ---
.controls {
  position: absolute;
  bottom: 2rem;
  right: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;

  .nav-btn {
    background: none;
    border: 1px solid #ddd;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.2s;
    color: #333;

    &:hover {
      background-color: #2c3e50;
      color: white;
      border-color: #2c3e50;
    }
  }

  .indicators {
    display: flex;
    gap: 8px;

    .dot {
      width: 10px;
      height: 10px;
      background-color: #ddd;
      border-radius: 50%;
      cursor: pointer;
      transition: all 0.3s;

      &.active {
        background-color: #FF6B6B;
        transform: scale(1.2);
      }
    }
  }
}

// --- 4. Vue Transition 효과 ---
// 이미지 페이드 효과
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.8s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 텍스트 슬라이드 업 효과
.slide-up-enter-active {
  transition: all 0.6s ease-out 0.2s; // 0.2s 딜레이로 이미지보다 늦게 뜨도록
}
.slide-up-leave-active {
  transition: all 0.3s ease-in;
}
.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>