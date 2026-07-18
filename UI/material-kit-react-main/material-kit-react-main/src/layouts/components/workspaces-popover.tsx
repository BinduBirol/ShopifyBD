import type { ButtonBaseProps } from '@mui/material/ButtonBase';

import { useState, useCallback, useEffect } from 'react';
import { varAlpha } from 'minimal-shared/utils';

import Box from '@mui/material/Box';
import Popover from '@mui/material/Popover';
import MenuList from '@mui/material/MenuList';
import ButtonBase from '@mui/material/ButtonBase';
import MenuItem, { menuItemClasses } from '@mui/material/MenuItem';

import { Label } from 'src/components/label';
import { Iconify } from 'src/components/iconify';

import { useTranslation } from 'react-i18next';
import { Navigate, useNavigate } from 'react-router-dom';
import { useWorkspace } from 'src/routes/hooks/use-workspace';


// ----------------------------------------------------------------------

export type WorkspacesPopoverProps = ButtonBaseProps & {
  data?: {
    id: string;
    name: string;
    logo: string;
    plan: string;
  }[];
};

// ----------------------------------------------------------------------

export function WorkspacesPopover({
  data = [],
  sx,
  ...other
}: WorkspacesPopoverProps) {
  //const [workspace, setWorkspace] = useState(data[0] ?? null);
  const { workspace, setWorkspace } = useWorkspace();

  const [openPopover, setOpenPopover] =
    useState<HTMLButtonElement | null>(null);

  const { t } = useTranslation();

  // Set first facility as active when API data arrives
  useEffect(() => {
    if (data.length === 0) return;

    const savedId = localStorage.getItem(
      'active_workspace_id'
    );

    const activeWorkspace = data.find(
      (item) => item.id === savedId
    );

    if (activeWorkspace) {
      setWorkspace(activeWorkspace);
    } else {
      setWorkspace(data[0]);

      localStorage.setItem(
        'active_workspace_id',
        data[0].id
      );
    }
  }, [data]);
  const handleOpenPopover = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>) => {
      setOpenPopover(event.currentTarget);
    },
    []
  );

  const navigate = useNavigate();

  const handleClosePopover = useCallback(() => {
    setOpenPopover(null);
  }, []);

  const handleChangeWorkspace = useCallback(
    (newValue: (typeof data)[number]) => {
      setWorkspace(newValue);

      handleClosePopover();
    },
    [setWorkspace, handleClosePopover]
  );
  const handleAddWorkspace = () => {
    handleClosePopover();

    // replace with your route
    console.log('Navigate to create facility');

    navigate('property/facility/create');
  };

  const renderAvatar = (alt: string, src: string) => (
    <Box
      component="img"
      alt={alt}
      src={src}
      sx={{
        width: 24,
        height: 24,
        borderRadius: '50%',
      }}
    />
  );

  const renderLabel = (plan: string) => (
    <Label color={plan === 'OWNER' ? 'default' : 'info'}>
      {t(`userRole.${plan}`)}
    </Label>
  );

  return (
    <>
      <ButtonBase
        disableRipple
        onClick={(event) => {
          if (data.length === 0) {
            handleAddWorkspace();
            return;
          }

          handleOpenPopover(event);
        }}
        sx={{
          pl: 2,
          py: 3,
          gap: 1.5,
          pr: 1.5,
          width: 1,
          borderRadius: 1.5,
          textAlign: 'left',
          justifyContent: 'flex-start',
          bgcolor: (theme) =>
            varAlpha(theme.vars.palette.grey['500Channel'], 0.08),
          ...sx,
        }}
        {...other}
      >
        {workspace ? (
          <>
            {renderAvatar(workspace.name, workspace.logo)}

            <Box
              sx={{
                gap: 1,
                flexGrow: 1,
                minWidth: 0, // important for flex truncation
                display: 'flex',
                alignItems: 'center',
                typography: 'body2',
                fontWeight: 'fontWeightSemiBold',
              }}
            >
              <Box
                component="span"
                sx={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  flexGrow: 1,
                  minWidth: 0,
                }}
              >
                {workspace.name}
              </Box>

              {renderLabel(workspace.plan)}
            </Box>

            <Iconify
              width={16}
              icon="carbon:chevron-sort"
              sx={{ color: 'text.disabled' }}
            />
          </>
        ) : (
          <>
            <Iconify
              icon="mingcute:add-line"
              color='primary.main'
              width={20}
            />

            <Box
              component="span"
              sx={{
                flexGrow: 1,
                color: 'primary.main',
                typography: 'body1',
              }}
            >
              {t('business.add_new')}
            </Box>
          </>
        )}
      </ButtonBase>

      <Popover
        open={!!openPopover}
        anchorEl={openPopover}
        onClose={handleClosePopover}
      >
        <MenuList
          disablePadding
          sx={{
            p: 0.5,
            gap: 0.5,
            width: 260,
            display: 'flex',
            flexDirection: 'column',

            [`& .${menuItemClasses.root}`]: {
              p: 1.5,
              gap: 1.5,
              borderRadius: 0.75,

              [`&.${menuItemClasses.selected}`]: {
                bgcolor: 'action.selected',
                fontWeight: 'fontWeightSemiBold',
              },
            },
          }}
        >
          {data.map((option) => (

            <MenuItem
              key={option.id}
              selected={option.id === workspace?.id}
              onClick={() => handleChangeWorkspace(option)}
            >
              {renderAvatar(option.name, option.logo)}

              <Box
                component="span"
                sx={{
                  flexGrow: 1,
                  minWidth: 0,
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
              >
                {option.name}
              </Box>

              {renderLabel(option.plan)}
            </MenuItem>
          ))}

          <MenuItem
            onClick={handleAddWorkspace}
            sx={{
              color: 'primary.main',
              fontWeight: 600,
            }}
          >
            <Iconify
              icon="mingcute:add-line"
              width={18}
            />

            {t('business.add_new')}
          </MenuItem>
        </MenuList>
      </Popover>
    </>
  );
}