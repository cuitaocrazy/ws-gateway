export interface Pagination {
  total: number;
  pageSize: number;
  current: number;
}

export interface ServiceData {
  id: string;
  md5: string;
  res: string[];
}

export interface QueryData {
  id?: string;
}

export interface ServiceListData {
  list: ServiceData[];
  pagination: Partial<Pagination>;
}
