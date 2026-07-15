import Box from '@mui/material/Box';
import Skeleton from '@mui/material/Skeleton';
import type { Breakpoint } from '@mui/material/styles';
import { DashboardContent } from 'src/layouts/dashboard';

type RouteLoadingProps = {
  layoutQuery?: Breakpoint;
  disablePadding?: boolean;
  sx?: any;
};

export function RouteLoading({
  layoutQuery = 'lg',
  disablePadding,
  sx,
}: RouteLoadingProps) {
  return (
    <DashboardContent>

      <Box
        sx={[
          (theme) => ({
            display: 'flex',
            flex: '1 1 auto',
            flexDirection: 'column',



            [theme.breakpoints.up(layoutQuery)]: {
              px: 'var(--layout-dashboard-content-px)',
            },

            ...(disablePadding && {
              p: 0,
            }),
          }),
          ...(Array.isArray(sx) ? sx : [sx]),
        ]}
      >
        {/* Header skeleton */}
        <Skeleton variant="text" width="35%" height={40} sx={{ mb: 2 }} />

        {/* Feed skeleton cards */}
        {Array.from({ length: 2 }).map((_, i) => (
          <Box
            key={i}
            sx={{
              p: 2,
              mb: 2,
              borderRadius: 2,
              bgcolor: 'background.paper',
              boxShadow: 1,
            }}
          >
            {/* title row */}
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <Skeleton variant="circular" width={40} height={40} />
              <Box sx={{ ml: 2, flex: 1 }}>
                <Skeleton width="40%" />
                <Skeleton width="25%" />
              </Box>
            </Box>

            {/* content */}
            <Skeleton width="90%" />
            <Skeleton width="80%" />

            {/* media */}
            <Skeleton variant="rectangular" height={120} sx={{ mt: 1 }} />
          </Box>
        ))}
      </Box>

    </DashboardContent>
  );
}