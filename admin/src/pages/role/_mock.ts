import { Request, Response } from 'express';
import { RoleData } from './data.d';

const roleData: RoleData[] = [
  {
    id: "admin",
    svcs: [
      {
        id: "service-1",
        resources: [
          {
            uri: "/merchant",
            ops: ["READ", "CREATE", "UPDATE", "DELETE"],
          },
        ],
      },
      {
        id: "service-1",
        resources: [
          {
            uri: "/terminal",
            ops: ["READ"],
          },
        ],
      },
    ],
  },
  {
    id: "user",
    svcs: [
      {
        id: "service-1",
        resources: [
          {
            uri: "/merchant",
            ops: ["READ", "CREATE", "UPDATE", "DELETE"],
          }
        ],
      },
    ],
  },
  {
    id: "anon",
    svcs: [
      {
        id: "service-1",
        resources: [
          {
            uri: "/merchant",
            ops: ["READ"],
          },
        ],
      },
    ],
  },
]

export default {
  'GET /admin/apis/role': (_: Request, resp: Response) => {
    resp.send(roleData);
  },
  'GET /admin/apis/role/:id/exist': (req: Request, resp: Response) => {
    const { id } = req.params;
    resp.send(roleData.filter(role => role.id === decodeURIComponent(id)).length > 0);
  },
}