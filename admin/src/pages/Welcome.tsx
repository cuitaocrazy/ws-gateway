import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Typography } from 'antd';
import Link from 'umi/link';

import styles from './Welcome.less';

export default (): React.ReactNode => (
  <PageHeaderWrapper>
    <Card>
      <Typography.Text strong>
        <Link to="/svc">服务管理</Link>
      </Typography.Text>
      <pre className={styles.pre}>
        服务管理
      </pre>
      <Typography.Text strong>
        <Link to="/role">角色管理</Link>
      </Typography.Text>
      <pre className={styles.pre}>
        角色管理
      </pre>
      <Typography.Text
        strong
        style={{
          marginBottom: 12,
        }}
      >
        <Link to="/org">用户管理</Link>
      </Typography.Text>
      <pre className={styles.pre}>
        用户管理
      </pre>
    </Card>
  </PageHeaderWrapper>
);
