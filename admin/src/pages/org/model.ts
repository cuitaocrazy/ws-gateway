import { Effect } from 'dva';
import { Reducer } from 'redux';
import { OrgTreeData, UserData, AppData, KeyData } from './data';
import { getOrgTree, getUserByOrgId, getApps, createAndUpdataOrg, deleteOrg, createAndUpdataUser, deleteUser } from './service';

export interface ModelState {
  orgTree: OrgTreeData[];
  users: UserData[];
  apps: AppData[];
  keys: KeyData[];
  orgId: string;
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    fetchOrgTree: Effect;
    fetchUserByOrgId: Effect;
    fetchApps: Effect;
    fetchCreateOrUpdateOrg: Effect;
    fetchDeleteOrg: Effect;
    fetchCreateOrUpdateUser: Effect;
    fetchDeleteUser: Effect;
  };
  reducers: {
    setOrgTree: Reducer<ModelState>;
    setUsers: Reducer<ModelState>;
    setApps: Reducer<ModelState>;
    setKeys: Reducer<ModelState>;
    setOrgId: Reducer<ModelState>;
  };
}

const defaulState: ModelState = {
  orgTree: [],
  users: [],
  apps: [],
  keys: [],
  orgId: "",
}

const Model: ModelType = {
  namespace: 'org',
  state: defaulState,
  effects: {
    *fetchOrgTree({ callback }, { call, put }) {
      const orgTree = yield call(getOrgTree);
      yield put({
        type: 'setOrgTree',
        payload: orgTree,
      });
      if (callback) callback(orgTree);
    },
    *fetchUserByOrgId({ callback, payload }, { call, put }) {
      const users = yield call(getUserByOrgId, payload);
      yield put({
        type: 'setUsers',
        payload: users,
      });
      if (callback) callback(users);
    },
    *fetchApps({ callback }, { call, put }) {
      const apps = yield call(getApps);
      yield put({ type: 'setApps', payload: apps || [] });
      if (callback) callback(apps || []);
    },
    *fetchCreateOrUpdateOrg({ callback, payload }, { call, put }) {
      yield call(createAndUpdataOrg, payload)
      yield put({
        type: 'fetchOrgTree'
      })
      if (callback) callback();
    },
    *fetchDeleteOrg({ callback, payload }, { call, put }) {
      yield call(deleteOrg, payload)
      yield put({
        type: 'fetchOrgTree'
      })
      if (callback) callback();
    },
    *fetchCreateOrUpdateUser({ callback, payload }, { call, put }) {
      yield call(createAndUpdataUser, payload)
      yield put({
        type: 'fetchUserByOrgId',
        payload: payload.orgId,
      })
      if (callback) callback();
    },
    *fetchDeleteUser({ callback, payload }, { call, put }) {
      const { id, orgId } = payload;
      yield call(deleteUser, id)
      yield put({
        type: 'fetchUserByOrgId',
        payload: orgId,
      })
      if (callback) callback();
    },
  },
  reducers: {
    setOrgTree(state = defaulState, { payload }) {
      return {
        ...state,
        orgTree: payload,
      };
    },
    setUsers(state = defaulState, { payload }) {
      return {
        ...state,
        users: payload,
      };
    },
    setApps(state = defaulState, { payload }) {
      return {
        ...state,
        apps: payload,
      };
    },
    setKeys(state = defaulState, { payload }) {
      const { orgId, userId } = payload;
      return {
        ...state,
        keys: [
          ...state.keys.filter(key => key.orgId !== orgId),
          { orgId, userId }
        ],
      };
    },
    setOrgId(state = defaulState, { payload }) {
      return {
        ...state,
        orgId: payload,
      };
    },
  },
}

export default Model;
