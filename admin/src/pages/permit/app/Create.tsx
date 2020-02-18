import React from 'react';
import { FormComponentProps } from 'antd/es/form';
import { formatMessage } from 'umi-plugin-react/locale';
import { Form, Modal, Input } from 'antd';
import { AppData } from './data.d';

interface CreatePops extends FormComponentProps {
  title: string;
  visible: boolean;
  hideModal(): void;
  handleFormSubmit(record: AppData): void;
  info: Partial<AppData>;
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

  const { form, title, visible, hideModal, handleFormSubmit, info } = props;
  const { getFieldDecorator } = form;

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        handleFormSubmit({ ...info, ...value });
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
      okText={formatMessage({ id: 'app.form.submit' })}
      onOk={handleSubmit}
      onCancel={hideModal}
    >
      <Form>
        <Form.Item {...formItemLayout} label={formatMessage({ id: 'app.form.id.label' })}>
          {getFieldDecorator('id', {
            initialValue: info.id,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.form.id.required' }),
              },
            ],
          })(<Input placeholder={formatMessage({ id: 'app.form.id.placeholder' })} />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label={formatMessage({ id: 'app.form.name.label' })}>
          {getFieldDecorator('name', {
            initialValue: info.name,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.form.name.required' }),
              },
            ],
          })(<Input placeholder={formatMessage({ id: 'app.form.name.placeholder' })} />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label={formatMessage({ id: 'app.form.path.label' })}>
          {getFieldDecorator('path', {
            initialValue: info.path,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.form.path.required' }),
              },
            ],
          })(<Input placeholder={formatMessage({ id: 'app.form.name.placeholder' })} />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label={formatMessage({ id: 'app.form.remark.label' })}>
          {getFieldDecorator('remark', {
            initialValue: info.remark,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.form.remark.required' }),
              },
            ],
          })(
            <Input.TextArea
              style={{ minHeight: 32 }}
              placeholder={formatMessage({ id: 'app.form.remark.placeholder' })}
              rows={3}
            />,
          )}
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default Form.create<CreatePops>()(Create);
