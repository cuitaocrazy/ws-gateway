export interface Pagination {
  total: number;
  pageSize: number;
  current: number;
}

export interface MethodData {
  id: string;
  name: string;
  remark: string;
}

export interface QueryData {
  id?: string;
  name?: string;
}

export interface MethodListData {
  list: MethodData[];
  pagination: Partial<Pagination>;
}
