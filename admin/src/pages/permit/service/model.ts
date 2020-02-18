import { Effect } from 'dva';
import { Reducer } from 'redux';
import { ServiceListData, QueryData } from './data';
import { find } from './service';

export interface ModelState {
  query: QueryData;
  data: ServiceListData;
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    find: Effect;
  };
  reducers: {
    setData: Reducer<ModelState>;
  };
}

const Model: ModelType = {
  namespace: 'service',

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
