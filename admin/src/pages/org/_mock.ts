import { Request, Response } from 'express';
import { OrgTreeData, OrgData, UserData } from './data';

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

const userData: UserData[] = [
  {
    id: "admin",
    pwd: "123456",
    orgId: "00",
    roles: [
      {
        appId: "app-1",
        roleName: "admin",
      },
      {
        appId: "app-1",
        roleName: "user",
      },
      {
        appId: "app-2",
        roleName: "admin",
      }
    ],
  }
]

export default {
  'GET /admin/apis/org': (_: Request, resp: Response) => {
    resp.send(makeTree("", orgData));
  },
  'GET /admin/apis/org/:id/exist': (req: Request, resp: Response) => {
    const { id } = req.params;
    resp.send(orgData.filter(org => org.id === decodeURIComponent(id)).length > 0);
  },
  'GET /admin/apis/user': (req: Request, resp: Response) => {
    const { orgId } = req.query;
    resp.send(userData.filter(item => item.orgId === decodeURIComponent(orgId)));
  },
  'GET /admin/apis/user/:id/exist': (req: Request, resp: Response) => {
    const { id } = req.params;
    resp.send(userData.filter(user => user.id === decodeURIComponent(id)).length > 0);
  },
}