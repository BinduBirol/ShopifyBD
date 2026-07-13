import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

import {
    Box,
    Button,
    Link,
    MenuItem,
    Stack,
    TextField,
    Typography,
} from '@mui/material';

import { useSnackbar } from 'notistack';

import { RouterLink } from 'src/routes/components';
import { forgotPassword } from 'src/api/axios';
import AlertDialog from 'src/components/dialog/AlertDialog';

type LoginType = 'EMAIL' | 'MOBILE';

export default function ForgotPasswordPage() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();

    const [alertOpen, setAlertOpen] = useState(false);
    const [alertTitle, setAlertTitle] = useState('');
    const [alertMessage, setAlertMessage] = useState('');

    const schema = z
        .object({
            loginType: z.enum(['EMAIL', 'MOBILE']),
            identifier: z.string().trim(),
        })
        .superRefine((data, ctx) => {
            if (!data.identifier) {
                ctx.addIssue({
                    code: z.ZodIssueCode.custom,
                    path: ['identifier'],
                    message: 'validation.required',
                });
                return;
            }

            if (
                data.loginType === 'EMAIL' &&
                !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.identifier)
            ) {
                ctx.addIssue({
                    code: z.ZodIssueCode.custom,
                    path: ['identifier'],
                    message: 'register.invalidEmail',
                });
            }

            if (
                data.loginType === 'MOBILE' &&
                !/^01[3-9]\d{8}$/.test(data.identifier)
            ) {
                ctx.addIssue({
                    code: z.ZodIssueCode.custom,
                    path: ['identifier'],
                    message: 'register.phoneRequired',
                });
            }
        });

    type FormValues = z.infer<typeof schema>;

    const {
        register,
        watch,
        setValue,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<FormValues>({
        resolver: zodResolver(schema),
        defaultValues: {
            loginType: 'EMAIL',
            identifier: '',
        },
    });

    const loginType = watch('loginType');
    const isEmail = loginType === 'EMAIL';

    const onSubmit = async (data: FormValues) => {
        try {
            const response = await forgotPassword(data);

            if (response.success) {
                setAlertTitle(t('common.success'));
                setAlertMessage(response.data);
                setAlertOpen(true);
            } else {
                if (
                    response.error?.code ===
                    'auth.password.reset.already.requested'
                ) {
                    setAlertTitle(t('common.error'));
                    setAlertMessage(response.error.message);
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

    return (
        <>
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
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
                        onChange={(e) =>
                            setValue(
                                'loginType',
                                e.target.value as LoginType,
                                {
                                    shouldValidate: true,
                                }
                            )
                        }
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
                        error={!!errors.identifier}
                        helperText={
                            errors.identifier
                                ? t(errors.identifier.message!)
                                : ''
                        }
                        {...register('identifier')}
                    />

                    <Button
                        type="submit"
                        fullWidth
                        size="large"
                        variant="contained"
                        disabled={isSubmitting}
                    >
                        {t('auth.forgotPassword.sendResetLink')}
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
                </Stack>
            </form>

            <AlertDialog
                open={alertOpen}
                title={alertTitle}
                message={alertMessage}
                onClose={() => setAlertOpen(false)}
            />
        </>
    );
}