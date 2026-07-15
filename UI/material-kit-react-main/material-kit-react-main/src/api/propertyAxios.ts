import axios from 'axios';
import api, { ApiResponse } from './axios';
import i18n from 'src/i18n';
import { Facility, FacilityType } from 'src/types/property/facility';
import { attachCommonInterceptors } from './axios-interceptors';

export const propertyAxios = axios.create({
  baseURL: 'http://localhost:8082/property',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

propertyAxios.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  config.headers['Accept-Language'] = i18n.language;

  return config;
});

attachCommonInterceptors(propertyAxios);

export const getFacilities = async (): Promise<Facility[]> => {
  const response = await propertyAxios.get<ApiResponse<Facility[]>>('/facility/get');

  return response.data.data;
};

export async function createFacility(data: Facility): Promise<ApiResponse<string>> {
  const response = await propertyAxios.post<ApiResponse<string>>('/facility/create', data);

  return response.data;
}
