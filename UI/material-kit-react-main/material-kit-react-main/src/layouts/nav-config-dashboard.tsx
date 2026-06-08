import { Label } from 'src/components/label';
import { SvgColor } from 'src/components/svg-color';

const icon = (name: string) => (
  <SvgColor src={`/assets/icons/navbar/${name}.svg`} />
);

export type NavItem = {
  title: string; // translation key
  path: string;
  icon: React.ReactNode;
  info?: React.ReactNode;
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
    title: 'nav.blog',
    path: '/blog',
    icon: icon('ic-blog'),
  },
  {
    title: 'nav.signIn',
    path: '/sign-in',
    icon: icon('ic-lock'),
  },
  {
    title: 'nav.notFound',
    path: '/404',
    icon: icon('ic-disabled'),
  },
];
