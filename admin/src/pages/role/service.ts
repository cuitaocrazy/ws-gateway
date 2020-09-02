import { request } from 'umi';
import { RoleData, SvcData } from './data';

export async function getRoles() {
  return request('/admin/apis/role');
}

export async function getSvcs() {
  return request('/admin/apis/svc');
}

export async function existRoleId(id: string) {
  return request(`/admin/apis/role/${encodeURIComponent(id)}/exist`);
}

export async function createAndUpdataRole(role: RoleData) {
  return request('/admin/apis/role', {
    method: 'PUT',
    data: role,
  });
}

export async function deleteRole(id: string) {
  return request(`/admin/apis/role/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  });
}

export async function getDefaultRole() {
  return request('/admin/apis/default_role');
}

export async function putDefaultRole(defaultRoleSvcRes: SvcData[]) {
  return request('/admin/apis/default_role', {
    method: 'PUT',
    data: defaultRoleSvcRes,
  });
}
