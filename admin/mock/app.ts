import path from 'path';
import jsonfile from 'jsonfile';
import { Request, Response } from 'express';
import { AppData } from '@/pages/permit/app/data';

const file = path.resolve('mock/data/app.json');

function find(req: Request, res: Response) {
  jsonfile
    .readFile(file)
    .then(data => res.json(data))
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
        dataSource.map((item: AppData) => (item.id === decodeURIComponent(id) ? body : item)),
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
        dataSource.filter((item: AppData) => item.id !== decodeURIComponent(id)),
        { spaces: 2 },
      );
      res.end();
    })
    .catch(error => res.status(500).send(error));
}

export default {
  'GET /api/app': find,
  'POST /api/app': save,
  'PUT /api/app/:id': update,
  'DELETE /api/app/:id': remove,
};
