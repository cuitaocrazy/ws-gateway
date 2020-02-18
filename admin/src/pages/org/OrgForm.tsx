import React from 'react';
import { Modal, Form, Input } from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { OrgData } from './data';
import { existOrgId } from './service';

interface OrgFormProps extends FormComponentProps {
  title: string;
  visible: boolean;
  onCancel(): void;
  onSubmit(value: OrgData): void;
  info: Partial<OrgData>;
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

const OrgForm: React.SFC<OrgFormProps> = props => {
  const {
    title, visible, onCancel, onSubmit,
    info, form, form: { getFieldDecorator }
  } = props;

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSubmit(values);
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
      <Form>
        <Form.Item {...formItemLayout} label="机构ID">
          {getFieldDecorator('id', {
            initialValue: info.id,
            rules: [
              {
                required: true,
                whitespace: true,
                message: '请输入机构ID',
              },
              {
                validator: async (_, value) => {
                  if (value === "")
                    return Promise.resolve();
                  if (value === info.id)
                    return Promise.resolve();
                  if (value.includes(" "))
                    return Promise.reject('机构ID不能包含空格');
                  return existOrgId(value).then((result: boolean) => result ? Promise.reject('机构ID已存在') : Promise.resolve())
                },
              },
            ],
          })(<Input placeholder="请输入" />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="机构名称">
          {getFieldDecorator('name', {
            initialValue: info.name,
            rules: [
              {
                required: true,
                message: '请输入机构名称',
              },
            ],
          })(<Input placeholder="请输入" />)}
        </Form.Item>
      </Form>
    </Modal>
  );
}

export default Form.create<OrgFormProps>()(OrgForm);
