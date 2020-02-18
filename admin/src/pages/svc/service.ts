import request from '@/utils/request';

export async function getSvcs() {
  return request('/admin/apis/svc');
}
