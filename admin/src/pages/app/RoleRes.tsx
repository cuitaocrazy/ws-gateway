import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { Card, Collapse } from 'antd';
import { SvcResData, SvcData, KeyData } from './data';
import { ModelState } from './model';
import { AppData } from './data';
import ResOps from './ResOps';

const { Panel } = Collapse;

interface RoleResProps {
  dispatch: Dispatch<any>;
  app: AppData;
  appId: string;
  roleId: string;
  svcs: SvcData[];
  keys: KeyData[];
  loading: boolean;
}

const RoleRes: React.FC<RoleResProps> = props => {
  const { dispatch, app, appId, roleId, svcs, keys } = props;

  const [roleRes, setRoleRes] = React.useState<SvcResData[]>([]);

  React.useEffect(() => {
    dispatch({ type: 'app/fetchSvcs' });
  }, []);

  React.useEffect(() => {
    setRoleRes(app.roles.filter(role => role.name === roleId)[0].resources);
  }, [app, roleId]);

  const handleChange = (activeKey: string | string[]) => {
    dispatch({
      type: 'app/setKeys',
      payload: {
        appId,
        roleId,
        activeKey,
      },
    });
  }

  const handleSubmit = () => {
    dispatch({
      type: 'app/fetchCreateOrUpdateApp',
      payload: {
        ...app,
        roles: [
          ...app.roles.filter(role => role.name !== roleId),
          {
            name: roleId,
            resources: roleRes,
          },
        ],
      },
    });
  }

  return (
    <Card title={`资源管理【${app.id}】->【${roleId}】`} extra={<a href="#" onClick={handleSubmit}>保存</a>}>
      <Collapse key={`${app.id}#${roleId}`} onChange={handleChange}
        activeKey={keys.filter(key => (key.appId === appId && key.roleId === roleId))[0]?.activeKey}
      >
        {svcs.filter(svc => app.resources.map(svcRes => svcRes.id).includes(svc.id))
          .map(svc => (
            <Panel header={svc.id} key={`${app.id}#${roleId}#${svc.id}`}>
              {app.resources.filter(svcRes => svcRes.id === svc.id)[0]?.resources?.map(res => (
                <ResOps key={`${app.id}#${roleId}#${svc.id}#${res.uri}`} svcId={svc.id} uri={res.uri} ops={res.ops} value={roleRes} onChange={setRoleRes} />
              ))}
            </Panel>
          ))}
      </Collapse>
    </Card>
  )
}

export default connect(
  ({
    app,
    loading,
  }: {
    app: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    svcs: app.svcs,
    keys: app.keys,
    appId: app.appId,
    roleId: app.roleId,
    loading: loading.models.app,
  }),
)(RoleRes);