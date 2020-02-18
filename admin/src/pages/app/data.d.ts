export type Operator = ("READ" | "CREATE" | "UPDATE" | "DELETE");

export interface ResData {
  uri: string;
  ops: Operator[];
}

export interface SvcData {
  id: string;
  resources: ResData[];
}

export type SvcResData = SvcData;

export interface RoleData {
  name: string;
  resources: SvcResData[];
}

export interface AppData {
  id: string;
  resources: SvcResData[];
  roles: RoleData[];
}

export interface KeyData {
  appId: string;
  roleId: string;
  activeKey: string[];
}
