import { request } from 'umi';
import { SvcData } from './data';

export async function getSvcs() {
  return request('/admin/apis/svc');
}

export async function getActualSvcIds() {
  return request('/admin/apis/svc/actual_svc_ids');
}

export async function getSvcActualRes(svcId: string) {
  return request(`/admin/apis/svc/${encodeURIComponent(svcId)}/actual_res`);
}

export async function putSvc(svc: SvcData) {
  return request('/admin/apis/svc', {
    method: 'PUT',
    data: svc,
  });
}

export async function deleteSvc(svcId: string) {
  return request(`/admin/apis/svc/${encodeURIComponent(svcId)}`, {
    method: 'DELETE',
  });
}