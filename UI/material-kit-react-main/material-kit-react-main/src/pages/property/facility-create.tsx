import { CONFIG } from 'src/config-global';

import { useTranslation } from 'react-i18next';
import { FacilityCreateView } from './facility-create-view';

// ----------------------------------------------------------------------

export default function Page() {
      const { t } = useTranslation();
  return (
    <>
      <title>{`Facility - ${CONFIG.appName}`}</title>
      <meta
        name="facility-create"
        content="Create a new facility here"
      />
      <meta name="keywords" content="react,material,kit,application,dashboard,admin,template, facility" />

      <FacilityCreateView />
    </>
  );
}
