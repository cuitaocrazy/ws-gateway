import React from 'react';
import { Dispatch } from 'redux';
import { Dropdown, Menu } from 'antd';
import { connect } from 'dva';
import { AppData } from './data';
import AppForm from './AppForm';
import RoleForm from './RoleForm';
import { RoleData } from '../org/data';

interface TreeMenuProps {
  dispatch: Dispatch<any>;
  app: AppData;
  role?: RoleData;
}

const TreeMenu: React.FC<TreeMenuProps> = props => {
  const { dispatch, app, role } = props;

  const [isUpdateApp, setIsUpdateApp] = React.useState<boolean>(false);
  const [isCreateRole, setIsCreateRole] = React.useState<boolean>(false);
  const [isUpdateRole, setIsUpdateRole] = React.useState<boolean>(false);

  const handleUpdateApp = (app: AppData) => {
    dispatch({
      type: 'app/fetchCreateOrUpdateApp',
      payload: app,
    });
  }

  const handleRemoveApp = () => {
    dispatch({
      type: 'app/fetchDeleteApp',
      payload: app.id,
    });
  }

  const handleRemoveRole = (role: RoleData) => {
    dispatch({
      type: 'app/fetchCreateOrUpdateApp',
      payload: {
        ...app,
        roles: app.roles.filter(r => r.name !== role.name),
      },
    });
  }

  return (
    <Dropdown trigger={['contextMenu']} overlay={() => {
      if (role) {
        return (
          <Menu>
            <Menu.Item onClick={(e) => {
              e.domEvent.stopPropagation();
              setIsUpdateRole(true);
            }}>
              修改角色
            </Menu.Item>
            <Menu.Item onClick={(e) => {
              e.domEvent.stopPropagation();
              handleRemoveRole(role);
            }}>
              删除角色
            </Menu.Item>
          </Menu>
        )
      }
      return (
        <Menu>
          <Menu.Item onClick={(e) => { e.domEvent.stopPropagation(); setIsUpdateApp(true); }}>修改应用</Menu.Item>
          <Menu.Item onClick={(e) => { e.domEvent.stopPropagation(); handleRemoveApp(); }}>删除应用</Menu.Item>
          <Menu.Item onClick={(e) => { e.domEvent.stopPropagation(); setIsCreateRole(true); }}>添加角色</Menu.Item>
        </Menu>
      )
    }} >
      <span>
        {role ? role.name : app.id}
        <div onClick={(e) => e.stopPropagation()}>
          <AppForm title="修改应用" visible={isUpdateApp} onCancel={() => setIsUpdateApp(false)}
            info={app} onSubmit={handleUpdateApp} />
          <RoleForm title="添加角色" visible={isCreateRole} onCancel={() => setIsCreateRole(false)}
            app={app} info={{ resources: [] }} onSubmit={handleUpdateApp} />
          <RoleForm title="修改角色" visible={isUpdateRole} onCancel={() => setIsUpdateRole(false)}
            app={app} info={role || { resources: [] }} onSubmit={handleUpdateApp} />
        </div>
      </span>
    </Dropdown>
  );
}

export default connect(
  ({
    loading,
  }: {
    loading: { models: { [key: string]: boolean } };
  }) => ({
    loading: loading.models.app,
  }),
)(TreeMenu);
