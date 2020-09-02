export interface OrgData {
  id: string;
  name: string;
}

export interface OrgTreeData {
  org: OrgData;
  children?: OrgTreeData[];
}

export interface RoleData {
  id: string;
}

export interface UserData {
  id: string;
  pwd: string;
  orgId: string;
  roles: string[];
  email: string;
}
