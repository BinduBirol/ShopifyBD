import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';

import { useTranslation } from 'react-i18next';

import { RouterLink } from 'src/routes/components';

import { Logo } from 'src/components/logo';

export function ServerErrorView() {
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
      <Typography variant="h3" sx={{ mb: 2 }}>
        {t('error.server.title')}
      </Typography>

      <Typography
        sx={{
          color: 'text.secondary',
          maxWidth: 520,
          textAlign: 'center',
          mb: 4
        }}
      >
        {t('error.server.description')}
      </Typography>



      <Button
        component={RouterLink}
        href="/"
        size="large"
        variant="contained"
        color="inherit"
      >
        {t('error.server.home')}
      </Button>
    </Container>

  );
}