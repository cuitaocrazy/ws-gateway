import { Effect } from 'dva';
import { Reducer } from 'redux';
import { getSvcs } from './service';
import { SvcData } from './data';

export interface ModelState {
  svcs: SvcData[];
  svcId: string;
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    fetchSvcs: Effect;
  };
  reducers: {
    setSvcs: Reducer<ModelState>;
    setSvcId: Reducer<ModelState>;
  };
}

const defaulState: ModelState = {
  svcs: [],
  svcId: '',
}

const Model: ModelType = {
  namespace: 'svc',
  state: defaulState,
  effects: {
    *fetchSvcs(_, { call, put }) {
      const response = yield call(getSvcs);
      if (!(response instanceof Response))
        yield put({
          type: 'setSvcs',
          payload: response,
        });
    },
  },
  reducers: {
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
