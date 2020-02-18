import { stringify } from 'qs';
import request from '@/utils/request';
import { MethodData, QueryData } from './data';

export async function find(query: Partial<QueryData>) {
  return request(`/api/method?${stringify(query)}`);
}

export async function save(method: MethodData) {
  return request('/api/method', {
    method: 'POST',
    data: method,
  });
}

export async function update(id: string, method: MethodData) {
  return request(`/api/method/${id}`, {
    method: 'PUT',
    data: method,
  });
}

export async function remove(id: string) {
  return request(`/api/method/${id}`, {
    method: 'DELETE',
  });
}
