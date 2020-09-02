import React from 'react';
import { Dispatch } from 'redux';
import { PageHeaderWrapper, GridContent } from '@ant-design/pro-layout';
import { Spin, Tree } from 'antd';
import { connect } from 'dva';
import { DataNode } from 'antd/lib/tree';
import { OrgTreeData } from './data.d';
import { ModelState } from './model';
import OrgMenu from './components/OrgMeun';
import User from './components/User';

import styles from './style.less';

interface OrgProps {
  dispatch: Dispatch<any>;
  orgTree: OrgTreeData[];
  orgId: string;
  loading: boolean;
}

const OrgView: React.FC<OrgProps> = props => {
  const { dispatch, orgTree, orgId, loading } = props;

  React.useEffect(() => {
    dispatch({ type: 'org/fetchOrgTree' });
    dispatch({ type: 'org/fetchRoles' });
  }, []);

  const onTreeSelect = (selectedKeys: string[]) => {
    dispatch({
      type: 'org/setOrgId',
      payload: selectedKeys[0],
    });
  }

  const makeTree = (orgTree: OrgTreeData[]): DataNode[] => orgTree.map(item => ({
    key: item.org.id,
    title: <OrgMenu node={item} />,
    children: makeTree(item.children || []),
  }));

  return (
    <PageHeaderWrapper>
      <Spin spinning={loading}>
        <GridContent>
          <div className={styles.main} >
            <div className={styles.leftMenu}>
              <Tree
                defaultExpandedKeys={[orgId]}
                onSelect={onTreeSelect}
                selectedKeys={[orgId]}
                treeData={makeTree(orgTree)}
              />
            </div>
            <div className={styles.right}>
              <div className={styles.title}>
                <User />
              </div>
            </div>
          </div>
        </GridContent>
      </Spin>
    </PageHeaderWrapper >
  )
}

export default connect(
  ({
    org,
    loading,
  }: {
    org: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    orgTree: org.orgTree,
    orgId: org.orgId,
    loading: loading.models.org,
  }),
)(OrgView);
