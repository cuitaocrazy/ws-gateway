import React from 'react';
import { Modal, Form, Input } from 'antd';
import { RoleData } from '../data';
import { existRoleId } from '../service';

interface RoleFormProps {
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

const RoleForm: React.FC<RoleFormProps> = props => {

  const { title, visible, onCancel, onSubmit, info } = props;
  const [form] = Form.useForm();

  React.useEffect(() => {
    form.setFieldsValue(info);
  }, [info])

  const handleSubmit = (values: RoleData) => {
    onSubmit({
      ...info,
      ...values,
    });
    onCancel();
  }

  return (
    <Modal
      destroyOnClose
      maskClosable={false}
      title={title}
      visible={visible}
      onOk={() => form.submit()}
      onCancel={() => onCancel()}
    >
      <Form {...formItemLayout} preserve={false} form={form} initialValues={info} onFinish={handleSubmit}>
        <Form.Item label="角色" name="id"
          rules={[
            {
              required: true,
              message: '请输入角色',
            },
            {
              validator: (_, value) => (value === '' || value === info.id) ? Promise.resolve() : existRoleId(value)
                .then((result: boolean) => result ? Promise.reject('角色已存在') : Promise.resolve()),
            },
          ]}
        >
          <Input placeholder="请输入" />
        </Form.Item>
      </Form>
    </Modal >
  );
}

export default RoleForm;
