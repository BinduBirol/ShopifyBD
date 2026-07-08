import axios from 'axios';
import i18n from 'src/i18n';

const api = axios.create({
  baseURL: 'http://localhost:8081/auth',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});


// Attach JWT
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


// Handle responses
api.interceptors.response.use(
  (response) => response,

  (error) => {

    const status = error.response?.status;

    const requestUrl = error.config?.url;


    // Token expired / unauthorized
    if (
      status === 401 &&
      requestUrl !== '/v1/login'
    ) {

      localStorage.removeItem('accessToken');

      window.location.href = '/sign-in';
    }


    return Promise.reject(error);
  }
);


export default api;