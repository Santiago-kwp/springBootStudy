<template>
  <div class="cart-container">
    <h1>Shopping Cart</h1>
    <div v-if="cartStore.cartItems.length === 0" class="empty-cart">
      Your cart is empty.
    </div>
    <div v-else>
      <div class="cart-items">
        <div v-for="item in cartStore.cartItems" :key="item.id" class="cart-item">
          <div class="item-info">
            <img :src="item.imgPath" :alt="item.name" class="item-image" />
            <div>
              <h3>{{ item.name }}</h3>
              <p>{{ item.price }} won</p>
            </div>
          </div>
          <div class="item-controls">
            <button @click="cartStore.removeItem(item.id)">Remove</button>
          </div>
        </div>
      </div>
      <div class="cart-summary">
        <h3>Total items: {{ cartStore.cartItems.length }}</h3>
      </div>
      <div class="order-form">
        <h2>Shipping Information</h2>
        <form @submit.prevent="handlePlaceOrder">
          <div class="form-group">
            <label for="address">Address:</label>
            <input type="text" id="address" v-model="shippingInfo.address" required />
          </div>
           <div class="form-group">
            <label for="address">Detail Address:</label>
            <input type="text" id="detailAddress" v-model="shippingInfo.detailAddress" required />
          </div>
          <div class="form-group">
            <label for="zipcode">Zipcode:</label>
            <input type="text" id="zipcode" v-model="shippingInfo.zipcode" required />
          </div>
          <button type="submit" :disabled="loading">
            {{ loading ? 'Placing order...' : 'Place Order' }}
          </button>
          <p v-if="error" class="error-message">{{ error }}</p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useCartStore } from '@/stores/cart';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();

const shippingInfo = ref({
  address: '',
  detailAddress: '',
  zipcode: '',
});
const loading = ref(false);
const error = ref(null);

onMounted(() => {
  if (!authStore.isLoggedIn) {
    router.push('/login');
  } else {
    cartStore.fetchCart();
  }
});

const handlePlaceOrder = async () => {
  loading.value = true;
  error.value = null;
  try {
    await cartStore.placeOrder(shippingInfo.value);
    alert('Order placed successfully!');
    router.push('/');
  } catch (err) {
    error.value = 'Failed to place order.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.cart-container {
  max-width: 800px;
  margin: 0 auto;
}
.empty-cart {
  text-align: center;
  font-size: 1.2rem;
  margin-top: 50px;
}
.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #ccc;
  padding-bottom: 20px;
}
.item-info {
  display: flex;
  align-items: center;
}
.item-image {
  width: 100px;
  height: 100px;
  margin-right: 20px;
}
.item-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}
.cart-summary {
  text-align: right;
  margin-top: 20px;
}
.order-form {
  margin-top: 40px;
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
}
input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
button:disabled {
  background-color: #ccc;
}
.error-message {
  color: red;
  margin-top: 10px;
}
</style>
