import { Effect } from 'dva';
import { Reducer } from 'redux';
import { getRoles, getSvcs, createAndUpdataRole, deleteRole, getDefaultRole, putDefaultRole } from './service';
import { RoleData, SvcData, KeyData } from './data';

export interface ModelState {
  svcs: SvcData[];
  roles: RoleData[];
  id: string;
  keys: KeyData[];
}

export interface ModelType {
  namespace: string;
  state: ModelState;
  effects: {
    fetchRoles: Effect;
    fetchSvcs: Effect;
    fetchCreateOrUpdateRole: Effect;
    fetchDeleteRole: Effect;
    fetchUpdateDefaultRole: Effect;
  };
  reducers: {
    setRoles: Reducer<ModelState>;
    setSvcs: Reducer<ModelState>;
    setId: Reducer<ModelState>;
    setKeys: Reducer<ModelState>;
  };
}

const defaulState: ModelState = {
  svcs: [],
  roles: [],
  id: "",
  keys: [],
}

const Model: ModelType = {
  namespace: 'role',
  state: defaulState,
  effects: {
    *fetchRoles({ callback }, { call, put }) {
      try {
        const roles = yield call(getRoles);
        const defaultRoleRes = yield call(getDefaultRole);
        yield put({
          type: 'setRoles',
          payload: [{ id: 'default', svcs: defaultRoleRes }, ...roles],
        });
        if (callback) callback([{ id: 'default', svcs: defaultRoleRes }, ...roles]);
      } catch (error) { }
    },
    *fetchSvcs({ callback }, { call, put }) {
      try {
        const svcs = yield call(getSvcs);
        yield put({
          type: 'setSvcs',
          payload: svcs,
        });
        if (callback) callback(svcs);
      } catch (error) { }
    },
    *fetchCreateOrUpdateRole({ callback, payload }, { call, put }) {
      try {
        yield call(createAndUpdataRole, payload)
        yield put({
          type: 'fetchRoles'
        })
        if (callback) callback();
      } catch (error) { }
    },
    *fetchDeleteRole({ callback, payload }, { call, put }) {
      try {
        yield call(deleteRole, payload)
        yield put({
          type: 'fetchRoles'
        })
        if (callback) callback();
      } catch (error) { }
    },
    *fetchUpdateDefaultRole({ callback, payload }, { call, put }) {
      try {
        yield call(putDefaultRole, payload)
        yield put({
          type: 'fetchRoles'
        })
        if (callback) callback();
      } catch (error) { }
    },
  },
  reducers: {
    setRoles(state = defaulState, { payload }) {
      return {
        ...state,
        roles: payload,
      };
    },
    setSvcs(state = defaulState, { payload }) {
      return {
        ...state,
        svcs: payload,
      };
    },
    setId(state = defaulState, { payload }) {
      return {
        ...state,
        id: payload,
      };
    },
    setKeys(state = defaulState, { payload }) {
      const { id } = payload;
      return {
        ...state,
        keys: [...state.keys.filter(key => key.id !== id), payload],
      };
    },
  }
}

export default Model;
