/* eslint-disable @typescript-eslint/ban-ts-comment */
// @ts-nocheck
const angularLanguages = {
  es: async (): Promise<void> => import('@angular/common/locales/es'),
  en: async (): Promise<void> => import('@angular/common/locales/en'),
  // jhipster-needle-i18n-language-angular-loader - JHipster will add languages in this object
};

const languagesData = {
  es: async (): Promise<any> => import('i18n/es.json').catch(),
  en: async (): Promise<any> => import('i18n/en.json').catch(),
  // jhipster-needle-i18n-language-loader - JHipster will add languages in this object
};

export const loadLocale = (locale: keyof typeof angularLanguages): Promise<any> => {
  angularLanguages[locale]();
  return languagesData[locale]();
};
