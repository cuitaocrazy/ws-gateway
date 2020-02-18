import path from 'path';
import jsonfile from 'jsonfile';
import { Request, Response } from 'express';
import { RestData } from '@/pages/permit/rest/data';

const file = path.resolve('mock/data/rest.json');

function find(req: Request, res: Response) {
  const params = req.query;
  let pageSize = 5;
  if (params.pageSize) {
    pageSize = params.pageSize * 1;
  }
  let current = 1;
  if (params.current) {
    current = params.current * 1;
  }
  jsonfile
    .readFile(file)
    .then(dataSource => {
      const list = dataSource
        .filter((item: RestData) => !params.path || params.path === '' || params.path === item.path)
        .filter((item: RestData) => !params.method || params.method === '' || params.method === item.method);
      res.json({
        list: list.slice((current - 1) * pageSize, current * pageSize),
        pagination: {
          total: list.length,
          pageSize,
          current,
        },
      });
    })
    .catch(error => res.status(500).send(error));
}

function save(req: Request, res: Response, u: any, b: any) {
  const body = (b && b.body) || req.body;
  jsonfile
    .readFile(file)
    .then(dataSource => {
      jsonfile.writeFileSync(file, [...dataSource, body], { spaces: 2 });
      find(req, res);
    })
    .catch(error => res.status(500).send(error));
}

function update(req: Request, res: Response, u: any, b: any) {
  const body = (b && b.body) || req.body;
  const { id } = req.params;
  jsonfile
    .readFile(file)
    .then(dataSource => {
      jsonfile.writeFileSync(
        file,
        dataSource.map((item: RestData) => (item.id === decodeURIComponent(id) ? body : item)),
        { spaces: 2 },
      );
      find(req, res);
    })
    .catch(error => res.status(500).send(error));
}

function remove(req: Request, res: Response) {
  const { id } = req.params;
  jsonfile
    .readFile(file)
    .then(dataSource => {
      jsonfile.writeFileSync(
        file,
        dataSource.filter((item: RestData) => item.id !== decodeURIComponent(id)),
        { spaces: 2 },
      );
      res.end();
    })
    .catch(error => res.status(500).send(error));
}

export default {
  'GET /api/rest': find,
  'POST /api/rest': save,
  'PUT /api/rest/:id': update,
  'DELETE /api/rest/:id': remove,
};
