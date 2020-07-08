import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Icon, Input, Button, notification } from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { changePwd } from './service';

const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 8 },
};

const tailLayout = {
  wrapperCol: { offset: 8, span: 8 },
};

const ChangePwdView: React.FC<FormComponentProps> = props => {
  console.log(props.form)
  const {
    form, form: { getFieldDecorator }
  } = props;

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll(async (err, values) => {
      if (!err) {
        try {
          await changePwd(values);
          form.resetFields();
          notification.success({
            message: "密码修改成功！",
          });
        } catch (error) {
          notification.error({
            message: "密码修改失败！",
          });
        }
      }
    });
  }

  return (
    <PageHeaderWrapper>
      <Card bordered={false}>
        <h1 style={{ textAlign: 'center' }}>修改密码</h1>
        <Form style={{ marginTop: 40 }}>
          <Form.Item label="原密码" {...layout} >
            {getFieldDecorator('oldPwd', {
              rules: [
                {
                  required: true,
                  message: "请输入原密码",
                },
              ],
            })(<Input.Password placeholder="请输入原密码" prefix={<Icon type="lock" />} />)}
          </Form.Item>
          <Form.Item label="新密码" {...layout} >
            {getFieldDecorator('newPwd', {
              rules: [
                {
                  required: true,
                  message: "请输入新密码",
                },
                {
                  pattern: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,32}$/,
                  message: "密码需要是数字加大小写字母组合,长度范围在8到32位",
                },
              ],
            })(<Input.Password placeholder="请输入新密码" prefix={<Icon type="lock" />} />)}
          </Form.Item>
          <Form.Item label="确认密码" {...layout} >
            {getFieldDecorator('checkPwd', {
              rules: [
                {
                  required: true,
                  message: "请再次输入新密码",
                },
                {
                  validator: (_, value) => (value === '' || value === form.getFieldValue('newPwd')) ?
                    Promise.resolve() : Promise.reject("与新密码不一致"),
                },
              ],
            })(<Input.Password placeholder="请再次输入新密码" prefix={<Icon type="lock" />} />)}
          </Form.Item>
          <Form.Item {...tailLayout}>
            <Button block size="large" type="primary" onClick={handleSubmit}>提交</Button>
          </Form.Item>
        </Form>
      </Card>
    </PageHeaderWrapper >
  )
};

export default Form.create<FormComponentProps>()(ChangePwdView);;
