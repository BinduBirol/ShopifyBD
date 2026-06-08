import i18n from './index';

const LANGUAGE_KEY = 'app-lang';

export function setLanguage(lang: 'en' | 'bn') {
  i18n.changeLanguage(lang);
  localStorage.setItem(LANGUAGE_KEY, lang);
}

export function getLanguage() {
  return (localStorage.getItem(LANGUAGE_KEY) || 'en') as 'en' | 'bn';
}