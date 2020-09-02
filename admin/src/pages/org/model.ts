import { Effect } from 'dva';
import { Reducer } from 'redux';
import { OrgTreeData } from './data';
import { getOrgTree, createAndUpdataOrg, deleteOrg } from './service';

export interface ModelState {
  orgTree: OrgTreeData[];
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    fetchOrgTree: Effect;
    fetchCreateOrUpdateOrg: Effect;
    fetchDeleteOrg: Effect;
  };
  reducers: {
    setOrgTree: Reducer<ModelState>;
  };
}

const defaulState: ModelState = {
  orgTree: [],
}

const Model: ModelType = {
  namespace: 'org',
  state: defaulState,
  effects: {
    *fetchOrgTree({ callback }, { call, put }) {
      try {
        const orgTree = yield call(getOrgTree);
        yield put({
          type: 'setOrgTree',
          payload: orgTree,
        });
        if (callback) callback(orgTree);
      } catch (error) { }
    },
    *fetchCreateOrUpdateOrg({ callback, payload }, { call, put }) {
      try {
        yield call(createAndUpdataOrg, payload)
        yield put({
          type: 'fetchOrgTree'
        })
        if (callback) callback();
      } catch (error) { }
    },
    *fetchDeleteOrg({ callback, payload }, { call, put }) {
      try {
        yield call(deleteOrg, payload)
        yield put({
          type: 'fetchOrgTree'
        })
        if (callback) callback();
      } catch (error) { }
    },
  },
  reducers: {
    setOrgTree(state = defaulState, { payload }) {
      return {
        ...state,
        orgTree: payload,
      };
    },
  },
}

export default Model;
