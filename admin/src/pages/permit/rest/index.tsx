import React from 'react';
import { connect } from 'dva';
import { Card, Table, Form, Row, Col, Input, Select, Button, Divider, message } from 'antd';
import { FormattedMessage, formatMessage } from 'umi-plugin-react/locale';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Dispatch } from 'redux';
import { FormComponentProps } from 'antd/es/form';
import { RestListData, RestData, Pagination, QueryData } from './data.d';
import { MethodData } from '../method/data.d';
import { ModelState } from './model';
import Create from './Create';

import styles from './style.less';

interface RestProps extends FormComponentProps {
  dispatch: Dispatch<any>;
  data: RestListData;
  query: QueryData;
  methods: MethodData[];
  loading: boolean;
}

const Rest: React.FC<RestProps> = props => {
  const [isCreateShow, setIsCreateShow] = React.useState<boolean>(false);
  const [isUpdateShow, setIsUpdateShow] = React.useState<boolean>(false);
  const [info, setInfo] = React.useState<Partial<RestData>>({});

  React.useEffect(() => {
    props.dispatch({ type: 'rest/find' });
    props.dispatch({ type: 'rest/findMethod' });
  }, []);

  const handleQuery = (e: any) => {
    e.preventDefault();
    props.form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        props.dispatch({
          type: 'rest/find',
          payload: value,
        });
      }
    });
  };

  const handleTable = (
    pagination: Partial<Pagination>,
  ) => {
    props.dispatch({
      type: 'rest/find',
      payload: {
        ...props.query,
        ...pagination,
      },
    });
  };

  const handleCreateForm = (record: RestData) => {
    props.dispatch({
      type: 'rest/save',
      payload: record,
      callback: () => setIsCreateShow(false),
    });
  };

  const handleUpdate = (record: RestData) => {
    setInfo(record);
    setIsUpdateShow(true);
  };

  const handleUpdateForm = (record: RestData, id: string) => {
    props.dispatch({
      type: 'rest/update',
      payload: {
        id,
        payload: record,
      },
      callback: () => setIsUpdateShow(false),
    });
  };

  const handleRemove = (record: RestData) => {
    props.dispatch({
      type: 'rest/remove',
      payload: record.id,
      callback: () => message.success('删除成功'),
    });
  };

  const columns = [
    {
      title: formatMessage({ id: 'rest.columns.id' }),
      dataIndex: 'id',
    },
    {
      title: formatMessage({ id: 'rest.columns.path' }),
      dataIndex: 'path',
    },
    {
      title: formatMessage({ id: 'rest.columns.method' }),
      dataIndex: 'method',
    },
    {
      title: formatMessage({ id: 'rest.columns.remark' }),
      dataIndex: 'remark',
    },
    {
      title: formatMessage({ id: 'rest.options' }),
      render: (_: any, record: RestData) => (
        <React.Fragment>
          <a onClick={() => handleRemove(record)}>
            <FormattedMessage id="rest.options.remove" />
          </a>
          <Divider type="vertical" />
          <a onClick={() => handleUpdate(record)}>
            <FormattedMessage id="rest.options.update" />
          </a>
        </React.Fragment>
      ),
    },
  ];

  const { form, query, data, methods, loading } = props;
  const { getFieldDecorator } = form;

  return (
    <PageHeaderWrapper title={formatMessage({ id: 'rest.title' })}>
      <Card bordered={false}>
        <div className={styles.tableList}>
          <div className={styles.tableListForm}>
            <Form layout="inline" onSubmit={handleQuery}>
              <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
                <Col md={8} sm={24}>
                  <Form.Item label={formatMessage({ id: 'rest.query.path.label' })}>
                    {getFieldDecorator('path', {
                      initialValue: query.path,
                    })(<Input placeholder={formatMessage({ id: 'rest.query.path.placeholder' })} />)}
                  </Form.Item>
                </Col>
                <Col md={8} sm={24}>
                  <Form.Item label={formatMessage({ id: 'rest.query.method.label' })}>
                    {getFieldDecorator('method', {
                      initialValue: query.method,
                    })(<Select placeholder={formatMessage({ id: 'rest.query.method.placeholder' })}>
                      {methods.map(item => (
                        <Select.Option key={item.id} value={item.id}>{item.id}</Select.Option>
                      ))}
                    </Select>)}
                  </Form.Item>
                </Col>
                <Col md={8} sm={24}>
                  <span className={styles.submitButtons}>
                    <Button type="primary" htmlType="submit">
                      <FormattedMessage id="rest.button.query" />
                    </Button>
                    <Button style={{ marginLeft: 8 }} onClick={() => form.resetFields()}>
                      <FormattedMessage id="rest.button.reset" />
                    </Button>
                  </span>
                </Col>
              </Row>
            </Form>
          </div>
          <div className={styles.tableListOperator}>
            <Button icon="plus" type="primary" onClick={() => setIsCreateShow(true)}>
              <FormattedMessage id="rest.button.create" />
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
        title={formatMessage({ id: 'rest.create.title' })}
        visible={isCreateShow}
        hideModal={() => setIsCreateShow(false)}
        handleFormSubmit={handleCreateForm}
        info={{}}
        methods={methods}
      />
      <Create
        title={formatMessage({ id: 'rest.update.title' })}
        visible={isUpdateShow}
        hideModal={() => setIsUpdateShow(false)}
        handleFormSubmit={handleUpdateForm}
        info={info}
        methods={methods}
      />
    </PageHeaderWrapper>
  );
};

export default Form.create<RestProps>()(
  connect(
    ({
      rest,
      loading,
    }: {
      rest: ModelState;
      loading: { models: { [key: string]: boolean } };
    }) => ({
      data: rest.data,
      query: rest.query,
      methods: rest.methods,
      loading: loading.models.rest,
    }),
  )(Rest),
);
