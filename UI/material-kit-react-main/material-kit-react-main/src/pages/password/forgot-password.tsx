import { useState } from 'react';
import { useTranslation } from 'react-i18next';



import {
    Box,
    Button,
    Link,
    MenuItem,
    Stack,
    TextField,
    Typography,
} from '@mui/material';

import { RouterLink } from 'src/routes/components';
import { useSnackbar } from 'notistack';
import { forgotPassword } from 'src/api/axios';
import AlertDialog from 'src/components/dialog/AlertDialog';

type LoginType = 'EMAIL' | 'MOBILE';

export default function ForgotPasswordPage() {
    const { enqueueSnackbar } = useSnackbar();

    const [loading, setLoading] = useState(false);
    const { t } = useTranslation();

    const [loginType, setLoginType] = useState<LoginType>('EMAIL');
    const [identifier, setIdentifier] = useState('');

    const isEmail = loginType === 'EMAIL';

    const [alertOpen, setAlertOpen] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertMessage, setAlertMessage] = useState("");

    const handleSubmit = async () => {
        if (!identifier) {
            return;
        }

        try {
            setLoading(true);

            const response = await forgotPassword({
                identifier,
                loginType,
            });

            console.log('Forgot password response:', response);


            if (response.success) {
                setAlertTitle(t("common.success"));
                setAlertMessage(response.data);
                setAlertOpen(true);
            } else {              

                if (
                    response.error?.code === "auth.password.reset.already.requested"
                ) {
                    setAlertTitle(t("common.error"));
                    setAlertMessage(response.error.message);
                    setAlertOpen(true);
                }else {
                    enqueueSnackbar(
                        response.error?.message ?? t('common.somethingWentWrong'),
                        {
                            variant: 'error',
                        }
                    );
                }
            }

        } catch (error: any) {


            enqueueSnackbar(
                error?.response?.data?.error?.message ??
                t('common.somethingWentWrong'),
                {
                    variant: 'error',
                }
            );

        } finally {
            setLoading(false);
        }
    };

    return (
        <Stack spacing={3}>
            <Box>
                <Typography variant="h4">
                    {t('auth.forgotPassword.title')}
                </Typography>

                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mt: 1 }}
                >
                    {t('auth.forgotPassword.description')}
                </Typography>
            </Box>

            <TextField
                select
                fullWidth
                label={t('auth.forgotPassword.loginType')}
                value={loginType}
                onChange={(e) => setLoginType(e.target.value as LoginType)}
            >
                <MenuItem value="EMAIL">
                    {t('auth.forgotPassword.email')}
                </MenuItem>

                <MenuItem value="MOBILE">
                    {t('auth.forgotPassword.phone')}
                </MenuItem>
            </TextField>

            <TextField
                fullWidth
                required
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                label={
                    isEmail
                        ? t('auth.forgotPassword.email')
                        : t('auth.forgotPassword.phone')
                }
                placeholder={
                    isEmail
                        ? t('auth.forgotPassword.emailPlaceholder')
                        : t('auth.forgotPassword.phonePlaceholder')
                }
            />

            <Button
                fullWidth
                size="large"
                variant="contained"
                onClick={handleSubmit}
                disabled={loading}
            >
                {loading
                    ? t('common.loading')
                    : loginType === 'EMAIL'
                        ? t('auth.forgotPassword.sendResetLink')
                        : t('auth.forgotPassword.sendOtp')}
            </Button>

            <Typography
                variant="body2"
                color="text.secondary"
                align="center"
            >
                {t('auth.forgotPassword.info')}
            </Typography>

            <Link
                component={RouterLink}
                href="/sign-in"
                variant="body2"
                underline="hover"
                sx={{ alignSelf: 'center' }}
            >
                {t('auth.forgotPassword.backToSignIn')}
            </Link>

            <AlertDialog
                open={alertOpen}
                title={alertTitle}
                message={alertMessage}
                onClose={() => setAlertOpen(false)}
            />



        </Stack>




    );
}