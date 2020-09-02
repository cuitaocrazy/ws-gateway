import React from 'react';
import { Dispatch } from 'redux';
import { Dropdown, Menu } from 'antd';
import { connect } from 'dva';
import RoleForm from './RoleForm';
import { RoleData } from '../data';

interface TreeMenuProps {
  dispatch: Dispatch<any>;
  role: RoleData;
}

const TreeMenu: React.FC<TreeMenuProps> = props => {
  const { dispatch, role } = props;

  const [isUpdateRole, setIsUpdateRole] = React.useState<boolean>(false);

  const handleUpdateRole = (role: RoleData) => {
    dispatch({
      type: 'role/fetchCreateOrUpdateRole',
      payload: role,
    });
  }

  const handleRemoveRole = (id: string) => {
    dispatch({
      type: 'role/fetchDeleteRole',
      payload: id,
    });
  }

  return (
    <Dropdown trigger={['contextMenu']} overlay={() => (
      <Menu>
        <Menu.Item onClick={(e) => {
          e.domEvent.stopPropagation();
          setIsUpdateRole(true);
        }}>
          修改角色
        </Menu.Item>
        <Menu.Item onClick={(e) => {
          e.domEvent.stopPropagation();
          handleRemoveRole(role.id);
        }}>
          删除角色
        </Menu.Item>
      </Menu>
    )} >
      <span>
        {role.id}
        <div onClick={(e) => e.stopPropagation()}>
          <RoleForm title="修改角色" visible={isUpdateRole} onCancel={() => setIsUpdateRole(false)}
            info={role || { svcs: [] }} onSubmit={handleUpdateRole} />
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
    loading: loading.models.role,
  }),
)(TreeMenu);
