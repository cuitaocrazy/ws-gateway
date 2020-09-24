import React from 'react';
import { Modal, Form, Input } from 'antd';
import { SvcData } from '../data';

interface SvcFormProps {
  title: string;
  visible: boolean;
  onCancel(): void;
  onSubmit(value: SvcData): void;
  info: Partial<SvcData>;
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

const SvcForm: React.SFC<SvcFormProps> = props => {

  const { title, visible, onCancel, onSubmit, info } = props;
  const [form] = Form.useForm();

  React.useEffect(() => {
    form.setFieldsValue(info);
  }, [info])

  const handleSubmit = (values: SvcData) => {
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
        <Form.Item label="服务" name="id"
          rules={[
            {
              required: true,
              message: '请输入服务',
            },
          ]}
        >
          <Input placeholder="请输入" />
        </Form.Item>
      </Form>
    </Modal >
  );
}

export default SvcForm;
