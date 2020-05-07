import React from 'react';
import { Dispatch } from 'redux';
import { PageHeaderWrapper, GridContent } from '@ant-design/pro-layout';
import { Spin, Button, Menu, Card, Empty } from 'antd';
import { connect } from 'dva';
import { RoleData, KeyData } from './data';
import { ModelState } from './model';
import RightMenu from './RightMenu';
import RoleRes from './RoleRes';
import RoleForm from './RoleForm';

import styles from './style.less';

interface RoleProps {
  dispatch: Dispatch<any>;
  roles: RoleData[];
  keys: KeyData[];
  id: string;
  loading: boolean;
}

const RoleView: React.FC<RoleProps> = props => {
  const { dispatch, roles, id, loading } = props;

  const [isCreateRole, setIsCreateRole] = React.useState<boolean>(false);

  React.useEffect(() => {
    dispatch({ type: 'role/fetchRoles' });
  }, []);

  const handleSelect = (selectedKey: string) => {
    dispatch({
      type: 'role/setId',
      payload: selectedKey,
    });
  }

  const handleCreateRole = (role: RoleData) => {
    dispatch({
      type: 'role/fetchCreateOrUpdateRole',
      payload: role,
    });
  }

  const renderRes = () => {
    if (roles.map(role => role.id).includes(id)) {
      return (
        <RoleRes />
      )
    }
    return (<Card><Empty /></Card>)
  }

  return (
    <PageHeaderWrapper extra={<Button type="link" onClick={() => setIsCreateRole(true)}>新增</Button>}>
      <Spin spinning={loading}>
        <GridContent>
          <div className={styles.main} >
            <div className={styles.leftMenu}>
              <Menu
                mode="inline"
                selectedKeys={[id]}
                onClick={({ key }) => handleSelect(key)}
              >
                {roles.map(role => (
                  <Menu.Item key={role.id} >
                    <RightMenu role={role} />
                  </Menu.Item>
                ))}
              </Menu>
              {/* <Tree
                onSelect={handleSelect}
                selectedKeys={[id]}
              >
                {roles.map(role => (
                  <TreeNode
                    key={role.id}
                    title={<TreeMenu role={role} />}
                  />
                ))}
              </Tree> */}
            </div>
            <div className={styles.right}>
              <div className={styles.title}>{renderRes()}</div>
            </div>
          </div>
        </GridContent>
      </Spin>
      <RoleForm title="添加角色" visible={isCreateRole} onCancel={() => setIsCreateRole(false)}
        info={{ svcs: [] }} onSubmit={handleCreateRole} />
    </PageHeaderWrapper>
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
    id: role.id,
    keys: role.keys,
    loading: loading.models.role,
  }),
)(RoleView);
