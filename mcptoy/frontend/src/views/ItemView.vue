<template>
  <div>
    <h1>Items</h1>
    <div v-if="loading">Loading...</div>
    <div v-if="error">{{ error }}</div>
    <div v-if="items" class="items-grid">
      <div v-for="item in items" :key="item.id" class="item-card">
        <img :src="item.imgPath" :alt="item.name" />
        <h3>{{ item.name }}</h3>
        <p>{{ item.price }} won</p>
        <button @click="addToCart(item)">Add to cart</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { apiClient } from '@/stores/auth';
import { useCartStore } from '@/stores/cart';

const items = ref(null);
const loading = ref(true);
const error = ref(null);
const cartStore = useCartStore();

onMounted(async () => {
  try {
    const response = await apiClient.get('/items');
    items.value = response.data;
  } catch (err) {
    error.value = 'Failed to fetch items.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});

const addToCart = (item) => {
  cartStore.addItem(item);
};
</script>

<style scoped>
.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}
.item-card {
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 20px;
  text-align: center;
}
.item-card img {
  max-width: 100%;
  height: auto;
  margin-bottom: 15px;
}
</style>
