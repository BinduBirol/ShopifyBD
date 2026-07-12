import type { Theme } from '@mui/material/styles';

import { createTheme as createMuiTheme } from '@mui/material/styles';

import { shadows } from './core/shadows';
import { palette } from './core/palette';
import { themeConfig } from './theme-config';
import { components } from './core/components';
import { typography } from './core/typography';
import { customShadows } from './core/custom-shadows';

import type { ThemeOptions } from './types';

// ----------------------------------------------------------------------

export const baseTheme: ThemeOptions = {
  colorSchemes: {
    light: {
      palette: palette.light,
      shadows: shadows.light,
      customShadows: customShadows.light,
    },

    dark: {
      palette: palette.dark,
      shadows: shadows.dark,
      customShadows: customShadows.dark,
    },
  },
  components,
  typography,
  shape: { borderRadius: 8 },
  cssVariables: themeConfig.cssVariables,
};

// ----------------------------------------------------------------------

type CreateThemeProps = {
  themeOverrides?: ThemeOptions;
  language?: string;
};

export function createTheme({
  themeOverrides = {},
  language = 'en',
}: CreateThemeProps = {}): Theme {
  const theme = createMuiTheme(
    baseTheme,
    {
      typography: {
        ...typography,
        fontFamily: getFontFamily(language),
      },
    },
    themeOverrides
  );

  console.log('Theme font:', theme.typography.fontFamily);

  return theme;
}

function getFontFamily(language: string): string {
  switch (language) {
    case 'bn':
      return 'HindSiliguri';

    default:
      return '"DM Sans", sans-serif';
  }
}
