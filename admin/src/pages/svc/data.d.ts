export interface ResData {
  uri: string;
  ops: ("READ" | "CREATE" | "UPDATE" | "DELETE")[];
}

export interface SvcData {
  id: string;
  resources: ResData[];
}
