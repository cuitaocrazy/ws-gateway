import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { Card, Button, Collapse, notification } from 'antd';
import { RoleData, SvcData, KeyData } from '../data';
import { ModelState } from '../model';
import ErrorRes from './ErrorRes';
import ResOps from './ResOps';
import { getExistRes } from '../utils';

const { Panel } = Collapse;

interface RoleResProps {
  dispatch: Dispatch<any>;
  id: string;
  roles: RoleData[];
  svcs: SvcData[];
  keys: KeyData[];
  loading: boolean;
}

const RoleRes: React.FC<RoleResProps> = props => {
  const { dispatch, roles, id, svcs, keys } = props;

  const [roleSvcs, setRoleSvcs] = React.useState<SvcData[]>([]);

  React.useEffect(() => {
    dispatch({ type: 'role/fetchSvcs' });
  }, []);

  React.useEffect(() => {
    setRoleSvcs(roles.filter(role => role.id === id)[0].svcs);
  }, [id]);

  const handleChange = (activeKey: string | string[]) => {
    dispatch({
      type: 'role/setKeys',
      payload: {
        id,
        activeKey,
      },
    });
  }

  const handleSubmit = () => {
    if (id === 'default') {
      dispatch({
        type: 'role/fetchUpdateDefaultRole',
        payload: getExistRes(roleSvcs, svcs),
        callback: () => {
          notification.success({
            message: '更新操作成功',
            description: `角色【${id}】的资源已更新成功!`,
          });
        },
      });
    } else {
      dispatch({
        type: 'role/fetchCreateOrUpdateRole',
        payload: {
          id,
          svcs: getExistRes(roleSvcs, svcs),
        },
        callback: () => {
          notification.success({
            message: '更新操作成功',
            description: `角色【${id}】的资源已更新成功!`,
          });
        },
      });
    }
  }

  return (
    <Card title={`角色【${id}】资源管理`} extra={<Button type="link" onClick={handleSubmit}>更新</Button>}>
      <ErrorRes role={roles.filter(role => role.id === id)[0]} svcs={svcs} />
      <Collapse key={id} onChange={handleChange}
        activeKey={keys.filter(key => key.id === id)[0]?.activeKey}
      >
        {svcs.map(svc => (
          <Panel header={svc.id} key={`${id}#${svc.id}`}>
            {svc.resources?.map(res => (
              <ResOps key={`${id}#${svc.id}#${res.uri}`} svcId={svc.id} uri={res.uri} ops={res.ops} value={roleSvcs} onChange={setRoleSvcs} />
            ))}
          </Panel>
        ))}
      </Collapse>
    </Card>
  )
}

export default connect(
  ({
    role,
    loading,
  }: {
    role: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    roles: role.roles,
    svcs: role.svcs,
    keys: role.keys,
    id: role.id,
    loading: loading.models.role,
  }),
)(RoleRes);