import React from 'react';
import { connect } from 'dva';
import { Card, Table, Form, Row, Col, Input, Button, Divider, message } from 'antd';
import { FormattedMessage, formatMessage } from 'umi-plugin-react/locale';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Dispatch } from 'redux';
import { FormComponentProps } from 'antd/es/form';
import { PaginationConfig, SorterResult, TableCurrentDataSource } from 'antd/es/table';
import { MethodListData, MethodData, QueryData } from './data';
import { ModelState } from './model';
import Create from './Create';

import styles from './style.less';

interface MethodProps extends FormComponentProps {
  dispatch: Dispatch<any>;
  data: MethodListData;
  query: QueryData;
  loading: boolean;
}

const Method: React.FC<MethodProps> = props => {
  const [isCreateShow, setIsCreateShow] = React.useState<boolean>(false);
  const [isUpdateShow, setIsUpdateShow] = React.useState<boolean>(false);
  const [info, setInfo] = React.useState<Partial<MethodData>>({});

  React.useEffect(() => {
    props.dispatch({ type: 'method/find' });
  }, []);

  const handleQuery = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        props.dispatch({
          type: 'method/find',
          payload: value,
        });
      }
    });
  };

  const handleTable = (pagination: PaginationConfig,
    _filters: Partial<Record<keyof MethodData, string[]>>,
    _sorter: SorterResult<MethodData>,
    _extra: TableCurrentDataSource<MethodData>) => {
    props.dispatch({
      type: 'method/find',
      payload: {
        ...props.query,
        ...pagination,
      },
    });
  };

  const handleCreateForm = (record: MethodData) => {
    props.dispatch({
      type: 'method/save',
      payload: record,
      callback: () => setIsCreateShow(false),
    });
  };

  const handleUpdate = (record: MethodData) => {
    setInfo(record);
    setIsUpdateShow(true);
  };

  const handleUpdateForm = (record: MethodData) => {
    props.dispatch({
      type: 'method/update',
      payload: {
        id: record.id,
        payload: record,
      },
      callback: () => setIsUpdateShow(false),
    });
  };

  const handleRemove = (record: MethodData) => {
    props.dispatch({
      type: 'method/remove',
      payload: record.id,
      callback: () => message.success('删除成功'),
    });
  };

  const columns = [
    {
      title: formatMessage({ id: 'method.columns.id' }),
      dataIndex: 'id',
    },
    {
      title: formatMessage({ id: 'method.columns.name' }),
      dataIndex: 'name',
    },
    {
      title: formatMessage({ id: 'method.columns.remark' }),
      dataIndex: 'remark',
    },
    {
      title: formatMessage({ id: 'method.options' }),
      render: (_: any, record: MethodData) => (
        <React.Fragment>
          <a onClick={() => handleRemove(record)}>
            <FormattedMessage id="method.options.remove" />
          </a>
          <Divider type="vertical" />
          <a onClick={() => handleUpdate(record)}>
            <FormattedMessage id="method.options.update" />
          </a>
        </React.Fragment>
      ),
    },
  ];

  const { form, query, data, loading } = props;
  const { getFieldDecorator } = form;

  return (
    <PageHeaderWrapper title={formatMessage({ id: 'method.title' })}>
      <Card bordered={false}>
        <div className={styles.tableList}>
          <div className={styles.tableListForm}>
            <Form layout="inline" onSubmit={handleQuery}>
              <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
                <Col md={8} sm={24}>
                  <Form.Item label={formatMessage({ id: 'method.query.id.label' })}>
                    {getFieldDecorator('id', {
                      initialValue: query.id,
                    })(<Input placeholder={formatMessage({ id: 'method.query.id.label' })} />)}
                  </Form.Item>
                </Col>
                <Col md={8} sm={24}>
                  <Form.Item label={formatMessage({ id: 'method.query.name.label' })}>
                    {getFieldDecorator('name', {
                      initialValue: query.name,
                    })(<Input placeholder={formatMessage({ id: 'method.query.name.label' })} />)}
                  </Form.Item>
                </Col>
                <Col md={8} sm={24}>
                  <span className={styles.submitButtons}>
                    <Button type="primary" htmlType="submit">
                      <FormattedMessage id="method.button.query" />
                    </Button>
                    <Button style={{ marginLeft: 8 }} onClick={() => form.resetFields()}>
                      <FormattedMessage id="method.button.reset" />
                    </Button>
                  </span>
                </Col>
              </Row>
            </Form>
          </div>
          <div className={styles.tableListOperator}>
            <Button icon="plus" type="primary" onClick={() => setIsCreateShow(true)}>
              <FormattedMessage id="method.button.create" />
            </Button>
          </div>
          <Table
            loading={loading}
            dataSource={data.list}
            pagination={data.pagination}
            onChange={handleTable}
            columns={columns}
            rowKey={record => record.id}
          />
        </div>
      </Card>
      <Create
        title={formatMessage({ id: 'method.create.title' })}
        visible={isCreateShow}
        hideModal={() => setIsCreateShow(false)}
        handleFormSubmit={handleCreateForm}
        info={{}}
      />
      <Create
        title={formatMessage({ id: 'method.update.title' })}
        visible={isUpdateShow}
        hideModal={() => setIsUpdateShow(false)}
        handleFormSubmit={handleUpdateForm}
        info={info}
      />
    </PageHeaderWrapper>
  );
};

export default Form.create<MethodProps>()(
  connect(
    ({
      method,
      loading,
    }: {
      method: ModelState;
      loading: { models: { [key: string]: boolean } };
    }) => ({
      data: method.data,
      query: method.query,
      loading: loading.models.method,
    }),
  )(Method),
);
