import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { apiClient, useAuthStore } from './auth';

export const useCartStore = defineStore('cart', () => {
    const items = ref([]);

    const cartItems = computed(() => items.value);
    const cartItemCount = computed(() => items.value.reduce((acc, item) => acc + item.quantity, 0));
    const cartTotalPrice = computed(() => items.value.reduce((acc, item) => acc + item.price * item.quantity, 0));

    function addItem(item) {
        const existingItem = items.value.find(i => i.id === item.id);
        if (existingItem) {
            existingItem.quantity++;
        } else {
            items.value.push({ ...item, quantity: 1 });
        }
        syncCart();
    }

    function removeItem(itemId) {
        const index = items.value.findIndex(i => i.id === itemId);
        if (index !== -1) {
            items.value.splice(index, 1);
            syncCart();
        }
    }

    function updateQuantity(itemId, quantity) {
        const item = items.value.find(i => i.id === itemId);
        if (item) {
            item.quantity = quantity;
            if (item.quantity <= 0) {
                removeItem(itemId);
            } else {
                syncCart();
            }
        }
    }

    async function syncCart() {
        const authStore = useAuthStore();
        if (!authStore.isLoggedIn) return;

        const cartData = items.value.map(item => ({
            itemId: item.id,
            itemCount: item.quantity,
        }));
        
        try {
            await apiClient.post('/cart/items', cartData);
        } catch (error) {
            console.error('Failed to sync cart with backend', error);
        }
    }

    async function fetchCart() {
        const authStore = useAuthStore();
        if (!authStore.isLoggedIn) return;

        try {
            const response = await apiClient.get('/carts');
            const cartData = response.data;
            // This assumes the backend returns a list of items with their quantities
            // We need to map this data to our cart's item structure
            const fetchedItems = cartData.map(cartItem => ({
                id: cartItem.itemId,
                name: cartItem.itemName,
                price: cartItem.itemPrice,
                imgPath: cartItem.itemImgPath,
                quantity: cartItem.itemCount,
            }));
            items.value = fetchedItems;
        } catch (error) {
            console.error('Failed to fetch cart from backend', error);
        }
    }

    async function placeOrder(orderInfo) {
        const authStore = useAuthStore();
        if (!authStore.isLoggedIn) {
            throw new Error('You must be logged in to place an order.');
        }

        const orderData = {
            ...orderInfo,
            orderItems: items.value.map(item => ({
                itemId: item.id,
                itemCount: item.quantity,
            })),
        };

        try {
            await apiClient.post('/orders', orderData);
            items.value = []; // Clear cart after successful order
            syncCart(); // Sync the empty cart with the backend
        } catch (error) {
            console.error('Failed to place order', error);
            throw error;
        }
    }


    return {
        items,
        cartItems,
        cartItemCount,
        cartTotalPrice,
        addItem,
        removeItem,
        updateQuantity,
        fetchCart,
        placeOrder,
    };
});
