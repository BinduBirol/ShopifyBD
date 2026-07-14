import { CONFIG } from 'src/config-global';

import { FacilityView } from 'src/sections/property/facility-list-view';

// ----------------------------------------------------------------------

export default function Page() {
  return (
    <>
      <title>{`My Facilities - ${CONFIG.appName}`}</title>

      <FacilityView />
    </>
  );
}
