import { Box, CircularProgress, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import LoadingData from './loading-data';

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
            <LoadingData />
        </Box>
    );
}