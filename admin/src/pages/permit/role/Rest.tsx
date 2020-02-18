import React from 'react';
import { connect } from 'dva';
import { FormComponentProps } from 'antd/es/form';
import { formatMessage } from 'umi-plugin-react/locale';
import { Form, Drawer, Input, Button } from 'antd';
import { Dispatch } from 'redux';
import { RoleData, RoleRestData } from './data';
import { RestData } from '../rest/data';
import { MethodData } from '../method/data';
import RestList from './RestList';

interface MenuPops extends FormComponentProps {
  dispatch?: Dispatch<any>;
  loading?: boolean;
  title: string;
  visible: boolean;
  hideModal(): void;
  handleFormSubmit(record: RoleRestData): void;
  role: Partial<RoleData>;
  info: Partial<RoleRestData>;
}

const Menu: React.FC<MenuPops> = props => {

  const [id] = React.useState(props.info.id);
  const [rest, setRest] = React.useState<RestData[]>([]);
  const [method, setMethod] = React.useState<MethodData[]>([]);

  React.useEffect(() => {
    props.dispatch && props.dispatch({
      type: 'role/getRest',
      callback: (rest: RestData[], method: MethodData[]) => {
        setRest(rest)
        setMethod(method)
      },
    })
  }, [id]);

  const { form, title, visible, hideModal, handleFormSubmit, role, info, loading } = props;
  const { getFieldDecorator } = form;

  const handleSubmit = (e: any) => {
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        handleFormSubmit({ ...info, ...value });
        form.resetFields();
      }
    });
  };

  return (
    <Drawer
      title={title}
      width="40%"
      destroyOnClose
      maskClosable={false}
      visible={visible}
      onClose={hideModal}
    >
      <Form>
        <Form.Item
          label={formatMessage({ id: 'role.form.id.label' })}
        >
          {getFieldDecorator('id', {
            initialValue: role.id,
          })(
            <Input readOnly placeholder={formatMessage({ id: 'role.form.id.placeholder' })} />
          )}
        </Form.Item>
        <Form.Item
          label={formatMessage({ id: 'role.form.rest.label' })}
        >
          {getFieldDecorator('rests', {
            initialValue: info.rests,
          })(
            <RestList listData={rest.map(item => item.id)} checkData={method.map(item => item.id)} />
          )}
        </Form.Item>
      </Form>
      <div
        style={{
          position: 'absolute',
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e8e8e8',
          padding: '10px 16px',
          textAlign: 'right',
          left: 0,
          background: '#fff',
          borderRadius: '0 0 4px 4px',
        }}
      >
        <Button style={{ marginRight: 8 }} onClick={hideModal} >
          取消
        </Button>
        <Button onClick={handleSubmit} type="primary" loading={loading}>
          提交
        </Button>
      </div>
    </Drawer>
  );
}

export default Form.create<MenuPops>()(
  connect(({ loading }: {
    loading: { effects: { [key: string]: boolean } };
  }) => ({
    loading: loading.effects['role/saveRest'],
  }))(Menu)
);
