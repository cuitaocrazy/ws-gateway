import React from 'react';
import { Modal, Form, Input } from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { AppData } from './data';
import { existAppId } from './service';

interface AppFormProps extends FormComponentProps {
  title: string;
  visible: boolean;
  onCancel(): void;
  onSubmit(value: AppData): void;
  info: Partial<AppData>;
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

const AppForm: React.SFC<AppFormProps> = props => {
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
        <Form.Item {...formItemLayout} label="应用">
          {getFieldDecorator('id', {
            initialValue: info.id,
            rules: [
              {
                required: true,
                message: '请输入应用',
              },
              {
                validator: (_, value) => value === info.id ? Promise.resolve() : existAppId(value)
                  .then((result: boolean) => result ? Promise.reject('应用已存在') : Promise.resolve()),
              },
            ],
          })(<Input placeholder="请输入" />)}
        </Form.Item>
      </Form>
    </Modal >
  );
}

export default Form.create<AppFormProps>()(AppForm);
