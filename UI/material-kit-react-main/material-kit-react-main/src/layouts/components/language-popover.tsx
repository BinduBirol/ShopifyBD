import type { IconButtonProps } from '@mui/material/IconButton';

import { useState, useCallback, useEffect } from 'react';
import { usePopover } from 'minimal-shared/hooks';
import i18n from 'i18next';

import { setLanguage } from '../../i18n/language';

import Box from '@mui/material/Box';
import Popover from '@mui/material/Popover';
import MenuList from '@mui/material/MenuList';
import IconButton from '@mui/material/IconButton';
import MenuItem, { menuItemClasses } from '@mui/material/MenuItem';

// ----------------------------------------------------------------------

export type LanguagePopoverProps = IconButtonProps & {
  data?: {
    value: string;
    label: string;
    icon: string;
  }[];
};

export function LanguagePopover({ data = [], sx, ...other }: LanguagePopoverProps) {
  const { open, anchorEl, onClose, onOpen } = usePopover();

  // GLOBAL language (not local only)
  const [locale, setLocale] = useState(i18n.language || 'en');

  // sync with i18n changes (VERY IMPORTANT)
  useEffect(() => {
    const handleChange = (lng: string) => {
      setLocale(lng);
    };

    i18n.on('languageChanged', handleChange);

    return () => {
      i18n.off('languageChanged', handleChange);
    };
  }, []);

  const handleChangeLang = useCallback(
    (newLang: string) => {
      setLanguage(newLang as 'en' | 'bn'); // saves + updates i18n
      onClose();
    },
    [onClose]
  );

  const currentLang = data.find((lang) => lang.value === locale);

  const renderFlag = (label?: string, icon?: string) => (
    <Box
      component="img"
      alt={label}
      src={icon}
      sx={{
        width: 26,
        height: 20,
        borderRadius: 0.5,
        objectFit: 'cover',
      }}
    />
  );

  const renderMenuList = () => (
    <Popover
      open={open}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      transformOrigin={{ vertical: 'top', horizontal: 'right' }}
    >
      <MenuList
        sx={{
          p: 0.5,
          gap: 0.5,
          width: 160,
          minHeight: 72,
          display: 'flex',
          flexDirection: 'column',

          [`& .${menuItemClasses.root}`]: {
            px: 1,
            gap: 2,
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
            key={option.value}
            selected={option.value === locale}
            onClick={() => handleChangeLang(option.value)}
          >
            {renderFlag(option.label, option.icon)}
            {option.label}
          </MenuItem>
        ))}
      </MenuList>
    </Popover>
  );

  return (
    <>
      <IconButton
        aria-label="Languages button"
        onClick={onOpen}
        sx={[
          (theme) => ({
            p: 0,
            width: 40,
            height: 40,
            ...(open && { bgcolor: theme.vars.palette.action.selected }),
          }),
          ...(Array.isArray(sx) ? sx : [sx]),
        ]}
        {...other}
      >
        {renderFlag(currentLang?.label, currentLang?.icon)}
      </IconButton>

      {renderMenuList()}
    </>
  );
}