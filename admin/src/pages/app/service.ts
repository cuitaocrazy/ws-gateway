import request from '@/utils/request';
import { AppData } from './data';

export async function getApps() {
  return request('/admin/apis/app');
}

export async function getSvcs() {
  return request('/admin/apis/svc');
}

export async function existAppId(appId: string) {
  return request(`/admin/apis/app/${encodeURIComponent(appId)}/exist`);
}

export async function createAndUpdataApp(app: AppData) {
  const svcs: string[] = app.resources
    .map(sr => sr.id);
  const uris: string[] = app.resources
    .map(sr => sr.resources
      .map(res => `${sr.id}${res.uri}`)
    )
    .reduce((a: string[], b: string[]) => [...a, ...b], []);;
  const urls: string[] = app.resources
    .map(sr => sr.resources
      .map(res => res.ops
        .map(op => `${op} ${sr.id}${res.uri}`)
      )
      .reduce((a: string[], b: string[]) => [...a, ...b], [])
    )
    .reduce((a: string[], b: string[]) => [...a, ...b], []);

  const data: AppData = {
    ...app,
    roles: app.roles.map(role => ({
      ...role,
      resources: role.resources.filter(sr => svcs.includes(sr.id)).map(sr => ({
        ...sr,
        resources: sr.resources.filter(res => uris.includes(`${sr.id}${res.uri}`)).map(res => ({
          ...res,
          ops: res.ops.filter(op => urls.includes(`${op} ${sr.id}${res.uri}`))
        })),
      })),
    })),
  }
  return request('/admin/apis/app', {
    method: 'PUT', data,
  });
}

export async function deleteApp(appId: string) {
  return request(`/admin/apis/app/${encodeURIComponent(appId)}`, {
    method: 'DELETE',
  });
}
