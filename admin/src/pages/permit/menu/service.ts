import { stringify } from 'qs';
import request from '@/utils/request';
import { MenuData, QueryData } from './data';

export async function find(query: Partial<QueryData>) {
  return request(`/api/menu?${stringify(query)}`);
}

export async function save(menu: MenuData) {
  return request('/api/menu', {
    method: 'POST',
    data: menu,
  });
}

export async function update(id: string, menu: MenuData) {
  return request(`/api/menu/${id}`, {
    method: 'PUT',
    data: menu,
  });
}

export async function remove(id: string) {
  return request(`/api/menu/${id}`, {
    method: 'DELETE',
  });
}
