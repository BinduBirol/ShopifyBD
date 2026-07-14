import { useState, useCallback } from 'react';

import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Table from '@mui/material/Table';
import Button from '@mui/material/Button';
import TableBody from '@mui/material/TableBody';
import Typography from '@mui/material/Typography';
import TableContainer from '@mui/material/TableContainer';
import TablePagination from '@mui/material/TablePagination';


import { DashboardContent } from 'src/layouts/dashboard';

import { Iconify } from 'src/components/iconify';
import { Scrollbar } from 'src/components/scrollbar';

import { TableNoData } from '../user/table-no-data';

import { TableEmptyRows } from '../user/table-empty-rows';
import { UserTableToolbar } from '../user/user-table-toolbar';


import type { UserProps } from '../user/user-table-row';
import { Facility } from 'src/types/property/facility';
import { CircularProgress } from '@mui/material';
import { FacilityTableRow } from './facility-table-row';
import { getFacilities } from 'src/api/propertyAxios';

import { useQuery } from '@tanstack/react-query';
import { applyFilter, emptyRows, getComparator } from './util';
import { FacilityTableHead } from './facility-table-head';
import { useTranslation } from 'react-i18next';

// ----------------------------------------------------------------------

export function FacilityView() {
    const table = useFacilityTable();
    const { t } = useTranslation();

    const [filterName, setFilterName] = useState('');

    const {
        data: facilities = [],
        isLoading,
        isError,
    } = useQuery({
        queryKey: ['facilities'],
        queryFn: getFacilities,
    });

    const dataFiltered: Facility[] = applyFilter({
        inputData: facilities,
        comparator: getComparator(table.order, table.orderBy),
        filterName,
    });

    const notFound = !dataFiltered.length && !!filterName;

    if (isLoading) {
        return (
            <DashboardContent>
                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'center',
                        py: 8,
                    }}
                >
                    <CircularProgress />
                </Box>
            </DashboardContent>
        );
    }

    if (isError) {
        return (
            <DashboardContent>
                <Typography color="error">
                    Failed to load facilities.
                </Typography>
            </DashboardContent>
        );
    }

    return (
        <DashboardContent>
            <Box
                sx={{
                    mb: 5,
                    display: 'flex',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h4" sx={{ flexGrow: 1 }}>
                    {t('nav.myFacilities')}
                </Typography>

                <Button
                    variant="contained"
                    color="inherit"
                    startIcon={<Iconify icon="mingcute:add-line" />}
                >
                    New Facility
                </Button>
            </Box>

            <Card>
                <UserTableToolbar
                    numSelected={table.selected.length}
                    filterName={filterName}
                    onFilterName={(event: React.ChangeEvent<HTMLInputElement>) => {
                        setFilterName(event.target.value);
                        table.onResetPage();
                    }}
                />

                <Scrollbar>
                    <TableContainer sx={{ overflow: 'unset' }}>
                        <Table sx={{ minWidth: 900 }}>
                            <FacilityTableHead
                                order={table.order}
                                orderBy={table.orderBy}
                                rowCount={facilities.length}
                                numSelected={table.selected.length}
                                onSort={table.onSort}
                                onSelectAllRows={(checked) =>
                                    table.onSelectAllRows(
                                        checked,
                                        facilities
                                            .filter((facility) => facility.id)
                                            .map((facility) => facility.id!)
                                    )
                                }
                                headLabel={[
                                    { id: 'name', label: t('facility.name') },
                                    { id: 'type', label: t('facility.type') },
                                    { id: 'city', label: t('facility.city') },
                                    { id: 'country', label: t('facility.country') },
                                    { id: 'userRole', label: t('facility.role') },
                                    { id: 'actions', label: '' },
                                ]}
                            />

                            <TableBody>
                                {dataFiltered
                                    .slice(
                                        table.page * table.rowsPerPage,
                                        table.page * table.rowsPerPage + table.rowsPerPage
                                    )
                                    .map((row) => (
                                        <FacilityTableRow
                                            key={row.id ?? row.name}
                                            row={row}
                                            selected={!!row.id && table.selected.includes(row.id)}
                                            onSelectRow={() => {
                                                if (row.id) {
                                                    table.onSelectRow(row.id);
                                                }
                                            }}
                                        />
                                    ))}

                                <TableEmptyRows
                                    height={68}
                                    emptyRows={emptyRows(
                                        table.page,
                                        table.rowsPerPage,
                                        facilities.length
                                    )}
                                />

                                {notFound && (
                                    <TableNoData searchQuery={filterName} />
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Scrollbar>

                <TablePagination
                    component="div"
                    page={table.page}
                    count={facilities.length}
                    rowsPerPage={table.rowsPerPage}
                    onPageChange={table.onChangePage}
                    rowsPerPageOptions={[5, 10, 25]}
                    onRowsPerPageChange={table.onChangeRowsPerPage}
                />
            </Card>
        </DashboardContent>
    );
}

// ----------------------------------------------------------------------
export function useFacilityTable() {
    const [page, setPage] = useState(0);
    //const [orderBy, setOrderBy] = useState<keyof Facility>('name');
    const [orderBy, setOrderBy] = useState('name');
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [selected, setSelected] = useState<string[]>([]);
    const [order, setOrder] = useState<'asc' | 'desc'>('asc');


    const onSort = useCallback(
        (id: string) => {
            const isAsc = orderBy === id && order === 'asc';
            setOrder(isAsc ? 'desc' : 'asc');
            setOrderBy(id);
        },
        [order, orderBy]
    );

    const onSelectAllRows = useCallback((checked: boolean, ids: string[]) => {
        setSelected(checked ? ids : []);
    }, []);

    const onSelectRow = useCallback((id: string) => {
        setSelected((prev) =>
            prev.includes(id)
                ? prev.filter((item) => item !== id)
                : [...prev, id]
        );
    }, []);

    const onResetPage = useCallback(() => {
        setPage(0);
    }, []);

    const onChangePage = useCallback((_: unknown, newPage: number) => {
        setPage(newPage);
    }, []);

    const onChangeRowsPerPage = useCallback(
        (event: React.ChangeEvent<HTMLInputElement>) => {
            setRowsPerPage(Number(event.target.value));
            setPage(0);
        },
        []
    );

    return {
        page,
        order,
        orderBy,
        rowsPerPage,
        selected,
        onSort,
        onSelectRow,
        onSelectAllRows,
        onResetPage,
        onChangePage,
        onChangeRowsPerPage,
    };
}