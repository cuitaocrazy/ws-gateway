import { stringify } from 'qs';
import request from '@/utils/request';
import { RestData, QueryData } from './data.d';

export async function find(query: Partial<QueryData>) {
  return request(`/api/rest?${stringify(query)}`);
}

export async function save(rest: RestData) {
  return request('/api/rest', {
    method: 'POST',
    data: rest,
  });
}

export async function update(id: string, rest: RestData) {
  return request(`/api/rest/${encodeURIComponent(id)}`, {
    method: 'PUT',
    data: rest,
  });
}

export async function remove(id: string) {
  return request(`/api/rest/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  });
}

export async function getMethod() {
  return request('/api/method?pageSize=100');
}
