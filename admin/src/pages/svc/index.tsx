import React from 'react';
import { Dispatch } from 'redux';
import { GridContent } from '@ant-design/pro-layout';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Spin, Menu, Card, List } from 'antd';
import { connect } from 'dva';
import { SvcData, ResData } from './data.d';
import { ModelState } from './model';

import styles from './style.less';

const { Item } = Menu;

interface SvcProps {
  dispatch: Dispatch<any>;
  svcs: SvcData[];
  svcId: string;
  loading: boolean;
}

const SvcView: React.FC<SvcProps> = props => {
  const { dispatch, svcs, svcId, loading } = props;

  React.useEffect(() => {
    dispatch({ type: 'svc/fetchSvcs' });
  }, []);

  const selectKey = (key: string) => {
    dispatch({
      type: 'svc/setSvcId',
      payload: key,
    });
  }

  return (
    <PageHeaderWrapper>
      <Spin spinning={loading}>
        <GridContent>
          <div className={styles.main} >
            <div className={styles.leftMenu}>
              <Menu
                mode="inline"
                selectedKeys={[svcId]}
                onClick={({ key }) => selectKey(key)}
              >
                {svcs.map(svc => <Item key={svc.id}>{svc.id}</Item>)}
              </Menu>
            </div>
            <div className={styles.right}>
              <div className={styles.title}>
                <Card title={svcId}>
                  <List
                    loading={loading}
                    itemLayout="horizontal"
                    dataSource={svcs.filter(svc => svc.id === svcId)[0]?.resources}
                    renderItem={(item: ResData) => (
                      <List.Item>
                        <List.Item.Meta title={item.uri} description={item.ops.join(' | ')} />
                      </List.Item>
                    )}
                  />
                </Card>
              </div>
            </div>
          </div>
        </GridContent>
      </Spin>
    </PageHeaderWrapper>
  )
}

export default connect(
  ({
    svc,
    loading,
  }: {
    svc: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    svcs: svc.svcs,
    svcId: svc.svcId,
    loading: loading.models.svc,
  }),
)(SvcView);
