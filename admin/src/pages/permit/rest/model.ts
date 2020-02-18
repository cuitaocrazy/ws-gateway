import { Effect } from 'dva';
import { Reducer } from 'redux';
import { RestListData, QueryData } from './data.d';
import { MethodData } from '../method/data.d';
import { find, save, update, remove, getMethod } from './service';

export interface ModelState {
  query: QueryData;
  data: RestListData;
  methods: MethodData[];
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    find: Effect;
    save: Effect;
    update: Effect;
    remove: Effect;
    findMethod: Effect;
  };
  reducers: {
    setData: Reducer<ModelState>;
    setMethod: Reducer<ModelState>;
  };
}

const Model: ModelType = {
  namespace: 'rest',

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
    methods: [],
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
    *findMethod(_, { call, put }) {
      const response = yield call(getMethod);
      yield put({
        type: 'setMethod',
        payload: response.list,
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
    setMethod(state, action) {
      return {
        ...(state as ModelState),
        methods: action.payload,
      };
    },
  },
};

export default Model;
