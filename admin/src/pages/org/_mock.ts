import { Request, Response } from 'express';
import { OrgTreeData, OrgData } from './data.d';

const orgData: OrgData[] = [
  {
    id: '00',
    name: '总机构',
  },
  {
    id: '001',
    name: '机构一',
  },

  {
    id: '001001',
    name: '机构一1',
  },
  {
    id: '002',
    name: '机构二',
  },
  {
    id: '003',
    name: '机构三',
  },
  {
    id: '004',
    name: '机构四',
  },
  {
    id: '005',
    name: '机构五',
  },
]

const isSub = (pId: string, id: string) => id.startsWith(pId) && id !== pId

function makeTree(orgId: string, orgs: OrgData[]): OrgTreeData[] {
  const ret: OrgTreeData[] = []
  const subs = orgs.filter(org => isSub(orgId, org.id))
  subs.filter(sub => subs.filter(s => isSub(s.id, sub.id)).length < 1)
    .forEach(org =>
      ret.push({
        org,
        children: makeTree(org.id, orgs),
      })
    )
  return ret
}

export default {
  'GET /admin/apis/org': (_: Request, resp: Response) => {
    resp.send(makeTree("", orgData));
  },
  'GET /admin/apis/org/:id/exist': (req: Request, resp: Response) => {
    const { id } = req.params;
    resp.send(orgData.filter(org => org.id === decodeURIComponent(id)).length > 0);
  },
}