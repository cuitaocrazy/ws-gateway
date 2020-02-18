import { Effect } from 'dva';
import { Reducer } from 'redux';
import { MethodListData, QueryData } from './data';
import { find, save, update, remove } from './service';

export interface ModelState {
  query: QueryData;
  data: MethodListData;
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    find: Effect;
    save: Effect;
    update: Effect;
    remove: Effect;
  };
  reducers: {
    setData: Reducer<ModelState>;
  };
}

const Model: ModelType = {
  namespace: 'method',

  state: {
    query: {},
    data: {
      list: [],
      pagination: {
        total: 0,
        pageSize: 0,
        current: 0,
      },
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
