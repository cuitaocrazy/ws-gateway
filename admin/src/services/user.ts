import { request } from 'umi';

export async function queryCurrent() {
  return request<API.CurrentUser>('/admin/apis/ui');
}

export async function fetchLogout() {
  return request('/admin/apis/logout', {
    method: 'POST',
  });
}
