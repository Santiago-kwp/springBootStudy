<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useCartStore } from '@/stores/cart';

const items = ref([]);
const cartStore = useCartStore();

onMounted(async () => {
  try {
    const response = await axios.get('/v1/api/items');
    items.value = response.data.content; // Assuming the API returns a Page object
  } catch (error) {
    console.error('Error fetching items:', error);
  }
});

const addToCart = (item) => {
  cartStore.addItem(item);
  alert(`${item.name}이(가) 장바구니에 추가되었습니다.`);
};
</script>

<template>
  <main>
    <div class="album py-5 bg-body-tertiary">
      <div class="container">
        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
          <div class="col" v-for="item in items" :key="item.id">
            <div class="card shadow-sm">
              <img :src="item.imgPath" class="card-img-top" alt="...">
              <div class="card-body">
                <p class="card-text">{{ item.name }}</p>
                <div class="d-flex justify-content-between align-items-center">
                  <small class="text-body-secondary">{{ item.price }}원</small>
                  <button class="btn btn-sm btn-outline-secondary" @click="addToCart(item)">Add to Cart</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.card-img-top {
  height: 225px;
  width: 100%;
  object-fit: cover;
}
</style>
