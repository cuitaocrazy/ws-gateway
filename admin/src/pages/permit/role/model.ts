import { Effect } from 'dva';
import { Reducer } from 'redux';
import { RoleListData, QueryData } from './data';
import { find, save, update, remove, getMenu, getRest, getMethod } from './service';

export interface ModelState {
  query: QueryData;
  data: RoleListData;
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    find: Effect;
    save: Effect;
    update: Effect;
    remove: Effect;
    getMenu: Effect;
    getRoleMenu: Effect;
    saveRoleMenu: Effect;
    getRest: Effect;
    getRoleRest: Effect;
    saveRoleRest: Effect;
  };
  reducers: {
    setData: Reducer<ModelState>;
  };
}

const Model: ModelType = {
  namespace: 'role',

  state: {
    query: {},
    data: {
      list: [],
      pagination: {
        total: 0,
        pageSize: 0,
        current: 0
      }
    },
  },

  effects: {
    *find({ payload }, { call, put }) {
      const response = yield call(find, payload);
      yield put({
        type: 'setData',
        payload: response,
      });
    },
    *save({ payload, callback }, { call, put }) {
      const response = yield call(save, payload);
      yield put({
        type: 'setData',
        payload: response,
      });
      if (callback) callback();
    },
    *update({ payload, callback }, { call, put }) {
      const { id, payload: params } = payload;
      const response = yield call(update, id, params);
      yield put({
        type: 'setData',
        payload: response,
      });
      if (callback) callback();
    },
    *remove({ payload, callback }, { call, put }) {
      yield call(remove, payload);
      const response = yield call(find, payload);
      yield put({
        type: 'setData',
        payload: response,
      });
      if (callback) callback();
    },
    *getMenu({ payload, callback }, { call, put }) {
      const menu = yield call(getMenu);
      if (callback) callback(menu);
    },
    *getRoleMenu() {

    },
    *saveRoleMenu() {

    },
    *getRest({ payload, callback }, { call, put }) {
      const rest = yield call(getRest);
      const method = yield call(getMethod);
      if (callback) callback(rest.list, method.list);
    },
    *getRoleRest() {

    },
    *saveRoleRest() {

    },
  },

  reducers: {
    setData(state, action) {
      return {
        ...(state as ModelState),
        data: action.payload,
      };
    },
  },
};

export default Model;