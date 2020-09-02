import { request } from 'umi';
import { OrgData } from './data';

export async function getOrgTree(idPrefix: string = '') {
  return request(`/admin/apis/org?id_prefix=${encodeURIComponent(idPrefix)}`);
}

export async function existOrgId(orgId: string) {
  return request(`/admin/apis/org/${encodeURIComponent(orgId)}/exist`);
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
