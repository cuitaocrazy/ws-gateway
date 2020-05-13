import { SvcData, ResData, Operator } from './data';

interface UriType {
  svcId: string;
  uri: string;
  ops: Operator[];
}

interface OpsType {
  svcId: string;
  uri: string;
  ops: Operator;
}

// 将资源转换为平坦数据
const svcToOps = (svcs: SvcData[]) => svcs
  .reduce((uris: UriType[], svc: SvcData) => ([
    ...uris,
    ...svc.resources.map((res: ResData) => ({
      svcId: svc.id,
      uri: res.uri,
      ops: res.ops,
    })),
  ]), [])
  .reduce((opss: OpsType[], uri: UriType) => ([
    ...opss,
    ...uri.ops.map((ops: Operator) => ({
      svcId: uri.svcId,
      uri: uri.uri,
      ops,
    }))
  ]), []);

// 将平坦数据转换为对象
const opsToSvc = (opss: OpsType[]) => opss
  .map((ops: OpsType) => ops.svcId)
  .reduce((a: string[], b: string) => a.includes(b) ? a : [...a, b], [])
  .map(svcId => ({
    id: svcId,
    resources: opss
      .filter((ops: OpsType) => svcId === ops.svcId)
      .map((ops: OpsType) => ops.uri)
      .reduce((a: string[], b: string) => a.includes(b) ? a : [...a, b], [])
      .map((uri: string) => ({
        uri,
        ops: opss
          .filter((ops: OpsType) => (svcId === ops.svcId && uri === ops.uri))
          .map((ops: OpsType) => ops.ops)
      }))
  }));

export function getErrorRes(roleSvcs: SvcData[], svcs: SvcData[]): SvcData[] {
  // 将服务资源转换为平坦数据
  const svcOps: OpsType[] = svcToOps(svcs);
  // 将角色资源转换为平坦数据
  const roleOps: OpsType[] = svcToOps(roleSvcs);
  // 筛选出错误的资源平坦数据
  const errorOps: OpsType[] = roleOps.filter((ops: OpsType) =>
    !svcOps.map(ops => ops.svcId + ops.uri + ops.ops)
      .includes(ops.svcId + ops.uri + ops.ops)
  );
  // 将平坦数据转换为对象
  return opsToSvc(errorOps);
}

export function getExistRes(roleSvcs: SvcData[], svcs: SvcData[]): SvcData[] {
  // 将服务资源转换为平坦数据
  const svcOps: OpsType[] = svcToOps(svcs);
  // 将角色资源转换为平坦数据
  const roleOps: OpsType[] = svcToOps(roleSvcs);
  // 筛选出存在的资源平坦数据
  const existOps: OpsType[] = roleOps.filter((ops: OpsType) =>
    svcOps.map(ops => ops.svcId + ops.uri + ops.ops)
      .includes(ops.svcId + ops.uri + ops.ops)
  );
  // 将平坦数据转换为对象
  return opsToSvc(existOps);
}