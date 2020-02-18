import React from 'react';
import { Dispatch } from 'redux';
import { PageHeaderWrapper, GridContent } from '@ant-design/pro-layout';
import { Spin, Button, Icon, Tree, Card, Empty } from 'antd';
import { connect } from 'dva';
import { AppData, KeyData } from './data.d';
import { ModelState } from './model';
import TreeMenu from './TreeMeun';
import AppRes from './AppRes';
import RoleRes from './RoleRes';
import AppForm from './AppForm';

import styles from './style.less';

const { TreeNode } = Tree;

interface AppProps {
  dispatch: Dispatch<any>;
  apps: AppData[];
  keys: KeyData[];
  appId: string;
  roleId: string;
  loading: boolean;
}

const AppView: React.FC<AppProps> = props => {
  const { dispatch, apps, appId, roleId, loading } = props;

  const [isCreateApp, setIsCreateApp] = React.useState<boolean>(false);

  React.useEffect(() => {
    dispatch({ type: 'app/fetchApps' });
  }, []);

  const onTreeSelect = (selectedKeys: string[]) => {
    if (selectedKeys.length > 0) {
      const ids = selectedKeys[0].split('#');
      dispatch({
        type: 'app/setId',
        payload: {
          appId: ids[0],
          roleId: ids[1],
        },
      });
    }
  }

  const handleCreateApp = (app: AppData) => {
    dispatch({
      type: 'app/fetchCreateOrUpdateApp',
      payload: app,
    });
  }

  const renderRes = () => {
    if (apps.map(app => app.id).includes(appId)) {
      if (!roleId) {
        return (
          <AppRes app={apps.filter(app => app.id === appId)[0]} />
        )
      } else if (apps.filter(app => app.id === appId)[0].roles.map(role => role.name).includes(roleId)) {
        return (
          <RoleRes app={apps.filter(app => app.id === appId)[0]} />
        )
      }
    }
    return (<Card><Empty /></Card>)
  }

  return (
    <PageHeaderWrapper>
      <Spin spinning={loading}>
        <GridContent>
          <div className={styles.main} >
            <div className={styles.leftMenu}>
              <Button type="dashed" onClick={() => setIsCreateApp(true)}>
                <Icon type="plus" />
              </Button>
              <Tree
                onSelect={onTreeSelect}
                selectedKeys={[`${appId}#${roleId}`]}
              >
                {apps.map(app => (
                  <TreeNode
                    appId={app.id}
                    key={`${app.id}#`}
                    title={<TreeMenu app={app} />}
                    children={app.roles?.map(role => (
                      <TreeNode isLeaf
                        appId={app.id}
                        roleId={role.name}
                        key={`${app.id}#${role.name}`}
                        title={<TreeMenu app={app} role={role} />} />
                    ))}
                  />
                ))}
              </Tree>
            </div>
            <div className={styles.right}>
              <div className={styles.title}>{renderRes()}</div>
            </div>
          </div>
        </GridContent>
      </Spin>
      <AppForm title="添加应用" visible={isCreateApp} onCancel={() => setIsCreateApp(false)}
        info={{ roles: [], resources: [] }} onSubmit={handleCreateApp} />
    </PageHeaderWrapper>
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
    apps: app.apps,
    appId: app.appId,
    roleId: app.roleId,
    keys: app.keys,
    loading: loading.models.app,
  }),
)(AppView);
