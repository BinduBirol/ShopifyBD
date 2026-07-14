import { RoleName } from '../auth/userRole';

export enum FacilityType {
  RESIDENTIAL = 'RESIDENTIAL',
  COMMERCIAL = 'COMMERCIAL',
  MIXED_USE = 'MIXED_USE',
  INDUSTRIAL = 'INDUSTRIAL',
  HOSPITALITY = 'HOSPITALITY',
  EDUCATIONAL = 'EDUCATIONAL',
  GOVERNMENT = 'GOVERNMENT',
  OTHER = 'OTHER',
}
export interface Facility {
  id?: string;

  name: string;

  type: FacilityType;

  addressLine1: string;

  addressLine2?: string;

  city?: string;

  country?: string;

  postalCode?: string;

  description?: string;

  userRole: RoleName;
}

export interface CreateFacilityRequest {
  name: string;
  type: FacilityType;
  addressLine1: string;
  addressLine2?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  description?: string;
  userRole: RoleName;
}
