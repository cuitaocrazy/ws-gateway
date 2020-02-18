export interface MenuData {
  id: string;
  pId: string;
  name: string;
  icon: string;
  path: string;
  locale: string;
  remark: string;
  children?: MenuData[];
}
