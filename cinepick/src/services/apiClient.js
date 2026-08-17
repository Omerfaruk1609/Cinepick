import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 60000, // Render Free Tier cold-boot (uyku modundan uyanma) için 60 saniye tolerans
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const config = error.config;
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // Sadece korumalı auth gerektiren sayfalarda token temizle
      const token = localStorage.getItem('token');
      if (token) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
      return Promise.reject(error);
    }

    // Sunucu uyku modundaysa (Spin down) veya network gecikmesi varsa 2 kez otomatik yeniden dene
    if (!config || !config.retryCount) {
      config.retryCount = 0;
    }

    if (config.retryCount < 2 && (!error.response || error.code === 'ECONNABORTED' || error.response.status >= 500)) {
      config.retryCount += 1;
      await new Promise((resolve) => setTimeout(resolve, 3000));
      return apiClient(config);
    }

    return Promise.reject(error);
  }
);

export const get = (url, params = {}, config = {}) => {
  return apiClient.get(url, { params, ...config });
};

export const post = (url, data = {}, config = {}) => {
  return apiClient.post(url, data, config);
};

export const put = (url, data = {}, config = {}) => {
  return apiClient.put(url, data, config);
};

export const del = (url, config = {}) => {
  return apiClient.delete(url, config);
};

export default apiClient;
