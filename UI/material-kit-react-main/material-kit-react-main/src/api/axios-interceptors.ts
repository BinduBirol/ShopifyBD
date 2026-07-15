// axios-interceptors.ts

import type { AxiosInstance } from 'axios';
import axios from 'axios';

// Separate instance for refresh
const refreshApi = axios.create({
  baseURL: 'http://localhost:8081/auth',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export function attachCommonInterceptors(api: AxiosInstance) {
  api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  });

  api.interceptors.response.use(
    (response) => response,

    async (error) => {
      const originalRequest = error.config;

      if (!originalRequest) {
        return Promise.reject(error);
      }

      const isAuthError = error.response?.status === 401 || error.response?.status === 403;

      const isLoginRequest = originalRequest.url?.includes('/v1/login');
      const isRefreshRequest = originalRequest.url?.includes('/v1/refresh');

      if (isAuthError && !isLoginRequest && !isRefreshRequest && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const refreshToken = localStorage.getItem('refreshToken');

          if (!refreshToken) {
            throw new Error('No refresh token');
          }

          const response = await refreshApi.post('/v1/refresh', {
            refreshToken,
            role: localStorage.getItem('role'),
          });

          const { accessToken, refreshToken: newRefreshToken } = response.data;

          localStorage.setItem('accessToken', accessToken);

          if (newRefreshToken) {
            localStorage.setItem('refreshToken', newRefreshToken);
          }

          originalRequest.headers.Authorization = `Bearer ${accessToken}`;

          return api(originalRequest);
        } catch (refreshError) {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');

          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    }
  );
}
