import axios from "axios";
import {useAccountStore} from "@/stores/account";

// ① Axios 인스턴스 생성
const instance = axios.create({
  withCredentials: true // Refresh Token 쿠키 자동 포함
});

// 인터셉터(응답 시)
instance.interceptors.response.use(
    (res) => {
  return res;
}, async (err) => {
  if (!err.response) {
    return Promise.reject(err);
  }

  const accountStore = useAccountStore();

  switch (err.response.status) {
    case 400:
      // 400번의 경우 기능별로 알아서 처리하도록 함.
      break;

    case 401:
      // 로그인 요청(/api/account/login)에서 발생한 401은 토큰 만료가 아니라 '비번 틀림'이므로 인터셉터 처리를 패스한다.
        if (err.config.url.includes('/v1/api/account/login')) {
          return Promise.reject(err);
        }
      // ② HTTP 응답코드가 401 이라면 액세스 토큰이 만료된 것일 수 있으므로 쿠키에 있는 리프레시 토큰으로 액세스 토큰을 다시 요청한다.
      //   쿠키는 HTTP 요청시 자동으로 포함되므로, 액세스 토큰을 다시 받았다면 토큰을 교체하여 HTTP 요청을 다시 수행한다.
      //   해당 요청의 HTTP 응답 상태 코드가 이전과 동일한 401 일 수도 있으므로 방지를 위해 요청 설정(config)에 config.retried = true 설정한다.
      const config = err.config;

      if (config.retried) { // 재요청 여부
        window.alert("로그인 세션이 만료되었습니다.");
        // 세션 만료 시 데이터 정리 후 이동
        accountStore.clearAccount();
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
        window.location.replace("/login");
        return Promise.reject(err);
      }

      try {
        // (쿠키에 있는) 리프레시 토큰으로 액세스 토큰 재발급
        const res = await axios.get("/v1/api/account/token", {withCredentials: true});
        const { accessToken } = res.data; // JSON 응답구조 반영
        if (!accessToken) {
          throw new Error("Token refresh failed");
        }
        // 확인 완료! console.log("새로운 토큰 발급! : ", accessToken)
        // 계정 스토어의 액세스 토큰 변경
        accountStore.setAccessToken(accessToken);

        // 새로고침 시에도 유지되도록 로컬 스토리지도 반드시 갱신
        localStorage.setItem('accessToken', accessToken);

        // 요청 헤더 갱신
        config.headers['Authorization'] = `Bearer ${accessToken}`;
        // 확인 완료! console.log("2. 교체된 헤더 값:", config.headers['Authorization']); // "Bearer eyJ..." 형태여야 함

        // 중복 재요청 방지를 위한 프로퍼티 추가
        config.retried = true;
        // 원래 하려던 요청을 새 토큰으로 다시 실행
        return instance(config);

      } catch (refreshError) {
        // 재발급 실패 시 로그아웃 처리
        accountStore.clearAccount();
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
        window.location.replace("/login");
        return Promise.reject(refreshError);
      }

    case 500:
      window.alert("오류가 있습니다. 관리자에게 문의해주세요.");
      break;
  }

  return Promise.reject(err);
});

// HTTP 요청 설정 생성 : 액세스 토큰이 있는 경우 인증 프로퍼티가 포함된 헤더가 있는 객체를 리턴하고, 없는 경우 빈객체를 리턴한다.
const generateConfig = () => { // ③
  // 계정 스토어
  const accountStore = useAccountStore();

  // 스토어에 없으면 로컬 스토리지에서라도 가져오기 (방어 코드)
  const token = accountStore.accessToken || localStorage.getItem('accessToken');

  if (token) {
    return {
      headers: { Authorization: `Bearer ${token}` }
    };
  }

  return {};
};


export default {
  get(url, params) { // ④ Axios 객체의 메서드를 호출하여 HTTP GET 요청을 한다. 호출시 generateConfig메서드를 호출해서 인수로 입력한다.
    const config = generateConfig();
    config.params = params;
    return instance.get(url, config);
  },
  post(url, params) { // ⑤  Axios 객체의 메서드를 호출하여 HTTP post 요청을 한다. 호출시 generateConfig메서드를 호출해서 인수로 입력한다.
    return instance.post(url, params, generateConfig());
  },
  put(url, params) { // ⑥  Axios 객체의 메서드를 호출하여 HTTP put 요청을 한다. 호출시 generateConfig메서드를 호출해서 인수로 입력한다.
    return instance.put(url, params, generateConfig());
  },
  delete(url) { // ⑦  Axios 객체의 메서드를 호출하여 HTTP delete 요청을 한다. 호출시 generateConfig메서드를 호출해서 인수로 입력한다.
    return instance.delete(url, generateConfig());
  },
};