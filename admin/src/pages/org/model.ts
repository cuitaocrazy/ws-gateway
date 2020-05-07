import { Effect } from 'dva';
import { Reducer } from 'redux';
import { OrgTreeData, UserData, RoleData, KeyData } from './data';
import { getOrgTree, getUserByOrgId, getRoles, createAndUpdataOrg, deleteOrg, createAndUpdataUser, deleteUser } from './service';

export interface ModelState {
  orgTree: OrgTreeData[];
  users: UserData[];
  roles: RoleData[];
  keys: KeyData[];
  orgId: string;
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    fetchOrgTree: Effect;
    fetchUserByOrgId: Effect;
    fetchRoles: Effect;
    fetchCreateOrUpdateOrg: Effect;
    fetchDeleteOrg: Effect;
    fetchCreateOrUpdateUser: Effect;
    fetchDeleteUser: Effect;
  };
  reducers: {
    setOrgTree: Reducer<ModelState>;
    setUsers: Reducer<ModelState>;
    setRoles: Reducer<ModelState>;
    setKeys: Reducer<ModelState>;
    setOrgId: Reducer<ModelState>;
  };
}

const defaulState: ModelState = {
  orgTree: [],
  users: [],
  roles: [],
  keys: [],
  orgId: "",
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
    *fetchUserByOrgId({ callback, payload }, { call, put }) {
      try {
        const users = yield call(getUserByOrgId, payload);
        yield put({
          type: 'setUsers',
          payload: users,
        });
        if (callback) callback(users);
      } catch (error) { }
    },
    *fetchRoles({ callback }, { call, put }) {
      try {
        const roles = yield call(getRoles);
        yield put({ type: 'setRoles', payload: roles || [] });
        if (callback) callback(roles || []);
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
    *fetchCreateOrUpdateUser({ callback, payload }, { call, put }) {
      try {
        yield call(createAndUpdataUser, payload)
        yield put({
          type: 'fetchUserByOrgId',
          payload: payload.orgId,
        })
        if (callback) callback();
      } catch (error) { }
    },
    *fetchDeleteUser({ callback, payload }, { call, put }) {
      try {
        const { id, orgId } = payload;
        yield call(deleteUser, id)
        yield put({
          type: 'fetchUserByOrgId',
          payload: orgId,
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
    setUsers(state = defaulState, { payload }) {
      return {
        ...state,
        users: payload,
      };
    },
    setRoles(state = defaulState, { payload }) {
      return {
        ...state,
        roles: payload,
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
