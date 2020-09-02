import { request } from 'umi';
import { LoginParamsType } from './data.d';

export async function fetchLogin(params: LoginParamsType) {
  return request('/admin/login', {
    method: 'POST',
    data: params,
  });
}
