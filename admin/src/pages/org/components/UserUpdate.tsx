import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { Card, Form, Input, TreeSelect, Checkbox, notification } from 'antd';
import { DataNode } from 'antd/lib/tree';
import { OrgTreeData, UserData, RoleData } from '../data';
import { ModelState } from '../model';

interface UserUpdateProps {
  dispatch: Dispatch<any>;
  info: Partial<UserData>;
  orgTree: OrgTreeData[];
  roles: RoleData[];
}

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 4 },
    md: { span: 2 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 20 },
    md: { span: 20 },
  },
};

const UserForm: React.SFC<UserUpdateProps> = props => {

  const [form] = Form.useForm();
  const { dispatch, info, orgTree, roles } = props;

  React.useEffect(() => {
    dispatch({ type: 'org/fetchRoles' });
  }, [info]);

  const makeTree = (orgTree: OrgTreeData[]): DataNode[] => orgTree.map(orgTree => ({
    key: orgTree.org.id,
    title: orgTree.org.name,
    children: makeTree(orgTree.children || []),
  }));

  const handleSubmit = (values: UserData) => {
    dispatch({
      type: 'org/fetchCreateOrUpdateUser',
      payload: {
        ...info,
        ...values,
      },
      callback: () => {
        if (info.orgId !== values.orgId)
          dispatch({
            type: 'org/fetchUserByOrgId',
            payload: info.orgId,
          });
        notification.success({
          message: '更新用户操作成功',
          description: `用户【${info.id}】已更新成功!`,
        });
      }
    });
  }

  return (
    < Card title="用户更新"
      extra={< a href="#" onClick={() => form.submit()} >保存</a>}>
      <Form {...formItemLayout} form={form} initialValues={info} onFinish={handleSubmit}>
        <Form.Item label="用户">
          {info.id}
        </Form.Item>
        <Form.Item label="机构" name='orgId'
          rules={[
            {
              required: true,
              message: '请选择机构',
            },
          ]}
        >
          <TreeSelect
            dropdownStyle={{
              maxHeight: 400,
              overflow: 'auto',
            }}
            placeholder="请选择"
            treeDefaultExpandAll
            treeData={makeTree(orgTree)}
          />
        </Form.Item>
        <Form.Item label="邮箱" name='email'
          rules={[
            {
              required: true,
              message: '请输入邮箱',
            },
            {
              type: 'email',
              message: '邮箱格式错误',
            },
          ]}
        >
          <Input placeholder="请输入" />
        </Form.Item>
        <Form.Item label="角色" name='roles'>
          <Checkbox.Group options={roles.map(role => role.id)} />
        </Form.Item>
      </Form>
    </Card>
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
    roles: org.roles,
    loading: loading.models.org,
  }),
)(UserForm);
