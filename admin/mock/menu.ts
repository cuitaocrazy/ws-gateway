import path from 'path';
import jsonfile from 'jsonfile';
import { Request, Response } from 'express';
import { MenuData } from '@/pages/permit/menu/data';

const file = path.resolve('mock/data/menu.json');

export const getMenuTree = (node: MenuData, list: MenuData[]): MenuData => {
  const root = { ...node }
  const children = list.filter(item => (item.pId || '') === root.id)
  if (children.length > 0) {
    root.children = []
    children.forEach((item: MenuData) => root.children && root.children.push(getMenuTree(item, list)))
  }
  return root;
}

function find(_: Request, res: Response) {
  jsonfile
    .readFile(file)
    .then(dataSource => {
      res.json(getMenuTree({ id: '', name: '', icon: '', path: '', locale: '', remark: '', pId: '' }, dataSource).children);
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
        dataSource.map((item: MenuData) => (item.id === decodeURIComponent(id) ? body : item)),
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
        dataSource.filter((item: MenuData) => item.id !== decodeURIComponent(id)),
        { spaces: 2 },
      );
      res.end();
    })
    .catch(error => res.status(500).send(error));
}

export default {
  'GET /api/menu': find,
  'POST /api/menu': save,
  'PUT /api/menu/:id': update,
  'DELETE /api/menu/:id': remove,
};
