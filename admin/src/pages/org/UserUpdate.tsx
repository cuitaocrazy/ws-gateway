import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { Card, Form, Input, TreeSelect, Checkbox } from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { TreeNode } from 'antd/es/tree-select';
import { OrgTreeData, UserData, RoleData } from './data';
import { ModelState } from './model';

interface UserUpdateProps extends FormComponentProps {
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
  const { dispatch, info, orgTree, roles, form, form: { getFieldDecorator } } = props;

  React.useEffect(() => {
    dispatch({ type: 'org/fetchRoles' });
  }, [info]);

  const makeTree = (orgTree: OrgTreeData[]): TreeNode[] => orgTree.map(orgTree => ({
    value: orgTree.org.id,
    title: orgTree.org.name,
    children: makeTree(orgTree.children || []),
  }));

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
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
          }
        });
      }
    });
  }

  return (
    < Card title="用户更新"
      extra={< a href="#" onClick={handleSubmit} >保存</a>}>
      <Form onSubmit={handleSubmit}>
        <Form.Item {...formItemLayout} label="用户">
          <Input value={info.id} readOnly />
        </Form.Item>
        <Form.Item {...formItemLayout} label="机构">
          {getFieldDecorator('orgId', {
            initialValue: info.orgId,
            rules: [
              {
                required: true,
                message: '请选择机构',
              },
            ],
          })(
            <TreeSelect
              dropdownStyle={{
                maxHeight: 400,
                overflow: 'auto',
              }}
              placeholder="请选择"
              treeDefaultExpandAll
              treeData={makeTree(orgTree)}
            />
          )}
        </Form.Item>
        <Form.Item {...formItemLayout} label="角色">
          {getFieldDecorator('roles', {
            initialValue: info.roles,
          })(
            <Checkbox.Group options={roles.map(role => role.id)} />
          )}
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
)(Form.create<UserUpdateProps>()(UserForm));
