import { Effect } from 'dva';
import { Reducer } from 'redux';
import { SvcData } from './data';
import { getSvcs, getActualSvcIds, getSvcActualRes, putSvc, deleteSvc } from './service';

export interface ModelState {
  svcIds: string[];
  svcs: SvcData[];
  svcId: string;
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    fetchSvcIds: Effect;
    fetchSvcs: Effect;
    fetchSvcActualRes: Effect;
    fetchUpdateSvc: Effect;
    fetchDeleteSvc: Effect;
  };
  reducers: {
    setSvcIds: Reducer<ModelState>;
    setSvcs: Reducer<ModelState>;
    setSvcId: Reducer<ModelState>;
  };
}

const defaulState: ModelState = {
  svcIds: [],
  svcs: [],
  svcId: '',
}

const Model: ModelType = {
  namespace: 'svc',
  state: defaulState,
  effects: {
    *fetchSvcIds(_, { call, put }) {
      try {
        const response = yield call(getActualSvcIds);
        yield put({
          type: 'setSvcIds',
          payload: response,
        });
      } catch (error) {
      }
    },
    *fetchSvcs(_, { call, put }) {
      try {
        const response = yield call(getSvcs);
        yield put({
          type: 'setSvcs',
          payload: response,
        });
      } catch (error) {
      }
    },
    *fetchSvcActualRes({ callback, payload }, { call }) {
      try {
        const response = yield call(getSvcActualRes, payload);
        if (callback) callback(response);
      } catch (error) {
      }
    },
    * fetchUpdateSvc({ callback, payload }, { call, put }) {
      try {
        const response = yield call(putSvc, payload);
        yield put({ type: 'fetchSvcs' })
        if (callback) callback(response);
      } catch (error) {
      }
    },
    * fetchDeleteSvc({ callback, payload }, { call, put }) {
      try {
        const response = yield call(deleteSvc, payload);
        yield put({ type: 'fetchSvcs' })
        if (callback) callback(response);
      } catch (error) {
      }
    },
  },
  reducers: {
    setSvcIds(state = defaulState, action) {
      return {
        ...state,
        svcIds: action.payload,
      };
    },
    setSvcs(state = defaulState, action) {
      return {
        ...state,
        svcs: action.payload,
      };
    },
    setSvcId(state = defaulState, action) {
      const svcId = action.payload;
      return {
        ...state,
        svcId: state.svcs.map(svc => svc.id).includes(svcId) ? svcId : "",
      };
    },
  }
}

export default Model;
