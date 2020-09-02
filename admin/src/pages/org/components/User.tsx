import React from 'react';
import { Dispatch } from 'redux';
import { GridContent } from '@ant-design/pro-layout';
import { UnlockOutlined, CloseOutlined } from '@ant-design/icons';
import { Row, Col, Tooltip, Button, Menu, Empty, Modal, notification } from 'antd';
import { connect } from 'dva';
import { UserData, KeyData } from '../data';
import { ModelState } from '../model';
import UserUpdate from './UserUpdate';

import styles from '../style.less';

const { Item } = Menu;
const { confirm } = Modal;

interface UserProps {
  dispatch: Dispatch<any>;
  orgId: string;
  users: UserData[];
  keys: KeyData[];
  loading: boolean;
}

const User: React.FC<UserProps> = props => {
  const { dispatch, orgId, keys, users } = props;

  React.useEffect(() => {
    dispatch({
      type: 'org/fetchUserByOrgId',
      payload: orgId,
    });
  }, [orgId]);

  const onSelectKey = (key: string) => {
    dispatch({
      type: 'org/setKeys',
      payload: {
        orgId,
        userId: key,
      },
    });
  }

  const handleResetUserPwd = (user: UserData) => {
    confirm({
      title: `确定要重置【${user.id}】用户密码?`,
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        dispatch({
          type: 'org/fetchResetUserPwd',
          payload: user,
          callback: () => {
            notification.success({
              message: '重置密码操作成功',
              description: `用户【${user.id}】已重置密码成功!`,
            });
          },
        });
      },
    });
  }

  const handleRemoveUser = (user: UserData) => {
    confirm({
      title: `确定要删除【${user.id}】用户?`,
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        dispatch({
          type: 'org/fetchDeleteUser',
          payload: user,
          callback: () => {
            notification.success({
              message: '删除用户操作成功',
              description: `用户【${user.id}】已删除成功!`,
            });
          },
        });
      },
    });
  }

  const userId = keys.filter(key => key.orgId === orgId)[0]?.userId;

  return users.length > 0 ? (
    <GridContent>
      <div className={styles.main} >
        <div className={styles.leftMenu}>
          <Menu mode="inline"
            selectedKeys={[userId]}
            onClick={({ key }) => onSelectKey(key + '')}
          >
            {users.map(user => (
              <Item key={user.id}>
                <Row>
                  <Col span={16}>{user.id}</Col>
                  <Col span={4}>
                    <Tooltip title="重置密码">
                      <Button type="link" shape="circle" size="small"
                        onClick={e => {
                          e.stopPropagation();
                          handleResetUserPwd(user);
                        }} >
                        <UnlockOutlined style={{ marginRight: 0 }} />
                      </Button>
                    </Tooltip>
                  </Col>
                  <Col span={4}>
                    <Tooltip title="删除用户">
                      <Button type="link" shape="circle" size="small"
                        onClick={e => {
                          e.stopPropagation();
                          handleRemoveUser(user);
                        }} >
                        <CloseOutlined style={{ marginRight: 0 }} />
                      </Button>
                    </Tooltip>
                  </Col>
                </Row>
              </Item>
            ))}
          </Menu>
        </div>
        <div className={styles.right}>
          <div className={styles.title}>
            {users.filter(user => (user.id === userId && user.orgId === orgId)).length > 0 ?
              (<UserUpdate info={users.filter(user => (user.id === userId && user.orgId === orgId))[0]} />) : (<Empty />)
            }
          </div>
        </div>
      </div>
    </GridContent>
  ) : (<Empty />);
}

export default connect(
  ({
    org,
    loading,
  }: {
    org: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    users: org.users,
    orgId: org.orgId,
    keys: org.keys,
    loading: loading.models.org,
  }),
)(User);
