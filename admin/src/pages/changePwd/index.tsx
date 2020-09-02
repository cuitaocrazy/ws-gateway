import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { LockOutlined } from '@ant-design/icons';
import { Card, Form, Input, Button, notification } from 'antd';
import { ModifyData } from './data';
import { changePwd } from './service';

const formLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 24 },
    md: { span: 8 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 24 },
    md: { span: 16 },
  },
};

const tailLayout = {
  wrapperCol: { offset: 8, span: 8 },
};

const ChangePwdView: React.FC = () => {

  const [form] = Form.useForm();

  const handleSubmit = async (values: ModifyData) => {
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

  return (
    <PageContainer>
      <Card bordered={false}>
        <h1 style={{ textAlign: 'center' }}>修改密码</h1>
        <Form style={{ marginTop: 40 }}
          {...formLayout}
          form={form}
          onFinish={handleSubmit}
        >
          <Form.Item label="原密码" name="oldPwd"
            rules={[
              {
                required: true,
                message: "请输入原密码",
              },
            ]}
          >
            <Input.Password placeholder="请输入原密码" prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item label="新密码" name="newPwd" rules={[
            {
              required: true,
              message: "请输入新密码",
            },
            {
              pattern: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,32}$/,
              message: "密码需要是数字加大小写字母组合,长度范围在8到32位",
            },
          ]}>
            <Input.Password placeholder="请输入新密码" prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item label="确认密码" name="checkPwd"
            rules={[
              {
                required: true,
                message: "请再次输入新密码",
              },
              {
                validator: (_, value) => (value === '' || value === form.getFieldValue('newPwd')) ?
                  Promise.resolve() : Promise.reject("与新密码不一致"),
              },
            ]}
          >
            <Input.Password placeholder="请再次输入新密码" prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item {...tailLayout}>
            <Button block size="large" type="primary" onClick={() => form.submit()}>提交</Button>
          </Form.Item>
        </Form>
      </Card>
    </PageContainer >
  )
};

export default ChangePwdView;
