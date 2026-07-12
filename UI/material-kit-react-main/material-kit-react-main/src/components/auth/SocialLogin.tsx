import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';

import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';

import { Iconify } from 'src/components/iconify';


// ----------------------------------------------------------------------

type SocialProvider = 'google' | 'github' | 'twitter';


export default function SocialLogin() {

    const { t } = useTranslation();

    const { enqueueSnackbar } = useSnackbar();



    const handleSocialLogin = (
        provider: SocialProvider
    ) => {

        enqueueSnackbar(
            t(`auth.${provider}LoginComingSoon`),
            {
                variant: 'warning',
            }
        );

    };



    return (

        <>

            <Divider
                sx={{
                    my: 3,
                    '&::before, &::after': {
                        borderTopStyle: 'dashed'
                    }
                }}
            >

                <Typography
                    variant="overline"
                    sx={{
                        color: 'text.secondary',
                        fontWeight: 'fontWeightMedium'
                    }}
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


                <IconButton
                    color="inherit"
                    onClick={() =>
                        handleSocialLogin('google')
                    }
                >

                    <Iconify
                        width={22}
                        icon="socials:google"
                    />

                </IconButton>




                <IconButton
                    color="inherit"
                    onClick={() =>
                        handleSocialLogin('github')
                    }
                >

                    <Iconify
                        width={22}
                        icon="socials:github"
                    />

                </IconButton>




                <IconButton
                    color="inherit"
                    onClick={() =>
                        handleSocialLogin('twitter')
                    }
                >

                    <Iconify
                        width={22}
                        icon="socials:twitter"
                    />

                </IconButton>



            </Box>


        </>

    );
}