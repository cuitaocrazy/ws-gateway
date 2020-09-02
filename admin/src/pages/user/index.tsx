import React from 'react';
import { Dispatch } from 'redux';
import { PageContainer } from '@ant-design/pro-layout';
import { Divider, Button, Modal, notification, Table, Card } from 'antd';
import { connect } from 'dva';
import { UserData, OrgTreeData, RoleData } from './data';
import { ModelState } from './model';
import Search from './components/Search';
import Form from './components/Form';

const { confirm } = Modal;

interface UserProps {
  dispatch: Dispatch<any>;
  users: UserData[];
  orgTree: OrgTreeData[],
  roles: RoleData[],
  loading: boolean;
}

const User: React.FC<UserProps> = props => {
  const { dispatch, orgTree, roles, users, loading } = props;

  const [info, setInfo] = React.useState<Partial<UserData>>({});
  const [isCreate, setIsCreate] = React.useState<boolean>(false);
  const [isUpdate, setIsUpdate] = React.useState<boolean>(false);

  React.useEffect(() => {
    dispatch({ type: 'user/fetchOrgTree' });
    dispatch({ type: 'user/fetchRoles' });
    dispatch({
      type: 'user/fetchUserByOrgId',
      payload: '00',
    });
  }, []);

  const handleQueryUser = (params: { orgId: string }) => {
    dispatch({ type: 'user/fetchOrgTree' });
    dispatch({ type: 'user/fetchRoles' });
    dispatch({
      type: 'user/fetchUserByOrgId',
      payload: params.orgId,
    });
  }

  const handleCreateUser = (user: UserData) => {
    dispatch({
      type: 'user/fetchCreateOrUpdateUser',
      payload: user,
      callback: () => {
        notification.success({
          message: '添加用户操作成功',
          description: `用户【${user.id}】已添加成功!`,
        });
      },
    });
  }

  const handleUpdateShow = (record: UserData) => {
    setInfo(record);
    setIsUpdate(true);
  }

  const handleUpdateUser = (values: UserData) => {
    dispatch({
      type: 'user/fetchCreateOrUpdateUser',
      payload: values,
      callback: () => {
        notification.success({
          message: '更新用户操作成功',
          description: `用户【${values.id}】已更新成功!`,
        });
      }
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
          type: 'user/fetchResetUserPwd',
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
          type: 'user/fetchDeleteUser',
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

  const columns = [
    {
      title: '机构',
      dataIndex: 'orgId',
    },
    {
      title: '用户名',
      dataIndex: 'id',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
    },
    {
      title: '角色',
      dataIndex: 'roles',
      render: (value: string[]) => value.join(', ')
    },
    {
      title: '操作',
      dataIndex: 'id',
      render: (_: string, record: UserData) => (
        <>
          <a onClick={() => handleUpdateShow(record)}>
            编辑
          </a>
          <Divider type="vertical" />
          <a onClick={() => handleRemoveUser(record)}>
            删除
          </a>
          <Divider type="vertical" />
          <a onClick={() => handleResetUserPwd(record)}>
            重置密码
          </a>
        </>
      ),
    },
  ];

  return (
    <PageContainer extra={<Button type="link" onClick={() => setIsCreate(true)}>新增</Button>}>
      <Card >
        <Search orgId="00" orgTree={orgTree} onSubmit={handleQueryUser} />
        <Table<UserData>
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={users}
        />
        <Form title="用户新增" visible={isCreate}
          onCancel={() => setIsCreate(false)} onSubmit={handleCreateUser}
          info={{}} orgTree={orgTree} roles={roles} />
        <Form title="用户编辑" visible={isUpdate}
          onCancel={() => setIsUpdate(false)} onSubmit={handleUpdateUser}
          info={info} orgTree={orgTree} roles={roles} />
      </Card>
    </PageContainer>
  );
}

export default connect(
  ({
    user,
    loading,
  }: {
    user: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    users: user.users,
    orgTree: user.orgTree,
    roles: user.roles,
    loading: loading.models.user,
  }),
)(User);
