import request from '@/utils/request';
import { OrgData, UserData } from './data';

export async function getOrgTree(idPrefix: string = '') {
  return request(`/admin/apis/org?id_prefix=${encodeURIComponent(idPrefix)}`);
}

export async function getUserByOrgId(orgId: string) {
  return request(`/admin/apis/user?org_id=${encodeURIComponent(orgId)}`);
}

export async function getApps() {
  return request('/admin/apis/app');
}

export async function existOrgId(orgId: string) {
  return request(`/admin/apis/org/${encodeURIComponent(orgId)}/exist`);
}

export async function existUserId(userId: string) {
  return request(`/admin/apis/user/${encodeURIComponent(userId)}/exist`);
}

export async function createAndUpdataOrg(org: OrgData) {
  return request('/admin/apis/org', {
    method: 'PUT',
    data: org,
  });
}

export async function deleteOrg(orgId: string) {
  return request(`/admin/apis/org/${encodeURIComponent(orgId)}`, {
    method: 'DELETE',
  });
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
