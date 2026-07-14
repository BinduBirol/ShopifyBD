import axios from 'axios';
import api, { ApiResponse } from './axios';
import i18n from 'src/i18n';
import { ApiError } from './authApi';
import { RoleName } from 'src/types/auth/userRole';
import { Facility, FacilityType } from 'src/types/property/facility';

export const propertyAxios = axios.create({
  baseURL: 'http://localhost:8082',
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

export const getFacilities = async (): Promise<Facility[]> => {
  const response = await propertyAxios.get<ApiResponse<Facility[]>>('/property/facility/get');

  return response.data.data;
};

export async function createFacility(data: Facility): Promise<ApiResponse<string>> {
  const response = await propertyAxios.post<ApiResponse<string>>('/property/facility/create', data);

  return response.data;
}
