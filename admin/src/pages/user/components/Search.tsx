import React from 'react';
import { Card, Form, Row, Col, TreeSelect, Space, Button } from 'antd';
import { DataNode } from 'antd/lib/tree';
import { UserData, OrgTreeData } from '../data';

interface SearchProps {
  orgId?: string;
  orgTree: OrgTreeData[];
  onSubmit: (params: { orgId: string }) => void;
}

const defaultColConfig = {
  lg: 8,
  md: 12,
  xxl: 8,
  xl: 8,
  sm: 12,
  xs: 24,
};

const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 },
};
const tailLayout = {
  wrapperCol: { span: 24 },
};

const SearchView: React.FC<SearchProps> = props => {

  const [form] = Form.useForm();
  const { orgId, orgTree, onSubmit } = props;

  const handleSubmit = (values: UserData) => {
    onSubmit({ orgId: values.orgId })
  }

  const makeTree = (orgTree: OrgTreeData[]): DataNode[] => orgTree.map(item => ({
    key: item.org.id,
    value: item.org.id,
    title: `${item.org.id}-${item.org.name}`,
    children: makeTree(item.children || []),
  }));

  return (
    <Card style={{ marginBottom: '20px' }} bodyStyle={{ paddingBottom: 0 }}>
      <Form
        form={form}
        initialValues={{
          orgId,
        }}
        onFinish={handleSubmit}
      >
        <Row gutter={16} justify="start">
          <Col {...defaultColConfig} >
            <Form.Item  {...layout} label="机构" name="orgId">
              <TreeSelect
                dropdownStyle={{
                  maxHeight: 400,
                  overflow: 'auto',
                }}
                placeholder="请选择"
                treeDefaultExpandAll
                treeData={makeTree(orgTree)}
              />
            </Form.Item>
          </Col>
          <Col {...defaultColConfig} ></Col>
          <Col {...defaultColConfig} style={{ textAlign: 'right' }}>
            <Form.Item {...tailLayout}>
              <Space>
                <Button onClick={() => form.resetFields()}>重置</Button>
                <Button type="primary" onClick={() => form.submit()}>查询</Button>
              </Space>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Card>
  );
}

export default SearchView;
