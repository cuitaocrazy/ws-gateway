import React from 'react';
import { connect } from 'dva';
import { Card, Table, Form, Row, Col, Input, Button, Modal, List } from 'antd';
import { FormattedMessage, formatMessage } from 'umi-plugin-react/locale';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Dispatch } from 'redux';
import { FormComponentProps } from 'antd/es/form';
import { PaginationConfig, SorterResult, TableCurrentDataSource } from 'antd/es/table';
import { ServiceListData, ServiceData, QueryData } from './data';
import { ModelState } from './model';

import styles from './style.less';

interface ServiceProps extends FormComponentProps {
  dispatch: Dispatch<any>;
  data: ServiceListData;
  query: QueryData;
  loading: boolean;
}

const ServiceList: React.FC<ServiceProps> = props => {
  const [isShowRes, setIsShowRes] = React.useState<boolean>(false);
  const [info, setInfo] = React.useState<Partial<ServiceData>>({});

  React.useEffect(() => {
    props.dispatch({ type: 'service/find' });
  }, []);

  const handleQuery = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        props.dispatch({
          type: 'service/find',
          payload: value,
        });
      }
    });
  };

  const handleTable = (pagination: PaginationConfig,
    _filters: Partial<Record<keyof ServiceData, string[]>>, _sorter: SorterResult<ServiceData>,
    _extra: TableCurrentDataSource<ServiceData>) => {
    props.dispatch({
      type: 'service/find',
      payload: {
        ...props.query,
        ...pagination,
      },
    });
  };

  const showRes = (record: ServiceData) => {
    setInfo(record);
    setIsShowRes(true);
  };

  const columns = [
    {
      title: formatMessage({ id: 'service.columns.id' }),
      dataIndex: 'id',
    },
    {
      title: formatMessage({ id: 'service.columns.md5' }),
      dataIndex: 'md5',
    },
    {
      title: formatMessage({ id: 'service.options' }),
      render: (_: any, record: ServiceData) => (
        <React.Fragment>
          <a onClick={() => showRes(record)}>
            <FormattedMessage id="service.options.res" />
          </a>
        </React.Fragment>
      ),
    },
  ];

  const { form, query, data, loading } = props;
  const { getFieldDecorator } = form;

  return (
    <PageHeaderWrapper title={formatMessage({ id: 'service.title' })}>
      <Card bordered={false}>
        <div className={styles.tableList}>
          <div className={styles.tableListForm}>
            <Form layout="inline" onSubmit={handleQuery}>
              <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
                <Col md={8} sm={24}>
                  <Form.Item label={formatMessage({ id: 'service.query.id.label' })}>
                    {getFieldDecorator('id', {
                      initialValue: query.id,
                    })(<Input placeholder={formatMessage({ id: 'service.query.id.label' })} />)}
                  </Form.Item>
                </Col>
                <Col md={8} sm={24}>
                  <span className={styles.submitButtons}>
                    <Button type="primary" htmlType="submit">
                      <FormattedMessage id="service.button.query" />
                    </Button>
                    <Button style={{ marginLeft: 8 }} onClick={() => form.resetFields()}>
                      <FormattedMessage id="service.button.reset" />
                    </Button>
                  </span>
                </Col>
              </Row>
            </Form>
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
      <Modal
        width="60%"
        bodyStyle={{ padding: '32px 40px 48px' }}
        title={formatMessage({ id: 'service.modal.title' })}
        maskClosable={false}
        visible={isShowRes}
        onOk={() => setIsShowRes(false)}
        onCancel={() => setIsShowRes(false)}
      >
        <List
          header={<div>{info.id}</div>}
          bordered
          dataSource={info.res}
          renderItem={item => (
            <List.Item>
              {item}
            </List.Item>
          )}
        />
      </Modal>
    </PageHeaderWrapper >
  );
};

export default Form.create<ServiceProps>()(
  connect(
    ({
      service,
      loading,
    }: {
      service: ModelState;
      loading: { models: { [key: string]: boolean } };
    }) => ({
      data: service.data,
      query: service.query,
      loading: loading.models.service,
    }),
  )(ServiceList),
);
