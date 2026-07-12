import { useCallback, useEffect, useMemo, useState } from 'react';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import { z } from 'zod';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

import axios from 'axios';
import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';
import { useSearchParams } from 'react-router-dom';

import { RouterLink } from 'src/routes/components/router-link';
import { resendVerificationOtp, verifyAccountOtp } from 'src/api/axios';
import AlertDialog from 'src/components/dialog/AlertDialog';

// import { verifyAccount, resendOtp } from 'src/api/authApi';


// ----------------------------------------------------------------------

export default function VerifyAccountView() {

    const { t } = useTranslation();

    const { enqueueSnackbar } = useSnackbar();

    const [successDialogOpen, setSuccessDialogOpen] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');

    const [searchParams] = useSearchParams();

    const userId = searchParams.get('userId');

    const [countdown, setCountdown] = useState(0);

    useEffect(() => {

        if (countdown === 0) {
            return;
        }

        const timer = setInterval(() => {

            setCountdown((prev) => prev - 1);

        }, 1000);

        return () => clearInterval(timer);

    }, [countdown]);



    const schema = useMemo(() =>

        z.object({

            otp: z
                .string()
                .min(1, t('otp.required'))
                .length(6, t('otp.invalid'))
                .regex(/^\d+$/, t('otp.invalid'))

        }),

        [t]
    );


    type VerifyForm = z.infer<typeof schema>;



    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<VerifyForm>({
        resolver: zodResolver(schema),
        defaultValues: {
            otp: '',
        },
    });



    const handleVerify = useCallback(

        async (data: VerifyForm) => {

            if (!userId) {

                enqueueSnackbar(
                    t('otp.invalidVerificationLink'),
                    {
                        variant: 'error',
                    }
                );

                return;
            }

            try {

                const response = await verifyAccountOtp(userId, data.otp);

                if (!response.success) {
                    enqueueSnackbar(
                        response.error?.message ?? t('otp.verificationFailed'),
                        {
                            variant: 'error',
                        }
                    );

                    return;
                }


                setSuccessMessage(
                    response.data ?? t('otp.verificationSuccessful')
                );

                setSuccessDialogOpen(true);

            } catch (error) {

                if (axios.isAxiosError(error)) {

                    enqueueSnackbar(
                        error.response?.data?.error?.message ??
                        t('common.serverUnavailable'),
                        {
                            variant: 'error',
                        }
                    );

                } else {

                    enqueueSnackbar(
                        t('common.somethingWentWrong'),
                        {
                            variant: 'error',
                        }
                    );

                }

            }

        },

        [enqueueSnackbar, t, userId]

    );



    const handleResend = useCallback(async () => {

        if (!userId || countdown > 0) {
            return;
        }

        try {

            const response = await resendVerificationOtp(userId);

            if (response.success) {

                enqueueSnackbar(
                    response.data ?? t("otp.resendSuccessful"),
                    {
                        variant: "success",
                    }
                );

                setCountdown(60);

                return;
            }

            enqueueSnackbar(
                response.error?.message ?? t("common.somethingWentWrong"),
                {
                    variant: "error",
                }
            );

        } catch (error) {

            if (axios.isAxiosError(error)) {

                enqueueSnackbar(
                    error.response?.data?.error?.message ??
                    t("common.serverUnavailable"),
                    {
                        variant: "error",
                    }
                );

            } else {

                enqueueSnackbar(
                    t("common.somethingWentWrong"),
                    {
                        variant: "error",
                    }
                );

            }

        }

    }, [
        userId,
        countdown,
        enqueueSnackbar,
        t,
    ]);

    if (!userId) {
        return (
            <Box
                sx={{
                    textAlign: 'center',
                    py: 8,
                }}
            >
                <Typography variant="h5" gutterBottom>
                    {t('common.error')}
                </Typography>

                <Typography
                    color="text.secondary"
                    sx={{ mb: 3 }}
                >
                    {t('otp.invalidVerificationLink')}
                </Typography>

                <Button
                    component={RouterLink}
                    href="/sign-in"
                    variant="contained"
                    color="inherit"
                >
                    {t('otp.backToLogin')}
                </Button>
            </Box>
        );
    }

    return (

        <>

            <Box
                sx={{
                    gap: 1.5,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    mb: 5,
                }}
            >

                <Typography variant="h5">
                    {t('otp.title')}
                </Typography>

                <Typography
                    variant="body2"
                    sx={{
                        color: 'text.secondary',
                        textAlign: 'center',
                    }}
                >
                    {t('otp.description')}
                </Typography>

            </Box>



            <Box
                component="form"
                onSubmit={handleSubmit(handleVerify)}
            >

                <TextField
                    fullWidth
                    label={t('otp.code')}
                    placeholder="123456"
                    {...register('otp')}
                    error={!!errors.otp}
                    helperText={errors.otp?.message}
                    slotProps={{
                        htmlInput: {
                            maxLength: 6,
                            inputMode: 'numeric'

                        },

                    }}
                    sx={{ mb: 3 }}
                />



                <Button
                    fullWidth
                    variant="contained"
                    color="inherit"
                    size="large"
                    type="submit"
                    disabled={isSubmitting}
                >
                    {isSubmitting
                        ? t('otp.verifying')
                        : t('otp.verify')}
                </Button>



                <Button
                    fullWidth
                    sx={{
                        mt: 3,
                    }}
                    variant="text"
                    onClick={handleResend}
                    disabled={countdown > 0 || isSubmitting}
                >
                    {countdown > 0
                        ? t("otp.resendIn", { seconds: countdown })
                        : t("otp.resend")}
                </Button>



                <Box
                    sx={{
                        textAlign: 'center',
                        mt: 3,
                    }}
                >

                    <Link
                        component={RouterLink}
                        href="/sign-in"
                    >
                        {t('otp.backToLogin')}
                    </Link>

                </Box>

            </Box>

            <AlertDialog
                open={successDialogOpen}
                title={t('common.success')}
                message={successMessage}
                buttonText={t('auth.signIn')}
                link="/sign-in"
                onClose={() => setSuccessDialogOpen(false)}
            />

        </>

    );

}