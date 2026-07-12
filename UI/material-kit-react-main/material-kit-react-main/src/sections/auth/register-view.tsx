import { useCallback, useMemo, useState } from 'react';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import axios from 'axios';
import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';

import { RouterLink } from 'src/routes/components/router-link';
import { register } from 'src/api/authApi';
import AlertDialog from 'src/components/dialog/AlertDialog';
import SocialLogin from 'src/components/auth/SocialLogin';


// ----------------------------------------------------------------------

export function RegisterView() {

    const { t } = useTranslation();

    const { enqueueSnackbar } = useSnackbar();

    const [loading, setLoading] = useState(false);

    const [successDialogOpen, setSuccessDialogOpen] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');


    const schema = useMemo(() => z.object({

        firstName: z
            .string()
            .min(1, t('register.firstNameRequired')),

        lastName: z
            .string()
            .min(1, t('register.lastNameRequired')),

        email: z
            .string()
            .min(1, t('register.emailRequired'))
            .email(t('register.invalidEmail')),

        phone: z
            .string()
            .min(1, t('register.phoneRequired')),

        password: z
            .string()
            .min(1, t('register.passwordRequired'))
            .min(8, t('register.passwordMinLength')),

    }), [t]);


    type RegisterForm = z.infer<typeof schema>;


    const {
        register: registerField,
        handleSubmit,
        formState: { errors },
    } = useForm<RegisterForm>({
        resolver: zodResolver(schema),
        defaultValues: {
            firstName: '',
            lastName: '',
            email: '',
            phone: '',
            password: '',
        },
    });



    const handleRegister = useCallback(
        async (data: RegisterForm) => {

            try {

                setLoading(true);


                const response = await register({
                    ...data,
                    role: "CUSTOMER",
                });


                if (response.success) {

                    setSuccessMessage(
                        response.data ?? t('register.registrationSuccessful')
                    );

                    setSuccessDialogOpen(true);

                    return;
                }


                enqueueSnackbar(
                    response.error?.message ??
                    t('register.registrationFailed'),
                    {
                        variant: 'error',
                    }
                );


            } catch (error) {


                if (axios.isAxiosError(error)) {

                    const apiError = error.response?.data;


                    enqueueSnackbar(
                        apiError?.error?.message ??
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


            } finally {

                setLoading(false);

            }


        },
        [
            enqueueSnackbar,
            t,
        ]
    );



    const renderForm = (

        <Box
            component="form"
            onSubmit={handleSubmit(handleRegister)}
            sx={{
                display: 'flex',
                alignItems: 'flex-end',
                flexDirection: 'column',
            }}
        >


            <TextField
                fullWidth
                label={t('register.firstName')}
                {...registerField('firstName')}
                error={!!errors.firstName}
                helperText={errors.firstName?.message}
                sx={{ mb: 3 }}
            />


            <TextField
                fullWidth
                label={t('register.lastName')}
                {...registerField('lastName')}
                error={!!errors.lastName}
                helperText={errors.lastName?.message}
                sx={{ mb: 3 }}
            />


            <TextField
                fullWidth
                label={t('auth.emailAddress')}
                {...registerField('email')}
                error={!!errors.email}
                helperText={errors.email?.message}
                sx={{ mb: 3 }}
            />


            <TextField
                fullWidth
                label={t('auth.mobileNumber')}
                {...registerField('phone')}
                error={!!errors.phone}
                helperText={errors.phone?.message}
                sx={{ mb: 3 }}
            />


            <TextField
                fullWidth
                type="password"
                label={t('auth.password')}
                {...registerField('password')}
                error={!!errors.password}
                helperText={errors.password?.message}
                sx={{ mb: 3 }}
            />



            <Button
                fullWidth
                size="large"
                variant="contained"
                color="inherit"
                disabled={loading}
                type="submit"
            >

                {
                    loading
                        ? t('register.loading')
                        : t('register.submit')
                }

            </Button>


        </Box>

    );



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
                    {t('register.title')}
                </Typography>


                <Typography
                    variant="body2"
                    sx={{ color: 'text.secondary' }}
                >

                    {t('register.alreadyHaveAccount')}

                    <Link
                        component={RouterLink}
                        href="/sign-in"
                        variant="subtitle2"
                        sx={{ ml: 0.5 }}
                    >
                        {t('auth.signIn')}
                    </Link>

                </Typography>


            </Box>


            {renderForm}
            
            <SocialLogin />

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