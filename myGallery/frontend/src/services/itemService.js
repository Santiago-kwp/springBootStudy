import  httpRequester from '@/libs/httpRequester.js';

// 상품 목록 조회
export const getItems = () => { // ②
  return httpRequester.get("/v1/api/items").catch(e => e.response);
};

// 상품 하나 조회
export const getItem = (id) => {
  return httpRequester.get(`/v1/api/items/${id}`).catch(e=> e.response);
}

