import React from 'react';
import { Dispatch } from 'redux';
import { PageContainer } from '@ant-design/pro-layout';
import { Table, Button, Divider, Modal, notification } from 'antd';
import { connect } from 'dva';
import { OrgTreeData, OrgData } from './data.d';
import { ModelState } from './model';
import Form from './components/Form';

const { confirm } = Modal;

interface OrgProps {
  dispatch: Dispatch<any>;
  orgTree: OrgTreeData[];
  loading: boolean;
}

const OrgView: React.FC<OrgProps> = props => {
  const { dispatch, orgTree, loading } = props;

  const [info, setInfo] = React.useState<Partial<OrgData>>({});
  const [isCreate, setIsCreate] = React.useState<boolean>(false);
  const [isUpdate, setIsUpdate] = React.useState<boolean>(false);

  React.useEffect(() => {
    dispatch({ type: 'org/fetchOrgTree' });
  }, []);

  const handleCreateOrg = (org: OrgData) => {
    dispatch({
      type: 'org/fetchCreateOrUpdateOrg',
      payload: org,
      callback: () => {
        notification.success({
          message: '添加机构操作成功',
          description: `机构【${org.id}-${org.name}】已添加成功!`,
        });
      },
    });
  }

  const handleUpdateShow = (org: OrgData) => {
    setInfo(org);
    setIsUpdate(true);
  }

  const handleUpdateOrg = (org: OrgData) => {
    dispatch({
      type: 'org/fetchCreateOrUpdateOrg',
      payload: org,
      callback: () => {
        notification.success({
          message: '修改机构操作成功',
          description: `机构【${org.id}-${org.name}】已修改成功!`,
        });
      },
    });
  }

  const handleRemoveOrg = (orgId: string) => {
    confirm({
      title: `确定要删除【${orgId}】机构?`,
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        dispatch({
          type: 'org/fetchDeleteOrg',
          payload: orgId,
          callback: () => {
            notification.success({
              message: '删除机构操作成功',
              description: `机构【${orgId}】已删除成功!`,
            });
          },
        });
      },
    });
  }

  const makeTree = (orgTree: OrgTreeData[]): OrgData[] => orgTree.map(item => ({
    key: item.org.id,
    id: item.org.id,
    name: item.org.name,
    children: item.children ? makeTree(item.children) : undefined,
  }));

  const columns = [
    {
      title: '机构号',
      dataIndex: 'id',
    },
    {
      title: '机构名称',
      dataIndex: 'name',
    },
    {
      title: '操作',
      dataIndex: 'id',
      render: (_: string, record: OrgData) => (
        <>
          <a onClick={() => handleUpdateShow(record)}>
            编辑
          </a>
          <Divider type="vertical" />
          <a onClick={() => handleRemoveOrg(record.id)}>
            删除
          </a>
        </>
      ),
    },
  ];

  return (
    <PageContainer extra={<Button type="link" onClick={() => setIsCreate(true)}>新增</Button>}>
      <Table<OrgData>
        loading={loading}
        columns={columns}
        dataSource={makeTree(orgTree)}
        pagination={false}
      />
      <Form
        title="机构新增" visible={isCreate} info={{}}
        onCancel={() => setIsCreate(false)} onSubmit={handleCreateOrg}
      />
      <Form
        title="机构编辑" visible={isUpdate} info={info}
        onCancel={() => setIsUpdate(false)} onSubmit={handleUpdateOrg}
      />
    </PageContainer >
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
    loading: loading.models.org,
  }),
)(OrgView);
