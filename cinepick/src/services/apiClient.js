import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
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
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
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
