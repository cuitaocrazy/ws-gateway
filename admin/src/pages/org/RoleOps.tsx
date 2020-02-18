import React from 'react';
import { Card, Checkbox } from 'antd';
import { CheckboxChangeEvent } from 'antd/lib/checkbox';
import { CheckboxValueType } from 'antd/lib/checkbox/Group';
import { AppData, RoleIdData } from './data';

interface RoleOpsProps {
  app: AppData;
  value: RoleIdData[];
  onChange(value: RoleIdData[]): void;
}

const RoleOps: React.FC<RoleOpsProps> = props => {

  const { app, value, onChange } = props;

  const [indeterminate, setIndeterminate] = React.useState<boolean>(false);
  const [checkAll, setCheckAll] = React.useState<boolean>(false);
  const [checkedList, setCheckedList] = React.useState<string[]>([]);

  React.useEffect(() => {
    const valueOps = value.filter(roleId => roleId.appId === app.id).map(roleId => roleId.roleName);
    setIndeterminate(valueOps.length > 0 && valueOps.length < app.roles.length);
    setCheckAll(valueOps.length >= app.roles.length);
    setCheckedList(valueOps);
  }, [value]);

  const onCheckAllChange = (e: CheckboxChangeEvent) => {
    if (e.target.checked) {
      onChange([
        ...value.filter(roleId => roleId.appId !== app.id),
        ...app.roles.map(role => ({
          appId: app.id,
          roleName: role.name,
        })),
      ]);
    } else {
      onChange([
        ...value.filter(roleId => roleId.appId !== app.id),
      ]);
    }
  }

  const onCheckedChange = (checkedValue: CheckboxValueType[]) => {
    if (checkedValue.length > 0) {
      onChange([
        ...value.filter(roleId => roleId.appId !== app.id),
        ...checkedValue.map(value => value as string).map(name => ({
          appId: app.id,
          roleName: name,
        })),
      ]);
    } else {
      onChange([
        ...value.filter(roleId => roleId.appId !== app.id),
      ]);
    }
  }

  return (
    <Card
      size="small"
      title={app.id}
      extra={<Checkbox
        indeterminate={indeterminate}
        checked={checkAll}
        onChange={onCheckAllChange}
      >全选</Checkbox>}
    >
      <Checkbox.Group
        options={app.roles.map(role => role.name)}
        value={checkedList}
        onChange={onCheckedChange}
      />
    </Card>
  )
}

export default RoleOps;
