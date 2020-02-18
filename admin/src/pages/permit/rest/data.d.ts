export interface Pagination {
  total: number;
  pageSize: number;
  current: number;
}

export interface RestData {
  id: string;
  path: string;
  method: string;
  remark: string;
}

export interface QueryData {
  path?: string;
  method?: string;
}

export interface RestListData {
  list: RestData[];
  pagination: Partial<Pagination>;
}
