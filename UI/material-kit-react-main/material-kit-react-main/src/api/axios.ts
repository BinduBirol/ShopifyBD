import axios from 'axios';
import i18n from 'src/i18n';
import { attachCommonInterceptors } from './axios-interceptors';

const api = axios.create({
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

attachCommonInterceptors(api);

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
