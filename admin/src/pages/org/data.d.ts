export interface OrgData {
  id: string;
  name: string;
  children?: OrgData[];
}

export interface OrgTreeData {
  org: OrgData;
  children?: OrgTreeData[];
}
