export type Operator = ("READ" | "CREATE" | "UPDATE" | "DELETE");

export interface ResData {
  uri: string;
  ops: Operator[];
}

export interface SvcData {
  id: string;
  resources: ResData[];
}

export interface RoleData {
  id: string;
  svcs: SvcData[];
}

export interface KeyData {
  id: string;
  activeKey: string[];
}
