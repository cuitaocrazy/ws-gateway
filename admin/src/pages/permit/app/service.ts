import request from '@/utils/request';

export async function queryList() {
  return request('/api/app');
}
