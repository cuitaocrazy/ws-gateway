import { Effect } from 'dva';
import { Reducer } from 'redux';
import { getApps, getSvcs, createAndUpdataApp, deleteApp } from './service';
import { AppData, SvcData, KeyData } from './data';

export interface ModelState {
  apps: AppData[];
  svcs: SvcData[];
  appId: string;
  roleId: string;
  keys: KeyData[];
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    fetchApps: Effect;
    fetchSvcs: Effect;
    fetchCreateOrUpdateApp: Effect;
    fetchDeleteApp: Effect;
  };
  reducers: {
    setApps: Reducer<ModelState>;
    setSvcs: Reducer<ModelState>;
    setId: Reducer<ModelState>;
    setKeys: Reducer<ModelState>;
  };
}

const defaulState: ModelState = {
  apps: [],
  svcs: [],
  appId: "",
  roleId: "",
  keys: [],
}

const Model: ModelType = {
  namespace: 'app',
  state: defaulState,
  effects: {
    *fetchApps({ callback }, { call, put }) {
      const apps = yield call(getApps);
      yield put({
        type: 'setApps',
        payload: apps,
      });
      if (callback) callback(apps);
    },
    *fetchSvcs({ callback }, { call, put }) {
      const svcs = yield call(getSvcs);
      yield put({
        type: 'setSvcs',
        payload: svcs,
      });
      if (callback) callback(svcs);
    },
    *fetchCreateOrUpdateApp({ callback, payload }, { call, put }) {
      yield call(createAndUpdataApp, payload)
      yield put({
        type: 'fetchApps'
      })
      if (callback) callback();
    },
    *fetchDeleteApp({ callback, payload }, { call, put }) {
      yield call(deleteApp, payload)
      yield put({
        type: 'fetchApps'
      })
      if (callback) callback();
    },
  },
  reducers: {
    setApps(state = defaulState, { payload }) {
      return {
        ...state,
        apps: payload,
      };
    },
    setSvcs(state = defaulState, { payload }) {
      return {
        ...state,
        svcs: payload,
      };
    },
    setId(state = defaulState, { payload }) {
      const { appId, roleId } = payload;
      return {
        ...state,
        appId,
        roleId,
      };
    },
    setKeys(state = defaulState, { payload }) {
      const { appId, roleId } = payload;
      return {
        ...state,
        keys: [...state.keys.filter(key => !(key.appId === appId && key.roleId === roleId)), payload],
      };
    },
  }
}

export default Model;
