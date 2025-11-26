import axios from "axios"; // ①

// 상품 목록 조회
export const getItems = () => { // ②
  return axios.get("/v1/api/items").catch(e => e.response);
};

// 상품 하나 조회
export const getItem = (id) => {
  return axios.get(`/v1/api/item/${id}`).catch(e=> e.response);
}

