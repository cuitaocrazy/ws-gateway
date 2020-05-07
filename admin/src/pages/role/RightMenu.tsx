import React from 'react';
import { Dispatch } from 'redux';
import { Row, Col, Button, Icon, Modal, notification } from 'antd';
import { connect } from 'dva';
import RoleForm from './RoleForm';
import { RoleData } from './data';

const { confirm } = Modal;

interface RightMenuProps {
  dispatch: Dispatch<any>;
  role: RoleData;
}

const RightMenu: React.FC<RightMenuProps> = props => {
  const { dispatch, role } = props;

  const [isUpdateRole, setIsUpdateRole] = React.useState<boolean>(false);

  const handleUpdateRole = (role: RoleData) => {
    dispatch({
      type: 'role/fetchCreateOrUpdateRole',
      payload: role,
      callback: () => {
        notification.success({
          message: '修改操作成功',
          description: `角色【${role.id}】已修改成功!`,
        });
      },
    });
  }

  const handleRemoveRole = (id: string) => {
    confirm({
      title: `确定要删除【${id}】角色?`,
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        dispatch({
          type: 'role/fetchDeleteRole',
          payload: id,
          callback: () => {
            notification.success({
              message: '删除操作成功',
              description: `角色【${role.id}】已删除成功!`,
            });
          },
        });
      },
    });
  }

  return role.id === 'default' ? (<>default</>) : (
    <Row>
      <Col span={16}>{role.id}</Col>
      <Col span={4}>
        <Button type="link" shape="circle" size="small"
          onClick={e => {
            e.stopPropagation();
            setIsUpdateRole(true);
          }} >
          <Icon type="edit" style={{ marginRight: 0 }} />
        </Button>
      </Col>
      <Col span={4}>
        <Button type="link" shape="circle" size="small"
          onClick={e => {
            e.stopPropagation();
            handleRemoveRole(role.id);
          }} >
          <Icon type="close" style={{ marginRight: 0 }} />
        </Button>
      </Col>
      <RoleForm title="修改角色" visible={isUpdateRole} onCancel={() => setIsUpdateRole(false)}
        info={role || { svcs: [] }} onSubmit={handleUpdateRole} />
    </Row>
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
)(RightMenu);
