import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import axios from 'axios';

// Axios instance for API calls
const apiClient = axios.create({
    baseURL: '/v1/api',
});

apiClient.interceptors.request.use(config => {
    const authStore = useAuthStore();
    if (authStore.accessToken) {
        config.headers.Authorization = `Bearer ${authStore.accessToken}`;
    }
    return config;
});

export const useAuthStore = defineStore('auth', () => {
    const user = ref(null);
    const accessToken = ref(localStorage.getItem('accessToken') || null);

    const isLoggedIn = computed(() => !!accessToken.value);

    function setAccessToken(token) {
        accessToken.value = token;
        if (token) {
            localStorage.setItem('accessToken', token);
        } else {
            localStorage.removeItem('accessToken');
        }
    }

    function setUser(userData) {
        user.value = userData;
    }

    async function login(credentials) {
        const response = await apiClient.post('/account/login', credentials);
        const data = response.data;
        setAccessToken(data.accessToken);
        setUser({ name: data.name, loginId: data.loginId });
        return data;
    }

    function logout() {
        apiClient.post('/account/logout');
        setAccessToken(null);
        setUser(null);
    }

    async function checkAuth() {
        if (!accessToken.value) return;
        try {
            const response = await apiClient.get('/account/check');
            if (!response.data) {
                await refreshToken();
            }
        } catch (error) {
            if (error.response && error.response.status === 401) {
                await refreshToken();
            }
        }
    }

    async function refreshToken() {
        try {
            const response = await apiClient.get('/account/token');
            const newAccessToken = response.data;
            if (newAccessToken) {
                setAccessToken(newAccessToken);
            } else {
                logout();
            }
        } catch (error) {
            logout();
        }
    }

    // Add interceptor to handle token refresh
    apiClient.interceptors.response.use(
        response => response,
        async error => {
            const originalRequest = error.config;
            if (error.response.status === 401 && !originalRequest._retry) {
                originalRequest._retry = true;
                await refreshToken();
                return apiClient(originalRequest);
            }
            return Promise.reject(error);
        }
    );


    return {
        user,
        accessToken,
        isLoggedIn,
        login,
        logout,
        checkAuth,
        setUser,
    };
});

// Initialize and export the apiClient
export { apiClient };
