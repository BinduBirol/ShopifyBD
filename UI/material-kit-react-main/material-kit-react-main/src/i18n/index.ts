import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import en from './en/commons.json';
import bn from './bn/commons.json';

const savedLang = localStorage.getItem('app-lang') || 'en';

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: en },
    bn: { translation: bn },
  },
  lng: savedLang, // 🔥 IMPORTANT
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false,
  },
});

export default i18n;