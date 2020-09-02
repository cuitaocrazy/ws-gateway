import { Request, Response } from 'express';
import { UserData } from './data.d';

const userData: UserData[] = [
  {
    id: "admin",
    pwd: "123456",
    orgId: "00",
    roles: ["admin", "user"],
    email: "admin@example.com",
  }
]

export default {
  'GET /admin/apis/user': (req: Request, resp: Response) => {
    const { org_id } = req.query;
    resp.send(userData.filter(item => item.orgId === decodeURIComponent(org_id as string)));
  },
  'GET /admin/apis/user/:id/exist': (req: Request, resp: Response) => {
    const { id } = req.params;
    resp.send(userData.filter(user => user.id === decodeURIComponent(id)).length > 0);
  },
}