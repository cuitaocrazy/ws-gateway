// https://umijs.org/config/
import { defineConfig } from 'umi';
import defaultSettings from './defaultSettings';
import proxy from './proxy';

const { REACT_APP_ENV } = process.env;

export default defineConfig({
  hash: true,
  publicPath: '/admin/',
  antd: {},
  dva: {
    hmr: true,
  },
  layout: {
    name: 'Admin Manager',
    locale: true,
    siderWidth: 208,
  },
  locale: {
    // default zh-CN
    default: 'zh-CN',
    // default true, when it is true, will use `navigator.language` overwrite default
    antd: true,
    baseNavigator: true,
  },
  dynamicImport: {
    loading: '@/components/PageLoading/index',
  },
  targets: {
    ie: 11,
  },
  history: {
    type: 'hash'
  },
  // umi routes: https://umijs.org/docs/routing
  routes: [
    {
      name: 'login',
      path: '/login',
      component: './login',
      layout: false,
      hideInMenu: true,
    },
    {
      path: '/',
      redirect: '/welcome',
    },
    {
      path: '/welcome',
      name: 'welcome',
      icon: 'home',
      component: './Welcome',
      hideInMenu: true,
    },
    {
      path: '/changePwd',
      name: 'changePwd',
      component: './ChangePwd',
      hideInMenu: true,
    },
    {
      name: 'svc',
      icon: 'database',
      path: '/svc',
      component: './svc',
    },
    {
      name: 'role',
      icon: 'appstore',
      path: '/role',
      component: './role',
    },
    {
      name: 'org',
      icon: 'apartment',
      path: '/org',
      component: './org',
    },
    {
      name: 'user',
      icon: 'user',
      path: '/user',
      component: './user',
    },
    {
      component: './404',
    },
  ],
  // Theme for antd: https://ant.design/docs/react/customize-theme-cn
  theme: {
    // ...darkTheme,
    'primary-color': defaultSettings.primaryColor,
  },
  // @ts-ignore
  title: false,
  ignoreMomentLocale: true,
  proxy: proxy[REACT_APP_ENV || 'dev'],
  manifest: {
    basePath: '/',
  },
});
