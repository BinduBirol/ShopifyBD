import { Label } from 'src/components/label';
import { SvgColor } from 'src/components/svg-color';

const icon = (name: string) => (
  <SvgColor src={`/assets/icons/navbar/${name}.svg`} />
);

export type NavItem = {
  title: string; // translation key
  path?: string;
  icon?: React.ReactNode;
  info?: React.ReactNode;
  children?: NavItem[];
};

export const navData: NavItem[] = [
  {
    title: 'nav.dashboard',
    path: '/',
    icon: icon('ic-analytics'),
  },
  {
    title: 'nav.user',
    path: '/user',
    icon: icon('ic-user'),
  },
  {
    title: 'nav.product',
    path: '/products',
    icon: icon('ic-cart'),
    info: (
      <Label color="error" variant="inverted">
        +3
      </Label>
    ),
  },


  {
    title: 'nav.properties',
    icon: icon('ic-lock'),
    children: [
      {
        title: 'nav.blog',
        path: '/blog',

      },
      {
        title: 'nav.myFacilities',
        path: '/property/facility/list',
      },
      {
        title: 'facility.create',
        path: '/property/facility/create',
      },
    ],
  },

  {
    title: 'nav.notFound',
    path: '/404',
    icon: icon('ic-disabled'),
  },
];