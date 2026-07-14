import { getFacilities } from 'src/api/propertyAxios';
import type { WorkspacesPopoverProps } from './components/workspaces-popover';

export const getWorkspaces = async (): Promise<WorkspacesPopoverProps['data']> => {
  const facilities = await getFacilities();

  console.log(facilities);

  return facilities.map((facility) => ({
    id: facility.id!,
    name: facility.name,
    plan: facility.userRole,
    logo: '/assets/icons/workspaces/logo-1.webp',
  }));
};