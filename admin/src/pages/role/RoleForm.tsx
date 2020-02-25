import React from 'react';
import { Modal, Form, Input } from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { RoleData } from './data';
import { existRoleId } from './service';

interface RoleFormProps extends FormComponentProps {
  title: string;
  visible: boolean;
  onCancel(): void;
  onSubmit(value: RoleData): void;
  info: Partial<RoleData>;
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
    info, form, form: { getFieldDecorator }
  } = props;

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSubmit({
          ...info,
          ...values,
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
        <Form.Item {...formItemLayout} label="角色">
          {getFieldDecorator('id', {
            initialValue: info.id,
            rules: [
              {
                required: true,
                message: '请输入角色',
              },
              {
                validator: (_, value) => (value === '' || value === info.id) ? Promise.resolve() : existRoleId(value)
                  .then((result: boolean) => result ? Promise.reject('角色已存在') : Promise.resolve()),
              },
            ],
          })(<Input placeholder="请输入" />)}
        </Form.Item>
      </Form>
    </Modal >
  );
}

export default Form.create<RoleFormProps>()(RoleForm);
