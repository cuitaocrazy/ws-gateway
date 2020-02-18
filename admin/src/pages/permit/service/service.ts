import { stringify } from 'qs';
import request from '@/utils/request';
import { QueryData } from './data';

export async function find(query: Partial<QueryData>) {
  return request(`/api/service?${stringify(query)}`);
}

export async function findRes(id: string) {
  return request(`/api/service/${id}`);
}
