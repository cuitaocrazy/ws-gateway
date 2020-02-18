import { stringify } from 'qs';
import request from '@/utils/request';
import { RoleData, QueryData } from './data';

export async function find(query: Partial<QueryData>) {
  return request(`/api/role?${stringify(query)}`);
}

export async function save(role: RoleData) {
  return request('/api/role', {
    method: 'POST',
    data: role,
  });
}

export async function update(id: string, role: RoleData) {
  return request(`/api/role/${id}`, {
    method: 'PUT',
    data: role,
  });
}

export async function remove(id: string) {
  return request(`/api/role/${id}`, {
    method: 'DELETE',
  });
}

export async function getMenu() {
  return request('/api/menu');
}

export async function getRest() {
  return request('/api/rest?pageSize=100');
}

export async function getMethod() {
  return request('/api/method?pageSize=100');
}
