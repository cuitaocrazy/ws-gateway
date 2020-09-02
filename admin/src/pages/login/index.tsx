import React, { useState } from 'react';
import { Alert, message } from 'antd';
import { Link, SelectLang, useModel } from 'umi';
import logo from '@/assets/logo.svg';
import Footer from '@/components/Footer';
import { LoginParamsType } from './data';
import { fetchLogin } from './service';
import LoginFrom from './components/Login';
import styles from './style.less';

const { Username, Password, Submit } = LoginFrom;

const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => (
  <Alert
    style={{
      marginBottom: 24,
    }}
    message={content}
    type="error"
    showIcon
  />
);

const Login: React.FC<{}> = () => {

  const [submitting, setSubmitting] = useState(false);
  const { refresh } = useModel('@@initialState');

  const handleSubmit = async (values: LoginParamsType) => {
    setSubmitting(true);
    try {
      await fetchLogin(values);
      message.success('登录成功！');
      setTimeout(() => {
        refresh();
      }, 0);
      window.location.href = '/admin';
    } catch (error) {
      message.error('登录失败，请重试！');
    }
    setSubmitting(false);
  };

  return (
    <div className={styles.container}>
      <div className={styles.lang}>
        <SelectLang />
      </div>
      <div className={styles.content}>
        <div className={styles.top}>
          <div className={styles.header}>
            <Link to="/">
              <img alt="logo" className={styles.logo} src={logo} />
              <span className={styles.title}>Admin Manager</span>
            </Link>
          </div>
          <div className={styles.desc}></div>
        </div>

        <div className={styles.main}>
          <LoginFrom onSubmit={handleSubmit}>
            <br />
            <br />
            {(status === 'error' && !submitting) ? (
              <LoginMessage content="账户或密码错误" />
            ) : <></>}
            <Username
              name="username"
              placeholder="用户"
              rules={[
                {
                  required: true,
                  message: '请输入用户名!',
                },
              ]}
            />
            <Password
              name="password"
              placeholder="密码"
              rules={[
                {
                  required: true,
                  message: '请输入密码！',
                },
              ]}
            />
            <Submit loading={submitting}>登录</Submit>
          </LoginFrom>
        </div>
      </div>
      <Footer />
    </div >
  );
};

export default Login;
