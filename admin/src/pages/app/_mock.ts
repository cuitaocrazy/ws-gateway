import { Request, Response } from 'express';
import { AppData } from './data';

const appData: AppData[] = [
  {
    id: "app-1",
    resources: [
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
    roles: [
      {
        name: "admin",
        resources: [
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
      {
        name: "user",
        resources: [
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

      {
        name: "anon",
        resources: [
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
    ],
  },
  {
    id: "app-2",
    resources: [
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
    roles: [
      {
        name: "admin",
        resources: [
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
      {
        name: "user",
        resources: [
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

      {
        name: "anon",
        resources: [
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
    ],
  },
]

export default {
  'GET /admin/apis/app': (_: Request, resp: Response) => {
    resp.send(appData);
  },
  'GET /admin/apis/app/:id/exist': (req: Request, resp: Response) => {
    const { id } = req.params;
    resp.send(appData.filter(app => app.id === decodeURIComponent(id)).length > 0);
  },
}