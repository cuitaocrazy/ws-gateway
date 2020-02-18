import React from 'react';
import { FormComponentProps } from 'antd/es/form';
import { formatMessage } from 'umi-plugin-react/locale';
import { Form, Modal, Input, Select } from 'antd';
import { RestData } from './data.d';
import { MethodData } from '../method/data.d'

interface CreatePops extends FormComponentProps {
  title: string;
  visible: boolean;
  hideModal(): void;
  handleFormSubmit(record: RestData, id?: string): void;
  info: Partial<RestData>;
  methods: MethodData[];
}

const Create: React.SFC<CreatePops> = props => {
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

  const { form, title, visible, hideModal, handleFormSubmit, info, methods } = props;
  const { getFieldDecorator } = form;

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        handleFormSubmit({ ...info, ...value, id: `${value.method} ${value.path}` }, info.id);
        form.resetFields();
      }
    });
  };

  return (
    <Modal
      width="60%"
      bodyStyle={{ padding: '32px 40px 48px' }}
      title={title}
      maskClosable={false}
      visible={visible}
      okText={formatMessage({ id: 'rest.form.submit' })}
      onOk={handleSubmit}
      onCancel={hideModal}
    >
      <Form>
        <Form.Item {...formItemLayout} label={formatMessage({ id: 'rest.form.path.label' })}>
          {getFieldDecorator('path', {
            initialValue: info.path,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'rest.form.path.required' }),
              },
              {
                pattern: /^\/.*/,
                message: formatMessage({ id: 'rest.form.path.required' }),
              },
              {
                validator: (rule: any, value: any, callback: any) => {
                  // TODO 后台校验资源是否重复
                  callback()
                },
              },
            ],
          })(<Input placeholder={formatMessage({ id: 'rest.form.path.placeholder' })} />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label={formatMessage({ id: 'rest.form.method.label' })}>
          {getFieldDecorator('method', {
            initialValue: info.method,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'rest.form.method.required' }),
              },
            ],
          })(<Select placeholder={formatMessage({ id: 'rest.form.method.placeholder' })}>
            {methods.map(item => (
              <Select.Option key={item.id} value={item.id}>{item.id}</Select.Option>
            ))}
          </Select>)}
        </Form.Item>
        <Form.Item {...formItemLayout} label={formatMessage({ id: 'rest.form.remark.label' })}>
          {getFieldDecorator('remark', {
            initialValue: info.remark,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'rest.form.remark.required' }),
              },
            ],
          })(<Input.TextArea
            style={{ minHeight: 32 }}
            placeholder={formatMessage({ id: 'rest.form.remark.placeholder' })}
            rows={3}
          />)}
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default Form.create<CreatePops>()(Create);
