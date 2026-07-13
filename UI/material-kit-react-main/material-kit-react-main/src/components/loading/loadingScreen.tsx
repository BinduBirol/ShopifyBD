import { Box, CircularProgress, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';

export default function LoadingScreen() {
    const { t } = useTranslation();

    return (
        <Box
            sx={{
                width: '100vw',
                height: '100vh',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 2,
            }}
        >
            <svg width={0} height={0}>
                <defs>
                    <linearGradient id="my_gradient" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" stopColor="#e01cd5" />
                        <stop offset="100%" stopColor="#1CB5E0" />
                    </linearGradient>
                </defs>
            </svg>

            <CircularProgress
                aria-label={t('common.loading')}
                sx={{ 'svg circle': { stroke: 'url(#my_gradient)' } }}
            />

            <Typography variant="body2" color="text.secondary">
                {t('common.loading')}
            </Typography>
        </Box>
    );
}