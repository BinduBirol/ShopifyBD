import axios from 'axios';
import i18n from 'src/i18n';

const api = axios.create({
  baseURL: 'http://localhost:8081/auth',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Separate instance for refresh
const refreshApi = axios.create({
  baseURL: 'http://localhost:8081/auth',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const publicApi = axios.create({
  baseURL: 'http://localhost:8081/auth',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

publicApi.interceptors.request.use(
  (config) => {
    config.headers['Accept-Language'] = i18n.language;
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    config.headers['Accept-Language'] = i18n.language;

    return config;
  },
  (error) => Promise.reject(error)
);

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

        console.log('Refreshing access token...');

        const response = await refreshApi.post('/v1/refresh', {
          refreshToken,
          role: localStorage.getItem('role'),
        });

        const { accessToken, refreshToken: newRefreshToken } = response.data;

        console.log('New access token:', accessToken);

        localStorage.setItem('accessToken', accessToken);

        if (newRefreshToken) {
          localStorage.setItem('refreshToken', newRefreshToken);
        }

        originalRequest.headers = {
          ...originalRequest.headers,
          Authorization: `Bearer ${accessToken}`,
        };

        console.log('Retrying:', originalRequest.url);

        return api(originalRequest);
      } catch (refreshError) {
        console.error('Refresh failed:', refreshError);

        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        //window.location.href = '/sign-in?reason=session_expired';

        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export type ForgotPasswordRequest = {
  identifier: string;
  loginType: 'EMAIL' | 'MOBILE';
};

export type ApiResponse<T> = {
  success: boolean;
  data: T;
  error?: {
    code: string;
    message: string;
  };
  timestamp: string;
  path: string;
  version: string;
  correlationId?: string;
};

export async function forgotPassword(payload: ForgotPasswordRequest) {
  const response = await api.post<ApiResponse<string>>('/v1/forgot-password', payload);

  return response.data;
}

export async function resendVerificationOtp(userId: string) {
  const response = await api.post('/v1/user/verify/resend-otp', {
    userId,
  });

  return response.data;
}

export async function verifyAccountOtp(userId: string, otp: string) {
  const response = await api.post('/v1/user/verify/account/otp', {
    userId,
    otp,
  });

  return response.data;
}

export interface ResetPasswordRequest {
  token: string;
  password: string;
}

export const resetPassword = async (request: ResetPasswordRequest) => {
  const response = await publicApi.post('/v1/reset-password', request);

  return response.data;
};
export default api;
