import React from 'react';
import { Dispatch } from 'redux';
import { Dropdown, Menu } from 'antd';
import { connect } from 'dva';
import { OrgTreeData, OrgData, UserData } from './data';
import { ModelState } from './model';
import OrgForm from './OrgForm';
import UserForm from './UserForm';

interface TreeMenuProps {
  dispatch: Dispatch<any>;
  orgTree: OrgTreeData[];
  node: OrgTreeData;
}

const TreeMenu: React.FC<TreeMenuProps> = props => {
  const { dispatch, orgTree, node } = props;

  const [isCreateOrg, setIsCreateOrg] = React.useState<boolean>(false);
  const [isUpdateOrg, setIsUpdateOrg] = React.useState<boolean>(false);
  const [isCreateUser, setIsCreateUser] = React.useState<boolean>(false);

  const handleCreateOrg = (org: OrgData) => {
    dispatch({
      type: 'org/fetchCreateOrUpdateOrg',
      payload: org,
    });
  }

  const handleUpdateOrg = (org: OrgData) => {
    dispatch({
      type: 'org/fetchCreateOrUpdateOrg',
      payload: org,
    });
  }

  const handleRemoveOrg = (orgId: string) => {
    dispatch({
      type: 'org/fetchDeleteOrg',
      payload: orgId,
    });
  }

  const handleCreateUser = (user: UserData) => {
    dispatch({
      type: 'org/fetchCreateOrUpdateUser',
      payload: user,
    });
  }

  return (
    <Dropdown trigger={['contextMenu']} overlay={() => (
      <Menu>
        <Menu.Item onClick={(e) => {
          e.domEvent.stopPropagation();
          setIsUpdateOrg(true);
        }}>
          修改机构
        </Menu.Item>
        {(node.children && node.children.length > 0) ? '' : (
          <Menu.Item onClick={(e) => {
            e.domEvent.stopPropagation();
            handleRemoveOrg(node.org.id);
          }}>
            删除机构
          </Menu.Item>
        )}
        <Menu.Item onClick={(e) => {
          e.domEvent.stopPropagation();
          setIsCreateOrg(true);
        }}>
          添加机构
        </Menu.Item>
        <Menu.Item onClick={(e) => {
          e.domEvent.stopPropagation();
          setIsCreateUser(true);
        }}>
          添加用户
        </Menu.Item>
      </Menu>
    )} >
      <span>
        {node.org.name}
        <div onClick={(e) => e.stopPropagation()}>
          <OrgForm title="修改机构" visible={isUpdateOrg} onCancel={() => setIsUpdateOrg(false)}
            info={node.org} onSubmit={handleUpdateOrg} />
          <OrgForm title="添加机构" visible={isCreateOrg} onCancel={() => setIsCreateOrg(false)}
            info={node.org} onSubmit={handleCreateOrg} />
          <UserForm title="添加用户" visible={isCreateUser} onCancel={() => setIsCreateUser(false)}
            orgTree={orgTree} info={{ orgId: node.org.id, roles: [] }} onSubmit={handleCreateUser} />
        </div>
      </span>
    </Dropdown>
  );
}

export default connect(
  ({
    org,
    loading,
  }: {
    org: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    orgTree: org.orgTree,
    loading: loading.models.org,
  }),
)(TreeMenu);
