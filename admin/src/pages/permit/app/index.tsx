import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { Button, Card, Icon, List, Typography, message } from 'antd';
import { FormattedMessage, formatMessage } from 'umi-plugin-react/locale';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { StateType } from './model';
import { AppData } from './data.d';
import Create from './Create';

import styles from './style.less';

const { Paragraph } = Typography;

interface AppProps {
  dispatch: Dispatch<any>;
  list: AppData[];
  loading: boolean;
}

const App: React.FC<AppProps> = props => {
  const [isCreateShow, setIsCreateShow] = React.useState<boolean>(false);
  const [isUpdateShow, setIsUpdateShow] = React.useState<boolean>(false);
  const [info, setInfo] = React.useState<Partial<AppData>>({});

  React.useEffect(() => {
    props.dispatch({ type: 'permitAndApp/fetch' });
  }, []);

  const handleCreateForm = (record: AppData) => {
    props.dispatch({
      type: 'permitAndApp/save',
      payload: record,
      callback: () => setIsCreateShow(false),
    });
  };

  const handleUpdate = (record: AppData) => {
    setInfo(record);
    setIsUpdateShow(true);
  };

  const handleUpdateForm = (record: AppData) => {
    props.dispatch({
      type: 'permitAndApp/update',
      payload: {
        id: record.id,
        payload: record,
      },
      callback: () => setIsUpdateShow(false),
    });
  };

  const handleRemove = (record: AppData) => {
    props.dispatch({
      type: 'permitAndApp/remove',
      payload: record.id,
      callback: () => message.success('删除成功'),
    });
  };

  const { list, loading } = props;

  return (
    <PageHeaderWrapper title={formatMessage({ id: 'app.title' })}>
      <div className={styles.cardList}>
        <List<AppData | string>
          rowKey="id"
          loading={loading}
          grid={{ gutter: 24, lg: 3, md: 2, sm: 1, xs: 1 }}
          dataSource={['', ...list]}
          renderItem={item => {
            if (typeof item === 'object') {
              return (
                <List.Item key={item.id}>
                  <Card
                    hoverable
                    className={styles.card}
                    actions={[
                      <a key="option1" onClick={() => handleUpdate(item)}>编辑</a>,
                      <a key="option2">角色</a>,
                      <a key="option2" onClick={() => handleRemove(item)}>删除</a>,
                    ]}
                  >
                    <Card.Meta
                      title={<a>{item.id}</a>}
                      avatar={<img className={styles.cardAvatar} src="https://gw.alipayobjects.com/zos/rmsportal/MjEImQtenlyueSmVEfUD.svg" />}
                      description={
                        <Paragraph className={styles.item} ellipsis={{ rows: 3 }}>
                          <div>{item.name}</div>
                          {item.remark}
                        </Paragraph>
                      }
                    />
                  </Card>
                </List.Item>
              );
            } else {
              return (
                <List.Item>
                  <Button type="dashed" className={styles.newButton} onClick={() => setIsCreateShow(true)}>
                    <Icon type="plus" /> 新增应用
                  </Button>
                </List.Item>
              )
            }
          }}
        />
      </div>
      <Create
        title={formatMessage({ id: 'app.create.title' })}
        visible={isCreateShow}
        hideModal={() => setIsCreateShow(false)}
        handleFormSubmit={handleCreateForm}
        info={{}}
      />
      <Create
        title={formatMessage({ id: 'app.update.title' })}
        visible={isUpdateShow}
        hideModal={() => setIsUpdateShow(false)}
        handleFormSubmit={handleUpdateForm}
        info={info}
      />
    </PageHeaderWrapper>
  )
}

export default connect(
  ({
    permitAndApp,
    loading,
  }: {
    permitAndApp: StateType;
    loading: {
      models: { [key: string]: boolean };
    };
  }) => ({
    list: permitAndApp.list,
    loading: loading.models.permitAndApp,
  }),
)(App);
