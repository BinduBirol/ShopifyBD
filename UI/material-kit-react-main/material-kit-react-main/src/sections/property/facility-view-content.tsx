import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    Chip,
    Divider,
    Grid,
    Stack,
    Typography,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

import { DashboardContent } from 'src/layouts/dashboard';

import type { Facility } from 'src/types/property/facility';
import CustomBreadcrumbs from 'src/routes/components/custom-breadcrumbs';
import DeleteFacilityButton from 'src/components/dialog/DeleteConfirmationButton';
import DeleteConfirmationButton from 'src/components/dialog/DeleteConfirmationButton';
import { deleteFacility } from 'src/api/propertyAxios';
import { enqueueSnackbar } from 'notistack';


type Props = {
    facility: Facility;
};

export default function FacilityViewContent({ facility }: Props) {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const activeWorkspaceId = localStorage.getItem('active_workspace_id');

    const handleEdit = () => {
        navigate(`/property/facility/${facility.id}/edit`);
    };



    const handleDeleteFacility = async () => {
        if (!facility?.id) {
            return;
        }

        try {
            const response = await deleteFacility(facility.id);

            if (response.success) {
                enqueueSnackbar(response.data || t('common.success'), {
                    variant: 'success',
                });
                navigate('/property/facility/list');
            } else {
                enqueueSnackbar(response.error?.message || t('common.somethingWentWrong'), {
                    variant: 'error',
                });
            }

        } catch (error) {
            console.log(error);
            enqueueSnackbar(t('common.somethingWentWrong'), {
                variant: 'error',
            });
        }
    };

    return (
        <DashboardContent>

            <CustomBreadcrumbs
                items={[
                    {
                        label: t('nav.properties'),
                    },
                    {
                        label: t('nav.myFacilities'),
                        href: '/property/facility/list',
                    },

                    {
                        label: t('common.details'),
                    },

                    {
                        label: facility.name,
                    },

                ]}
            />

            <Card>
                <CardContent>
                    <Stack spacing={3}>

                        {/* Header */}
                        <Stack
                            direction="row"
                            sx={{
                                justifyContent: 'space-between',
                                alignItems: 'center',
                            }}
                        >
                            <Stack spacing={0.5}>
                                <Typography variant="h5">
                                    {facility.name}
                                </Typography>

                                <Typography
                                    variant="body2"
                                    color="text.secondary"
                                >
                                    {t(`facilityType.${facility.type}`)}
                                </Typography>
                            </Stack>

                            <Button
                                variant="outlined"
                                color='primary'
                                startIcon={<EditIcon />}
                                onClick={handleEdit}
                            >
                                {t('common.edit')}
                            </Button>
                        </Stack>


                        <Divider />




                        <Grid container spacing={3}>

                            <Grid size={{ xs: 12, md: 4 }}>
                                <Typography variant="caption" color="text.secondary">
                                    {t('facility.type')}
                                </Typography>

                                <Typography>
                                    {t(`facilityType.${facility.type}`)}
                                </Typography>
                            </Grid>


                            <Grid size={{ xs: 12, md: 4 }}>
                                <Typography variant="caption" color="text.secondary">
                                    {t('facility.role')}
                                </Typography>
                                <Typography>
                                    {t(`userRole.${facility.userRole}`)}
                                </Typography>
                            </Grid>


                            <Grid size={4}>
                                <Typography variant="caption" color="text.secondary">
                                    {t('facility.addressLine1')}
                                </Typography>

                                <Typography>
                                    {facility.addressLine1}
                                </Typography>
                            </Grid>


                            {facility.addressLine2 && (
                                <Grid size={12}>
                                    <Typography variant="caption" color="text.secondary">
                                        {t('facility.addressLine2')}
                                    </Typography>

                                    <Typography>
                                        {facility.addressLine2}
                                    </Typography>
                                </Grid>
                            )}


                            <Grid size={{ xs: 12, md: 4 }}>
                                <Typography variant="caption" color="text.secondary">
                                    {t('facility.city')}
                                </Typography>

                                <Typography>
                                    {facility.city || '-'}
                                </Typography>
                            </Grid>


                            <Grid size={{ xs: 12, md: 4 }}>
                                <Typography variant="caption" color="text.secondary">
                                    {t('facility.country')}
                                </Typography>

                                <Typography>
                                    {facility.country || '-'}
                                </Typography>
                            </Grid>


                            <Grid size={{ xs: 12, md: 4 }}>
                                <Typography variant="caption" color="text.secondary">
                                    {t('facility.postalCode')}
                                </Typography>

                                <Typography>
                                    {facility.postalCode || '-'}
                                </Typography>
                            </Grid>


                            {facility.description && (
                                <Grid size={12}>
                                    <Typography variant="caption" color="text.secondary">
                                        {t('facility.description')}
                                    </Typography>

                                    <Typography sx={{ whiteSpace: 'pre-wrap' }}>
                                        {facility.description}
                                    </Typography>
                                </Grid>
                            )}

                        </Grid>

                    </Stack>
                </CardContent>
            </Card>

            {activeWorkspaceId === facility.id && (
                <Alert
                    severity="warning"
                    sx={{ mt: 3 }}
                >
                    {t('facility.activeFacilityDeleteWarning')}
                </Alert>
            )}

            {activeWorkspaceId !== facility.id &&
                (facility.userRole === "OWNER" ||
                    facility.userRole === "PROPERTY_MANAGER") && (
                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'flex-end',
                            mb: 3,
                            mt: 4,
                        }}
                    >
                        <DeleteConfirmationButton
                            title={t('facility.deleteFacility')}
                            details={t('facility.deleteFacilityConfirm')}
                            onConfirm={handleDeleteFacility}
                        />
                    </Box>
                )}
        </DashboardContent>
    );
}