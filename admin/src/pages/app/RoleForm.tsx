import React from 'react';
import { Modal, Form, Input } from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { AppData, RoleData } from './data';

interface RoleFormProps extends FormComponentProps {
  title: string;
  visible: boolean;
  onCancel(): void;
  onSubmit(value: AppData): void;
  info: Partial<RoleData>;
  app: AppData;
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

const RoleForm: React.SFC<RoleFormProps> = props => {
  const {
    title, visible, onCancel, onSubmit,
    app, info, form, form: { getFieldDecorator }
  } = props;

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSubmit({
          ...app,
          roles: [
            ...app.roles.filter(role => role.name !== values.name),
            {
              ...info,
              ...values,
            },
          ],
        });
        form.resetFields();
        onCancel();
      }
    });
  }

  return (
    <Modal
      maskClosable={false}
      title={title}
      visible={visible}
      onOk={handleSubmit}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
    >
      <Form onSubmit={handleSubmit}>
        <Form.Item {...formItemLayout} label="应用">
          <Input value={app.id} readOnly />
        </Form.Item>
        <Form.Item {...formItemLayout} label="角色">
          {getFieldDecorator('name', {
            initialValue: info.name,
            rules: [
              {
                required: true,
                message: '请输入角色',
              },
              {
                validator: (_, value, callback) => {
                  if (app.roles.filter(role => role.name !== info.name).map(role => role.name).includes(value)) {
                    callback("角色已存在");
                  }
                  callback();
                },
              },
            ],
          })(<Input placeholder="请输入" />)}
        </Form.Item>
      </Form>
    </Modal >
  );
}

export default Form.create<RoleFormProps>()(RoleForm);
