import React from 'react';
import { Dispatch } from 'redux';
import { PageContainer, GridContent } from '@ant-design/pro-layout';
import { CloseOutlined } from '@ant-design/icons';
import { Spin, Menu, Modal, Card, List, Row, Col, Tooltip, Button, notification } from 'antd';
import { connect } from 'dva';
import { SvcData, ResData } from './data.d';
import { ModelState } from './model';
import SvcForm from './components/SvcForm';
import SvcUpdate from './components/SvcUpdate';

import styles from './style.less';

const { Item } = Menu;
const { confirm } = Modal;

interface SvcProps {
  dispatch: Dispatch<any>;
  svcIds: string[];
  svcs: SvcData[];
  svcId: string;
  loading: boolean;
}

const SvcView: React.FC<SvcProps> = props => {
  const { dispatch, svcIds, svcs, svcId, loading } = props;

  const [isCreate, setIsCreate] = React.useState<boolean>(false);
  const [isUpdateSvc, setIsUpdateSvc] = React.useState<boolean>(false);

  React.useEffect(() => {
    dispatch({ type: 'svc/fetchSvcIds' });
    dispatch({ type: 'svc/fetchSvcs' });
  }, []);

  const selectKey = (key: string) => {
    dispatch({
      type: 'svc/setSvcId',
      payload: key,
    });
  }

  const handleCreate = (svc: SvcData) => {
    dispatch({
      type: 'svc/fetchUpdateSvc',
      payload: svc,
      callback: () => {
        setIsUpdateSvc(false);
        notification.success({
          message: '添加服务操作成功',
          description: `服务【${svc.id}】已添加成功!`,
        });
      },
    });
  }

  const handleUpdate = (svc: SvcData) => {
    dispatch({
      type: 'svc/fetchUpdateSvc',
      payload: svc,
      callback: () => {
        setIsUpdateSvc(false);
        notification.success({
          message: '更新操作成功',
          description: `服务资源【${svc.id}】已更新成功!`,
        });
      },
    });
  }

  const handleRemove = (id: string) => {
    confirm({
      title: `确定要删除【${id}】服务资源?`,
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        dispatch({
          type: 'svc/fetchDeleteSvc',
          payload: id,
          callback: () => {
            setIsUpdateSvc(false);
            notification.success({
              message: '删除操作成功',
              description: `服务资源【${id}】已删除成功!`,
            });
          },
        });
      },
    });
  }

  return (
    <PageContainer extra={<Button type="link" onClick={() => setIsCreate(true)}>新增</Button>}>
      <Spin spinning={loading}>
        <GridContent>
          <div className={styles.main} >
            <div className={styles.leftMenu}>
              <Menu
                mode="inline"
                selectedKeys={[svcId]}
                onClick={({ key }) => selectKey(key + '')}
              >
                {svcs.map(svc => (
                  <Item key={svc.id} disabled={!svcIds.includes(svc.id)}>
                    {svcIds.includes(svc.id) ? svc.id : (
                      <Row>
                        <Col span={20}>{svc.id}</Col>
                        <Col span={4}>
                          <Tooltip title="删除服务">
                            <Button type="link" shape="circle" size="small"
                              onClick={e => {
                                e.stopPropagation();
                                handleRemove(svc.id);
                              }} >
                              <CloseOutlined style={{ marginRight: 0 }} />
                            </Button>
                          </Tooltip>
                        </Col>
                      </Row>
                    )}
                  </Item>
                ))}
              </Menu>
            </div>
            <div className={styles.right}>
              <div className={styles.title}>
                <Card title={svcId} extra={svcId !== '' && <Button type="link" onClick={() => setIsUpdateSvc(true)}>更新</Button>}>
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
      <SvcForm title="服务新增" visible={isCreate} onCancel={() => setIsCreate(false)} onSubmit={handleCreate} info={{ resources: [] }} />
      <SvcUpdate svcId={svcId} visible={isUpdateSvc} onCancel={() => setIsUpdateSvc(false)} onSubmit={handleUpdate} />
    </PageContainer>
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
    svcIds: svc.svcIds,
    svcs: svc.svcs,
    svcId: svc.svcId,
    loading: loading.models.svc,
  }),
)(SvcView);
