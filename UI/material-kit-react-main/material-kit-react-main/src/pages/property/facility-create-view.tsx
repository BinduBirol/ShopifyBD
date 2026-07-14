import { useMemo, useState, useCallback } from 'react';

import { z } from 'zod';
import axios from 'axios';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import { useTranslation } from 'react-i18next';
import { useSnackbar } from 'notistack';

import {
    createFacility,
} from 'src/api/propertyAxios';

import { Facility, FacilityType } from 'src/types/property/facility';
import { RoleName } from 'src/types/auth/userRole';

export function FacilityCreateView() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();

    const [loading, setLoading] = useState(false);

    const facilityTypeOptions = Object.values(FacilityType).map((value) => ({
        value,
        label: t(`facilityType.${value}`),
    }));

    const roleOptions = Object.values(RoleName).map((value) => ({
        value,
        label: t(`userRole.${value}`),
    }));

    type FacilityForm = z.infer<typeof schema>;

    const schema = useMemo(
        () =>
            z.object({
                name: z.string().min(1, t('facility.validation.nameRequired')),

                type: z
                    .string()
                    .min(1, t('facility.validation.typeRequired'))
                    .refine(
                        (value) => Object.values(FacilityType).includes(value as FacilityType),
                        {
                            message: t('facility.validation.typeRequired'),
                        }
                    ),

                addressLine1: z
                    .string()
                    .min(1, t('facility.validation.addressLine1Required')),

                addressLine2: z.string().optional(),

                city: z.string().optional(),

                country: z.string().optional(),

                postalCode: z.string().optional(),

                description: z.string().optional(),

                userRole: z
                    .string()
                    .min(1, t('facility.validation.roleRequired'))
                    .refine(
                        (value) => Object.values(RoleName).includes(value as RoleName),
                        {
                            message: t('facility.validation.roleRequired'),
                        }
                    ),
            }),
        [t]
    );

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<FacilityForm>({
        resolver: zodResolver(schema),

        defaultValues: {
            //id: undefined,
            name: '',
            type: '',
            addressLine1: '',
            addressLine2: '',
            city: '',
            country: '',
            postalCode: '',
            description: '',
            userRole: '',
        },
    });

    const onSubmit = useCallback(
        async (data: FacilityForm) => {
            try {
                setLoading(true);

                const request: Facility = {
                    ...data,
                    type: data.type as FacilityType,
                    userRole: data.userRole as RoleName,
                };

                const response = await createFacility(request);

                if (!response.success) {
                    enqueueSnackbar(response.error?.message, {
                        variant: 'error',
                    });
                    return;
                }

                enqueueSnackbar(response.data, {
                    variant: 'success',
                });
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
                    enqueueSnackbar(t('common.somethingWentWrong'), {
                        variant: 'error',
                    });
                }
            } finally {
                setLoading(false);
            }
        },
        [enqueueSnackbar, t]
    );

    return (
        <Card
            sx={{
                p: 4,
                mx: {
                    xs: 2,
                    sm: 3,
                    md: 4,
                    lg: 6,
                    xl: 8,
                },
                mb: 6
            }}
        >
            <Typography variant="h4" sx={{ mb: 4 }}>
                {t('facility.create')}
            </Typography>

            <Box component="form" onSubmit={handleSubmit(onSubmit)}>
                <Grid container spacing={3}>
                    <Grid size={{ xs: 12, md: 4 }}>
                        <TextField
                            fullWidth
                            label={t('facility.name')}
                            {...register('name')}
                            error={!!errors.name}
                            helperText={errors.name?.message}
                        />
                    </Grid>

                    <Grid size={{ xs: 12, md: 4 }}>
                        <TextField
                            select
                            fullWidth
                            label={t('facility.type')}
                            {...register('type')}
                            error={!!errors.type}
                            helperText={errors.type?.message}
                        >
                            <MenuItem value="">
                                <em>{t('common.select')}</em>
                            </MenuItem>

                            {facilityTypeOptions.map((option) => (
                                <MenuItem key={option.value} value={option.value}>
                                    {option.label}
                                </MenuItem>
                            ))}
                        </TextField>
                    </Grid>

                    <Grid size={{ xs: 12, md: 4 }}>
                        <TextField
                            select
                            fullWidth
                            label={t('facility.role')}
                            {...register('userRole')}
                            error={!!errors.userRole}
                            helperText={errors.userRole?.message}
                        >
                            <MenuItem value="">
                                <em>{t('common.select')}</em>
                            </MenuItem>

                            {roleOptions.map((option) => (
                                <MenuItem key={option.value} value={option.value}>
                                    {option.label}
                                </MenuItem>
                            ))}
                        </TextField>
                    </Grid>



                    <Grid size={{ xs: 12, md: 6 }}>
                        <TextField
                            fullWidth
                            label={t('facility.addressLine1')}
                            {...register('addressLine1')}
                            error={!!errors.addressLine1}
                            helperText={errors.addressLine1?.message}
                        />
                    </Grid>

                    <Grid size={{ xs: 12, md: 6 }}>
                        <TextField
                            fullWidth
                            label={t('facility.addressLine2')}
                            {...register('addressLine2')}
                        />
                    </Grid>

                    <Grid size={{ xs: 12, md: 4 }}>
                        <TextField
                            fullWidth
                            label={t('facility.city')}
                            {...register('city')}
                        />
                    </Grid>

                    <Grid size={{ xs: 12, md: 4 }}>
                        <TextField
                            fullWidth
                            label={t('facility.country')}
                            {...register('country')}
                        />
                    </Grid>

                    <Grid size={{ xs: 12, md: 4 }}>
                        <TextField
                            fullWidth
                            label={t('facility.postalCode')}
                            {...register('postalCode')}
                        />
                    </Grid>

                    <Grid size={12}>
                        <TextField
                            fullWidth
                            multiline
                            rows={4}
                            label={t('facility.description')}
                            {...register('description')}
                        />
                    </Grid>



                    <Grid size={12}>
                        <Box
                            sx={{
                                display: 'flex',
                                justifyContent: 'flex-end',
                                mt: 2,
                            }}
                        >
                            <Button
                                type="submit"
                                variant="contained"
                                disabled={loading}
                                size='large'
                            >
                                {loading
                                    ? t('common.creating')
                                    : t('facility.create')}
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            </Box>
        </Card>
    );
}