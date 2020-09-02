import React from 'react';
import { Dispatch } from 'redux';
import { Row, Col, Tooltip, Button, Modal, notification } from 'antd';
import { CopyOutlined, CloseOutlined } from '@ant-design/icons';
import { connect } from 'dva';
import RoleForm from './RoleForm';
import { RoleData } from '../data';

const { confirm } = Modal;

interface RightMenuProps {
  dispatch: Dispatch<any>;
  role: RoleData;
}

const RightMenu: React.FC<RightMenuProps> = props => {
  const { dispatch, role } = props;

  const [isCopyRole, setIsCopyRole] = React.useState<boolean>(false);

  const handleCopyRole = (role: RoleData) => {
    dispatch({
      type: 'role/fetchCreateOrUpdateRole',
      payload: role,
      callback: () => {
        notification.success({
          message: '拷贝操作成功',
          description: `角色【${role.id}】已创建成功!`,
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
        <Tooltip title="拷贝角色">
          <Button type="link" shape="circle" size="small"
            onClick={e => {
              e.stopPropagation();
              setIsCopyRole(true);
            }} >
            <CopyOutlined style={{ marginRight: 0 }} />
          </Button>
        </Tooltip>
      </Col>
      <Col span={4}>
        <Tooltip title="删除角色">
          <Button type="link" shape="circle" size="small"
            onClick={e => {
              e.stopPropagation();
              handleRemoveRole(role.id);
            }} >
            <CloseOutlined style={{ marginRight: 0 }} />
          </Button>
        </Tooltip>
      </Col>
      <RoleForm title={`拷贝【${role.id}】角色`} visible={isCopyRole} onCancel={() => setIsCopyRole(false)}
        info={{ id: '', svcs: role.svcs }} onSubmit={handleCopyRole} />
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
