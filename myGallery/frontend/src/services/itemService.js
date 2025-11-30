import  httpRequester from '@/libs/httpRequester.js';

// 상품 목록 조회
// Spring Boot의 기본 페이지네이션 파라미터는 'page'와 'size'입니다.
export const getItems = async (args) => {
  try {
    return httpRequester.get(`/v1/api/items`,args);

  } catch (error) {
    console.error("상품 목록 조회 오류:", error);
    throw error;
  }
};

// 상품 하나 조회
export const getItem = (id) => {
  return httpRequester.get(`/v1/api/items/${id}`).catch(e=> e.response);
}


// 상품 하나 이상을 아이디 리스트로 조회
export const getItemList = (itemIds) => {
    const idsString = Array.isArray(itemIds) ? itemIds.join(',') : String(itemIds);
    return httpRequester.get(`/v1/api/itemList`, { itemIds: idsString })
        .catch(e => e.response);
}

