import { usePopover } from 'minimal-shared/hooks';
import { useState, useEffect, useCallback } from 'react';

import Popover from '@mui/material/Popover';
import MenuList from '@mui/material/MenuList';
import IconButton from '@mui/material/IconButton';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import MenuItem, { menuItemClasses } from '@mui/material/MenuItem';
import ContrastIcon from '@mui/icons-material/Contrast';

type ThemeMode = 'light' | 'dark' | 'system';

const STORAGE_KEY = 'theme-mode';

function getSystemTheme(): 'light' | 'dark' {
    if (typeof window === 'undefined') return 'light';
    return window.matchMedia('(prefers-color-scheme: dark)').matches
        ? 'dark'
        : 'light';
}

function resolveTheme(mode: ThemeMode): 'light' | 'dark' {
    if (mode === 'system') return getSystemTheme();
    return mode;
}

export function ThemePopover({
    setMode,
    mode,
}: {
    mode: ThemeMode;
    setMode: (mode: ThemeMode) => void;
}) {
    const { open, anchorEl, onClose, onOpen } = usePopover();

    const [activeMode, setActiveMode] = useState<ThemeMode>(mode);

    // sync with parent
    useEffect(() => {
        setActiveMode(mode);
    }, [mode]);

    // apply theme + persist
    const applyMode = useCallback(
        (newMode: ThemeMode) => {
            setActiveMode(newMode);
            setMode(newMode);
            localStorage.setItem(STORAGE_KEY, newMode);

            const resolved = resolveTheme(newMode);
            document.documentElement.setAttribute('data-theme', resolved);

            onClose();
        },
        [setMode, onClose]
    );

    // eslint-disable-next-line consistent-return
    useEffect(() => {
        if (activeMode !== 'system') return;

        const media = window.matchMedia('(prefers-color-scheme: dark)');

        const handler = () => {
            const resolved = media.matches ? 'dark' : 'light';
            document.documentElement.setAttribute('data-theme', resolved);
        };

        media.addEventListener('change', handler);

        handler();

        return () => {
            media.removeEventListener('change', handler);
        };
    }, [activeMode]);

    const currentIcon =
        activeMode === 'dark' ? (
            <Brightness4Icon  />
        ) : activeMode === 'light' ? (
            <Brightness7Icon sx={{color: 'warning.main'}} />
        ) : (
            <ContrastIcon sx={{color: 'primary.main'}} />
        );

    return (
        <>
            <IconButton onClick={onOpen}>
                {currentIcon}
            </IconButton>

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
                        width: 160,
                        [`& .${menuItemClasses.root}`]: {
                            px: 1,
                            borderRadius: 1,
                            '&.Mui-selected': {
                                bgcolor: 'action.selected',
                            },
                        },
                    }}
                >
                    <MenuItem
                        selected={activeMode === 'light'}
                        onClick={() => applyMode('light')}
                    >
                        <Brightness7Icon fontSize="small" sx={{ mr: 1 , color: 'warning.main'}} />
                        Light
                    </MenuItem>

                    <MenuItem
                        selected={activeMode === 'dark'}
                        onClick={() => applyMode('dark')}
                    >
                        <Brightness4Icon fontSize="small" sx={{ mr: 1 }} />
                        Dark
                    </MenuItem>

                    <MenuItem
                        selected={activeMode === 'system'}
                        onClick={() => applyMode('system')}
                    >
                        <ContrastIcon fontSize="small" sx={{ mr: 1, color: 'primary.main' }} />
                        Auto
                    </MenuItem>
                </MenuList>
            </Popover>
        </>
    );
}