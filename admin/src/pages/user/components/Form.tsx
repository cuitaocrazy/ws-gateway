import React from 'react';
import { Modal, Form, Input, TreeSelect, Checkbox } from 'antd';
import { DataNode } from 'antd/lib/tree';
import { OrgTreeData, RoleData, UserData } from '../data';
import { existUserId } from '../service';

interface UserFormProps {
  title: string;
  visible: boolean;
  onCancel(): void;
  onSubmit(value: UserData): void;
  info: Partial<UserData>;
  orgTree: OrgTreeData[];
  roles: RoleData[];
}

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 7 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 12 },
    md: { span: 10 },
  },
};

const UserForm: React.SFC<UserFormProps> = props => {

  const [form] = Form.useForm();
  const { title, visible, onCancel, onSubmit, info, orgTree, roles } = props;

  React.useEffect(() => {
    form.resetFields();
  }, [info]);

  const makeTree = (orgTree: OrgTreeData[]): DataNode[] => orgTree.map(orgTree => ({
    key: orgTree.org.id,
    value: orgTree.org.id,
    title: orgTree.org.name,
    children: makeTree(orgTree.children || []),
  }));

  const handleSubmit = (values: UserData) => {
    onSubmit({
      ...info,
      ...values,
    });
    form.resetFields();
    onCancel();
  }

  return (
    <Modal
      maskClosable={false}
      getContainer={false}
      title={title}
      visible={visible}
      onOk={() => form.submit()}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
    >
      <Form {...formItemLayout} form={form} initialValues={info} onFinish={handleSubmit}>
        {info.id ?
          (<Form.Item label="用户">
            {info.id}
          </Form.Item>) :
          (<Form.Item label="用户" name="id"
            rules={[
              {
                required: true,
                message: '请输入用户',
              },
              {
                validator: (_, value) => (value === '' || value === info.id) ? Promise.resolve() :
                  existUserId(value).then((result: boolean) => result ? Promise.reject('用户已存在') : Promise.resolve()),
              },
            ]}
          >
            <Input placeholder="请输入" />
          </Form.Item>)
        }
        <Form.Item label="机构" name="orgId"
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
        <Form.Item label="邮箱" name="email"
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
        <Form.Item label="角色" name="roles">
          <Checkbox.Group options={roles.map(role => role.id)} />
        </Form.Item>
      </Form>
    </Modal >
  );
}

export default UserForm;
