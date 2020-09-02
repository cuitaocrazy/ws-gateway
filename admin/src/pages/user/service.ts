import { request } from 'umi';
import { UserData } from './data';

export async function getOrgTree(idPrefix: string = '') {
  return request(`/admin/apis/org?id_prefix=${encodeURIComponent(idPrefix)}`);
}

export async function getUserByOrgId(orgId: string) {
  return request(`/admin/apis/user?org_id=${encodeURIComponent(orgId)}`);
}

export async function getRoles() {
  return request('/admin/apis/role');
}

export async function existUserId(userId: string) {
  return request(`/admin/apis/user/${encodeURIComponent(userId)}/exist`);
}

export async function createAndUpdataUser(user: UserData) {
  return request('/admin/apis/user', {
    method: 'PUT',
    data: user,
  });
}

export async function deleteUser(userId: string) {
  return request(`/admin/apis/user/${encodeURIComponent(userId)}`, {
    method: 'DELETE',
  });
}

export async function resetUserPwd(userId: string) {
  return request(`/admin/apis/user/${userId}/reset_pwd`, {
    method: 'PUT',
  });
}
