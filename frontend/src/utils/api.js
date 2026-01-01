import axios from 'axios';

const api = axios.create({
  headers: {
    baseURL: 'http://localhost:8080/api',

    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
