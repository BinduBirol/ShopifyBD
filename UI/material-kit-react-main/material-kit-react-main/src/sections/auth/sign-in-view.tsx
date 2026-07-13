import { useState, useCallback, useEffect, useRef, useMemo } from 'react';

import Box from '@mui/material/Box';
import Link from '@mui/material/Link';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import TextField from '@mui/material/TextField';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import InputAdornment from '@mui/material/InputAdornment';
import MenuItem from '@mui/material/MenuItem';

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from '@mui/material';


import { z } from 'zod';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';


import axios from 'axios';
import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';
import { useSearchParams } from 'react-router-dom';


import { useRouter } from 'src/routes/hooks';
import { RouterLink } from 'src/routes/components/router-link';

import { Iconify } from 'src/components/iconify';

import { login } from 'src/api/authApi';

import { useAuth } from 'src/auth/AuthContext';
import { authStorage } from 'src/auth/authStorage';
import SocialLogin from 'src/components/auth/SocialLogin';
import AlertDialog from 'src/components/dialog/AlertDialog';


// ----------------------------------------------------------------------

export function SignInView() {


  const { t } = useTranslation();

  const { enqueueSnackbar } = useSnackbar();

  const router = useRouter();

  const auth = useAuth();


  const [searchParams] = useSearchParams();


  const reason = searchParams.get('reason');


  const sessionExpiredShown = useRef(false);

  const [successDialogOpen, setSuccessDialogOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [verifyUserId, setVerifyUserId] = useState<string>('');



  useEffect(() => {


    if (
      reason === 'session_expired'
      &&
      !sessionExpiredShown.current
    ) {

      sessionExpiredShown.current = true;

      enqueueSnackbar(
        t('auth.sessionExpired'),
        {
          variant: 'error'
        }
      );

    }



    if (
      reason === 'logged_out'
      &&
      !sessionExpiredShown.current
    ) {

      sessionExpiredShown.current = true;

      enqueueSnackbar(
        t('auth.loggedOut'),
        {
          variant: 'info'
        }
      );

    }


  }, [
    reason,
    enqueueSnackbar,
    t
  ]);





  const [loading, setLoading] = useState(false);


  const [
    loginType,
    setLoginType
  ] = useState<'EMAIL' | 'MOBILE'>('EMAIL');



  const [
    showPassword,
    setShowPassword
  ] = useState(false);



  const [
    verificationDialogOpen,
    setVerificationDialogOpen
  ] = useState(false);



  const [
    verificationType,
    setVerificationType
  ] = useState<'EMAIL' | 'MOBILE' | null>(null);





  const schema = useMemo(
    () =>
      z.object({

        identifier:
          z.string()
            .min(
              1,
              t('auth.identifierRequired')
            ),


        password:
          z.string()
            .min(
              1,
              t('register.passwordRequired')
            )

      }),
    [
      t
    ]
  );



  type LoginForm = z.infer<typeof schema>;



  const {
    register: registerField,
    handleSubmit,
    watch,
    formState: {
      errors
    }

  } = useForm<LoginForm>({

    resolver: zodResolver(schema),

    defaultValues: {
      identifier: '',
      password: ''
    }

  });



  const identifier = watch('identifier');




  const handleSignIn = useCallback(
    async (data: LoginForm) => {


      try {


        setLoading(true);



        const response = await login({

          identifier: data.identifier,

          password: data.password,

          role: 'CUSTOMER',

          loginType

        });



        if (!response.success) {


          switch (response.error?.code) {


            case 'mail.not.verified':

              setVerificationType('EMAIL');

              setVerifyUserId(response.correlationId ?? '');

              setVerificationDialogOpen(true);

              return;


            case 'phone.not.verified':

              setVerificationType('MOBILE');

              setVerifyUserId(response.correlationId ?? '');

              setVerificationDialogOpen(true);

              return;



            default:

              enqueueSnackbar(
                response.error?.message
                ??
                t('auth.loginFailed'),
                {
                  variant: 'error'
                }
              );

              return;

          }

        }



        const {
          accessToken,
          refreshToken,
          role

        } = response.data;



        authStorage.saveTokens(
          accessToken,
          refreshToken,
          role
        );



        await auth.login();


        await auth.refreshUser();



        router.push('/');



      }
      catch (error) {


        if (axios.isAxiosError(error)) {


          const apiError =
            error.response?.data;



          enqueueSnackbar(
            apiError?.error?.message
            ??
            t('common.serverUnavailable'),
            {
              variant: 'error'
            }
          );


        }
        else {


          enqueueSnackbar(
            t('common.somethingWentWrong'),
            {
              variant: 'error'
            }
          );

        }


      }
      finally {


        setLoading(false);


      }


    },
    [
      loginType,
      router,
      enqueueSnackbar,
      t,
      auth
    ]
  );

  const renderForm = (

    <Box
      component="form"
      onSubmit={handleSubmit(handleSignIn)}
      sx={{
        display: 'flex',
        alignItems: 'flex-end',
        flexDirection: 'column',
      }}
    >

      <TextField
        select
        fullWidth
        label={t('auth.loginType')}
        value={loginType}
        onChange={(e) =>
          setLoginType(
            e.target.value as 'EMAIL' | 'MOBILE'
          )
        }
        sx={{ mb: 3 }}
      >

        <MenuItem value="EMAIL">
          {t('auth.email')}
        </MenuItem>


        <MenuItem value="MOBILE">
          {t('auth.mobileNumber')}
        </MenuItem>

      </TextField>



      <TextField
        fullWidth
        label={
          loginType === 'EMAIL'
            ? t('auth.emailAddress')
            : t('auth.mobileNumber')
        }
        placeholder={
          loginType === 'EMAIL'
            ? t('auth.emailPlaceholder')
            : t('auth.mobilePlaceholder')
        }
        {...registerField('identifier')}
        error={!!errors.identifier}
        helperText={errors.identifier?.message}
        sx={{ mb: 3 }}
        slotProps={{
          inputLabel: {
            shrink: true
          }
        }}
      />




      <TextField
        fullWidth
        label={t('auth.password')}
        type={
          showPassword
            ? 'text'
            : 'password'
        }
        {...registerField('password')}
        error={!!errors.password}
        helperText={errors.password?.message}
        sx={{ mb: 3 }}
        slotProps={{
          inputLabel: {
            shrink: true
          },

          input: {
            endAdornment: (

              <InputAdornment position="end">

                <IconButton
                  onClick={() =>
                    setShowPassword(!showPassword)
                  }
                  edge="end"
                >

                  <Iconify
                    icon={
                      showPassword
                        ? 'solar:eye-bold'
                        : 'solar:eye-closed-bold'
                    }
                  />

                </IconButton>


              </InputAdornment>

            )
          }
        }}
      />




      <Link
        variant="body2"
        color="inherit"
        sx={{ mb: 1.5 }}
        component={RouterLink}
        href="/forgot-password"
      >

        {t('auth.forgotPassword.title')}

      </Link>




      <Button
        fullWidth
        size="large"
        variant="contained"
        color="primary"
        disabled={loading}
        type="submit"
      >

        {
          loading
            ? t('auth.signingIn')
            : t('auth.signIn')
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

          {t('auth.login')}

        </Typography>



        <Typography
          variant="body2"
          sx={{
            color: 'text.secondary'
          }}
        >

          {t('auth.noAccount')}


          <Link
            component={RouterLink}
            href="/register"
            variant="subtitle2"
            sx={{
              ml: 0.5
            }}
          >

            {t('auth.getStarted')}

          </Link>


        </Typography>


      </Box>



      {renderForm}




      <SocialLogin />





      <AlertDialog
        open={verificationDialogOpen}
        title={
          verificationType === 'EMAIL'
            ? t('auth.emailVerificationRequired')
            : t('auth.phoneVerificationRequired')
        }
        message={
          verificationType === 'EMAIL'
            ? t('auth.emailVerificationDescription')
            : t('auth.phoneVerificationDescription')
        }
        buttonText={
          verificationType === 'EMAIL'
            ? t('auth.verifyEmail')
            : t('auth.verifyPhone')
        }
        link={
          verifyUserId
            ? `/verify-account?userId=${verifyUserId}`
            : undefined
        }
        onClose={() => setVerificationDialogOpen(false)}
      />


    </>
  );

}