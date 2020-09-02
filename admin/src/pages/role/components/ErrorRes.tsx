import React from 'react';
import { Alert, Modal, Button, Card, List } from 'antd';
import { RoleData, SvcData, ResData } from '../data';
import { getErrorRes } from '../utils';

interface ErrorResProps {
  role: RoleData;
  svcs: SvcData[];
}

const ErrorRes: React.FC<ErrorResProps> = props => {
  const { role, svcs } = props;

  const [isShowError, setIsShowError] = React.useState<boolean>(false);

  const errorSvcs: SvcData[] = getErrorRes(role.svcs, svcs);

  return errorSvcs.length > 0 ? (
    <>
      <Alert message={(
        <>
          角色中存在不可用的资源<Button type="link" onClick={() => setIsShowError(true)}>查看详情</Button>
        </>
      )} type="error" />
      <br />
      <Modal
        title={`角色【${role.id}】中的不可用的资源`}
        visible={isShowError}
        onCancel={() => setIsShowError(false)}

        footer={null}
      >
        {errorSvcs.map((svc: SvcData) => (
          <Card key={svc.id} title={svc.id} size="small" style={{ marginTop: 16 }}>
            <List
              key={svc.id}
              itemLayout="horizontal"
              dataSource={svc?.resources}
              renderItem={(item: ResData) => (
                <List.Item key={svc.id + item.uri}>
                  <List.Item.Meta title={item.uri} description={item.ops.join(' | ')} />
                </List.Item>
              )}
            />
          </Card>))}
      </Modal>
    </>
  ) : (<></>)
}

export default ErrorRes;
