import { useState, useCallback } from 'react';

import Box from '@mui/material/Box';
import Link from '@mui/material/Link';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import TextField from '@mui/material/TextField';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import InputAdornment from '@mui/material/InputAdornment';

import { useRouter } from 'src/routes/hooks';

import { Iconify } from 'src/components/iconify';
import { login } from 'src/api/authApi';
import { useSnackbar } from "notistack";
import axios from "axios";
import MenuItem from '@mui/material/MenuItem';
import { useTranslation } from 'react-i18next';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from "@mui/material";



// ----------------------------------------------------------------------

export function SignInView() {
  const { t } = useTranslation();

  const { enqueueSnackbar } = useSnackbar();

  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [loginType, setLoginType] = useState<"EMAIL" | "MOBILE">("EMAIL");
  const router = useRouter();
  const [verificationDialogOpen, setVerificationDialogOpen] = useState(false);
  const [verificationType, setVerificationType] = useState<
    "EMAIL" | "MOBILE" | null
  >(null);

  const [showPassword, setShowPassword] = useState(false);

  const handleSignIn = useCallback(async () => {

    try {

      setLoading(true);

      const response = await login({
        identifier,
        password,
        role: "CUSTOMER",
        loginType
      });

      console.log(response);

      if (!response.success) {

        switch (response.error?.code) {

          case "mail.not.verified":
            setVerificationType("EMAIL");
            setVerificationDialogOpen(true);
            return;

          case "phone.not.verified":
            setVerificationType("MOBILE");
            setVerificationDialogOpen(true);
            return;

          default:
            enqueueSnackbar(
              response.error?.message ?? t("auth.loginFailed"),
              {
                variant: "error",
              }
            );
            return;
        }
      }

      localStorage.setItem(
        "accessToken",
        response.data.token
      );

      router.push("/");

    } catch (error) {

      if (axios.isAxiosError(error)) {

        const apiError = error.response?.data;

        enqueueSnackbar(
          apiError?.error?.message ?? t('common.serverUnavailable'),
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

  }, [
    identifier,
    password,
    loginType,
    router,
    enqueueSnackbar,
    t,
  ]);
  const renderForm = (
    <Box
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
          setLoginType(e.target.value as "EMAIL" | "MOBILE")
        }
        sx={{ mb: 3 }}
      >
        <MenuItem value="EMAIL">{t('auth.email')}</MenuItem>
        <MenuItem value="MOBILE">{t('auth.mobileNumber')}</MenuItem>
      </TextField>

      <TextField
        fullWidth
        name="identifier"
        value={identifier}
        onChange={(e) => setIdentifier(e.target.value)}
        label={
          loginType === "EMAIL"
            ? t('auth.emailAddress')
            : t('auth.mobileNumber')
        }
        placeholder={
          loginType === "EMAIL"
            ? t('auth.emailPlaceholder')
            : t('auth.mobilePlaceholder')
        }
        sx={{ mb: 3 }}
        slotProps={{
          inputLabel: { shrink: true },
        }}
      />



      <TextField
        fullWidth
        name="password"
        label={t('auth.password')}
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        type={showPassword ? 'text' : 'password'}
        slotProps={{
          inputLabel: { shrink: true },
          input: {
            endAdornment: (
              <InputAdornment position="end">
                <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                  <Iconify icon={showPassword ? 'solar:eye-bold' : 'solar:eye-closed-bold'} />
                </IconButton>
              </InputAdornment>
            ),
          },
        }}
        sx={{ mb: 3 }}
      />

      <Link variant="body2" color="inherit" sx={{ mb: 1.5 }}>
        {t('auth.forgotPassword')}
      </Link>

      <Button
        fullWidth
        size="large"
        variant="contained"
        color="inherit"
        disabled={loading}
        onClick={handleSignIn}
      >
        {loading ? t('auth.signingIn') : t('auth.signIn')}
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
        <Typography variant="h5">{t('auth.login')}</Typography>
        <Typography
          variant="body2"
          sx={{ color: 'text.secondary' }}
        >
          {t('auth.noAccount')}
          <Link variant="subtitle2" sx={{ ml: 0.5 }}>
            {t('auth.getStarted')}
          </Link>
        </Typography>
      </Box>
      {renderForm}
      <Divider sx={{ my: 3, '&::before, &::after': { borderTopStyle: 'dashed' } }}>
        <Typography
          variant="overline"
          sx={{ color: 'text.secondary', fontWeight: 'fontWeightMedium' }}
        >
          {t('common.or')}
        </Typography>
      </Divider>
      <Box
        sx={{
          gap: 1,
          display: 'flex',
          justifyContent: 'center',
        }}
      >
        <IconButton color="inherit">
          <Iconify width={22} icon="socials:google" />
        </IconButton>
        <IconButton color="inherit">
          <Iconify width={22} icon="socials:github" />
        </IconButton>
        <IconButton color="inherit">
          <Iconify width={22} icon="socials:twitter" />
        </IconButton>
      </Box>

      <Dialog
        open={verificationDialogOpen}
        onClose={() => setVerificationDialogOpen(false)}
        maxWidth="xs"
        fullWidth
      >
        <DialogTitle>
          {verificationType === "EMAIL"
            ? t("auth.emailVerificationRequired")
            : t("auth.phoneVerificationRequired")}
        </DialogTitle>

        <DialogContent>
          <DialogContentText>
            {verificationType === "EMAIL"
              ? t("auth.emailVerificationDescription")
              : t("auth.phoneVerificationDescription")}
          </DialogContentText>
        </DialogContent>

        <DialogActions>
          <Button
            color="error"
            variant="outlined"

            onClick={() => setVerificationDialogOpen(false)}
          >
            {t("common.cancel")}
          </Button>

          <Button
            variant="outlined"
            color="primary"
            onClick={() => {
              setVerificationDialogOpen(false);

              router.push(
                verificationType === "EMAIL"
                  ? `/verify-email?email=${encodeURIComponent(identifier)}`
                  : `/verify-phone?phone=${encodeURIComponent(identifier)}`
              );
            }}
          >
            {verificationType === "EMAIL"
              ? t("auth.verifyEmail")
              : t("auth.verifyPhone")}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
