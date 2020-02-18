import React from 'react';
import { Dispatch } from 'redux';
import { GridContent } from '@ant-design/pro-layout';
import { Row, Col, Button, Menu, Empty, Icon } from 'antd';
import { connect } from 'dva';
import { UserData, KeyData } from './data';
import { ModelState } from './model';
import UserUpdate from './UserUpdate';

import styles from './style.less';

const { Item } = Menu;

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

  const handleRemoveUser = (user: UserData) => {
    dispatch({
      type: 'org/fetchDeleteUser',
      payload: user,
    });
  }

  const userId = keys.filter(key => key.orgId === orgId)[0]?.userId;

  return users.length > 0 ? (
    <GridContent>
      <div className={styles.main} >
        <div className={styles.leftMenu}>
          <Menu mode="inline"
            selectedKeys={[userId]}
            onClick={({ key }) => onSelectKey(key)}
          >
            {users.map(user => (
              <Item key={user.id}>
                <Row>
                  <Col span={20}>{user.id}</Col>
                  <Col span={4}>
                    <Button shape="circle" size="small"
                      onClick={e => {
                        e.stopPropagation();
                        handleRemoveUser(user);
                      }} >
                      <Icon type="close" style={{ marginRight: 0 }} />
                    </Button>
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
