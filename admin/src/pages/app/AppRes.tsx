import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { Card, Collapse } from 'antd';
import { SvcData, AppData, KeyData } from './data';
import { ModelState } from './model';
import { SvcResData } from './data';
import ResOps from './ResOps';

const { Panel } = Collapse;

interface AppResProps {
  dispatch: Dispatch<any>;
  app: AppData;
  svcs: SvcData[];
  appId: string;
  keys: KeyData[];
  loading: boolean;
}

const AppRes: React.FC<AppResProps> = props => {

  const { dispatch, app, appId, svcs, keys } = props;

  const [appRes, setAppRes] = React.useState<SvcResData[]>([]);

  React.useEffect(() => {
    dispatch({ type: 'app/fetchSvcs' });
  }, []);

  React.useEffect(() => {
    setAppRes(app.resources);
  }, [app]);

  React.useEffect(() => {

  }, [svcs, keys]);

  const handleChange = (activeKey: string | string[]) => {
    dispatch({
      type: 'app/setKeys',
      payload: {
        appId,
        roleId: "",
        activeKey,
      },
    });
  }

  const handleSubmit = () => {
    dispatch({
      type: 'app/fetchCreateOrUpdateApp',
      payload: {
        ...app,
        resources: appRes,
      },
    });
  }

  return (
    <Card title={`资源管理【${app.id}】`} extra={<a href="#" onClick={handleSubmit}>保存</a>}>
      <Collapse key={app.id} onChange={handleChange}
        activeKey={keys.filter(key => (key.appId === appId && key.roleId === ""))[0]?.activeKey}
      >
        {svcs.map(svc => (
          <Panel header={svc.id} key={`${app.id}#${svc.id}`}>
            {svc.resources?.map(res => (
              <ResOps key={`${app.id}#${svc.id}#${res.uri}`} svcId={svc.id} uri={res.uri} ops={res.ops} value={appRes} onChange={setAppRes} />
            ))}
          </Panel>
        ))}
      </Collapse>
    </Card>
  )
}

export default connect(
  ({
    app,
    loading,
  }: {
    app: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    svcs: app.svcs,
    keys: app.keys,
    appId: app.appId,
    loading: loading.models.app,
  }),
)(AppRes);