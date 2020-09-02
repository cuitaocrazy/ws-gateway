import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { Modal, Row, Col, Card, List } from 'antd';
import { ModelState } from '../model';
import { SvcData, ResData } from '../data';
import {} from '../service';

interface SvcUpdateProps {
  svcs: SvcData[];
  dispatch: Dispatch<any>;
  svcId: string;
  visible: boolean;
  onCancel(): void;
  onSubmit(value: SvcData): void;
}

const SvcUpdate: React.SFC<SvcUpdateProps> = props => {
  const { svcs, dispatch, svcId, visible, onSubmit, onCancel } = props;
  const [resources, setResources] = React.useState<ResData[]>([]);

  React.useEffect(() => {
    if (svcId !== '' && visible)
      dispatch({
        type: 'svc/fetchSvcActualRes',
        payload: svcId,
        callback: setResources,
      });
  }, [svcId, visible]);

  const handleUpdate = (e: any) => {
    e.preventDefault();
    onSubmit({
      id: svcId,
      resources,
    })
  }

  return (
    <Modal
      maskClosable={false}
      width="70%"
      title={`【${svcId}】资源更新`}
      visible={visible}
      onOk={handleUpdate}
      onCancel={() => {
        onCancel();
      }}
    >
      <Row gutter={24}>
        <Col span={12}>
          < Card title="旧资源">
            <List
              itemLayout="horizontal"
              dataSource={svcs.filter(svc => svc.id === svcId)[0]?.resources}
              renderItem={(item: ResData) => (
                <List.Item>
                  <List.Item.Meta title={item.uri} description={item.ops.join(' | ')} />
                </List.Item>
              )}
            />
          </Card>
        </Col>
        <Col span={12}>
          < Card title="新资源">
            <List
              itemLayout="horizontal"
              dataSource={resources}
              renderItem={(item: ResData) => (
                <List.Item>
                  <List.Item.Meta title={item.uri} description={item.ops.join(' | ')} />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
    </Modal>
  );
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
    loading: loading.models.svc,
  }),
)(SvcUpdate);
