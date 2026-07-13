import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';
import { z } from 'zod';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

import {
    Box,
    Button,
    Link,
    Stack,
    TextField,
    Typography,
} from '@mui/material';

import { useSnackbar } from 'notistack';

import { RouterLink } from 'src/routes/components';
import AlertDialog from 'src/components/dialog/AlertDialog';
import { resetPassword } from 'src/api/axios';

export default function ResetPasswordPage() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();

    const [searchParams] = useSearchParams();
    const token = searchParams.get('token') ?? '';

    const [alertOpen, setAlertOpen] = useState(false);
    const [alertTitle, setAlertTitle] = useState('');
    const [alertMessage, setAlertMessage] = useState('');

    const schema = z
        .object({
            password: z
                .string()
                .min(8, 'validation.passwordMinLength')
                .max(100, 'validation.passwordMaxLength'),

            confirmPassword: z.string(),
        })
        .refine(
            (data) => data.password === data.confirmPassword,
            {
                path: ['confirmPassword'],
                message: 'validation.passwordMismatch',
            }
        );

    type FormValues = z.infer<typeof schema>;

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<FormValues>({
        resolver: zodResolver(schema),
        defaultValues: {
            password: '',
            confirmPassword: '',
        },
    });

    const onSubmit = async (data: FormValues) => {
        if (!token) {
            enqueueSnackbar(t('auth.resetPassword.invalidToken'), {
                variant: 'error',
            });
            return;
        }

        try {
            const response = await resetPassword({
                token,
                password: data.password,
            });

            if (response.success) {
                setAlertTitle(t('common.success'));
                setAlertMessage(response.data);
                setAlertOpen(true);
            } else {
                enqueueSnackbar(
                    response.error?.message ??
                    t('common.somethingWentWrong'),
                    {
                        variant: 'error',
                    }
                );
            }
        } catch (error: any) {
            enqueueSnackbar(
                error?.response?.data?.error?.message ??
                t('common.somethingWentWrong'),
                {
                    variant: 'error',
                }
            );
        }
    };

    if (!token) {
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
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
                <Stack spacing={3}>
                    <Box>
                        <Typography variant="h4">
                            {t('auth.resetPassword.title')}
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                            sx={{ mt: 1 }}
                        >
                            {t('auth.resetPassword.description')}
                        </Typography>
                    </Box>

                    <TextField
                        fullWidth
                        type="password"
                        label={t('auth.resetPassword.newPassword')}
                        error={!!errors.password}
                        helperText={
                            errors.password
                                ? t(errors.password.message!)
                                : ''
                        }
                        {...register('password')}
                    />

                    <TextField
                        fullWidth
                        type="password"
                        label={t('auth.resetPassword.confirmPassword')}
                        error={!!errors.confirmPassword}
                        helperText={
                            errors.confirmPassword
                                ? t(errors.confirmPassword.message!)
                                : ''
                        }
                        {...register('confirmPassword')}
                    />

                    <Button
                        type="submit"
                        fullWidth
                        size="large"
                        variant="contained"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? t('common.loading')
                            : t('auth.resetPassword.resetPassword')}
                    </Button>

                    <Link
                        component={RouterLink}
                        href="/sign-in"
                        underline="hover"
                        variant="body2"
                        sx={{ alignSelf: 'center' }}
                    >
                        {t('auth.resetPassword.backToSignIn')}
                    </Link>
                </Stack>
            </form>

            <AlertDialog
                open={alertOpen}
                title={alertTitle}
                link="/sign-in"
                buttonText={t('otp.backToLogin')}
                message={alertMessage}
                onClose={() => setAlertOpen(false)}
            />
        </>
    );
}