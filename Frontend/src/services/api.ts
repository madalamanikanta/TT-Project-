import axios, { AxiosInstance, AxiosResponse } from 'axios';

// Create axios instance with base configuration
// baseURL can be overridden by VITE_API_URL environment variable (e.g. VITE_API_URL=http://localhost:8080/api)
const baseURL =
  (import.meta.env && import.meta.env.VITE_API_URL) || 'http://localhost:8080/api';

const api: AxiosInstance = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
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

// Response interceptor to handle common errors
api.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid, clear localStorage and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;