import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { apiClient, useAuthStore } from './auth';

export const useCartStore = defineStore('cart', () => {
    const items = ref([]);

    const cartItems = computed(() => items.value);

    function addItem(item) {
        const existingItem = items.value.find(i => i.id === item.id);
        if (!existingItem) {
            items.value.push({ ...item });
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

    async function syncCart() {
        const authStore = useAuthStore();
        if (!authStore.isLoggedIn) return;

        const cartData = items.value.map(item => ({
            itemId: item.id,
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
            const response = await apiClient.get('/cart/items');
            const cartData = response.data;
            const fetchedItems = cartData.map(cartItem => ({
                id: cartItem.itemId,
                name: cartItem.itemName,
                price: cartItem.itemPrice,
                imgPath: cartItem.itemImgPath,
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
            items: items.value.map(item => ({
                itemId: item.id,
                qty: 1,
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
        addItem,
        removeItem,
        fetchCart,
        placeOrder,
    };
});
