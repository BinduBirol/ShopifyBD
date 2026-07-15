import { CONFIG } from 'src/config-global';

import { useTranslation } from 'react-i18next';
import { FacilityCreateView } from './facility-create-view';

// ----------------------------------------------------------------------

export default function Page() {
  const { t } = useTranslation();
  return (
    <>
      <title>{`${t('facility.create')} - ${CONFIG.appName}`}</title>


      <FacilityCreateView />
    </>
  );
}
