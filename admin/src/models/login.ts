import { Reducer } from 'redux';
import { Effect } from 'dva';
import { stringify } from 'querystring';
import router from 'umi/router';
import { fakeAccountLogin, fakeAccountLogout } from '@/services/login';
import { getPageQuery } from '@/utils/utils';

export interface StateType {
  status?: 'ok' | 'error';
}

export interface LoginModelType {
  namespace: string;
  state: StateType;
  effects: {
    login: Effect;
    logout: Effect;
  };
  reducers: {
    changeLoginStatus: Reducer<StateType>;
  };
}

const Model: LoginModelType = {
  namespace: 'login',

  state: {
    status: undefined,
  },

  effects: {
    *login({ payload }, { call, put }) {
      try {
        const response = yield call(fakeAccountLogin, payload);
        // Login successfully
        if (response) {
          yield put({
            type: 'changeLoginStatus',
            payload: 'ok',
          });
          const urlParams = new URL(window.location.href);
          const params = getPageQuery();
          let { redirect } = params as { redirect: string };
          if (redirect) {
            const redirectUrlParams = new URL(redirect);
            if (redirectUrlParams.origin === urlParams.origin) {
              redirect = redirect.substr(urlParams.origin.length);
              if (redirect.match(/^\/.*#/)) {
                redirect = redirect.substr(redirect.indexOf('#') + 1);
              }
            } else {
              window.location.href = '/';
              return;
            }
          }
          router.replace(redirect || '/');
        }
      } catch (error) {
        yield put({
          type: 'changeLoginStatus',
          payload: 'error',
        });
      }
    },

    *logout(_, { call }) {
      try {
        yield call(fakeAccountLogout);
        const { redirect } = getPageQuery();
        if (window.location.pathname !== '/user/login' && !redirect) {
          router.replace({
            pathname: '/user/login',
            search: stringify({
              redirect: window.location.href,
            }),
          });
        }
      } catch (error) {

      }
    },
  },

  reducers: {
    changeLoginStatus(_, { payload }) {
      return {
        status: payload,
      };
    },
  },
};

export default Model;
