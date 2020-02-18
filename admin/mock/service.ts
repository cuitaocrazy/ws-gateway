import { Request, Response } from 'express';
import { ServiceData } from '@/pages/permit/service/data';

const data = [
  {
    id: 'ser-1',
    md5: 'abcdefghqmnopqrstuvw',
    res: [
      '/merchant',
      '/terminal',
      '/trans',
      '/manager',
    ],
  },
  {
    id: 'ser-2',
    md5: 'abcdefghqmnopqrstuvw',
    res: [
      '/merchant',
      '/terminal',
      '/trans',
      '/manager',
    ],
  }
]

export default {
  'GET /api/service': (req: Request, res: Response) => {
    const params = req.query;
    let pageSize = 5;
    if (params.pageSize) {
      pageSize = params.pageSize * 1;
    }
    let current = 1;
    if (params.current) {
      current = params.current * 1;
    }
    const list = data
      .filter((item: ServiceData) => !params.id || params.id === '' || params.id === item.id)
    res.json({
      list: list.slice((current - 1) * pageSize, current * pageSize),
      pagination: {
        total: list.length,
        pageSize,
        current,
      },
    });
  },
}