import { Box, CircularProgress, Container, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Logo } from '../logo';

export default function LoadingData() {
    const { t } = useTranslation();

    return (

        <Container
            sx={{
                py: 10,
                flexGrow: 1,
                display: 'flex',
                alignItems: 'center',
                flexDirection: 'column',
                justifyContent: 'center',
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
                sx={{ 'svg circle': { stroke: 'url(#my_gradient)' }, mb: 4 }} />

            <Typography variant="body1" color="text.secondary">
                {t('common.loading')}
            </Typography>
        </Container>
    );
}