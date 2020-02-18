import React from 'react';
import { connect } from 'dva';
import { FormComponentProps } from 'antd/es/form';
import { TreeNodeNormal } from 'antd/lib/tree/Tree';
import { formatMessage } from 'umi-plugin-react/locale';
import { Form, Drawer, Input, Button } from 'antd';
import { Dispatch } from 'redux';
import { RoleData, RoleMenuData } from './data';
import { MenuData } from '@/pages/permit/menu/data'
import MenuTree from './MenuTree';

interface MenuPops extends FormComponentProps {
  dispatch?: Dispatch<any>;
  loading?: boolean;
  title: string;
  visible: boolean;
  hideModal(): void;
  handleFormSubmit(record: RoleMenuData): void;
  role: Partial<RoleData>;
  info: Partial<RoleMenuData>;
}

const Menu: React.FC<MenuPops> = props => {

  const [id] = React.useState(props.info.id);
  const [menu, setMenu] = React.useState([]);

  React.useEffect(() => {
    props.dispatch && props.dispatch({
      type: 'role/getMenu',
      callback: setMenu,
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

  const loopTree = (node: Partial<MenuData>): TreeNodeNormal => {
    const root: TreeNodeNormal = { key: node.id || '', title: node.name || '' }
    if (node.children && node.children.length > 0) {
      root.children = node.children.map(loopTree);
    }
    return root;
  }

  const treeData = loopTree({ id: 'root', name: 'root', children: menu }).children || [];

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
          label={formatMessage({ id: 'role.form.menu.label' })}
        >
          {getFieldDecorator('menus', {
            initialValue: info.menus,
          })(
            <MenuTree treeData={treeData} />
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
    loading: loading.effects['role/saveMenu'],
  }))(Menu)
);
