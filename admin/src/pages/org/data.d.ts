export interface RoleIdData {
  appId: string;
  roleName: string;
}

export interface OrgData {
  id: string;
  name: string;
}

export interface OrgTreeData {
  org: OrgData;
  children?: OrgTreeData[];
}

export interface UserData {
  id: string;
  pwd: string;
  orgId: string;
  roles: RoleIdData[];
}

export interface RoleData {
  name: string;
}

export interface AppData {
  id: string;
  roles: RoleData[];
}

export interface KeyData {
  orgId: string;
  userId: string;
}
