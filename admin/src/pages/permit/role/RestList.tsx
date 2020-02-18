import React from 'react';
import { List, Checkbox } from 'antd';
import { CheckboxValueType } from 'antd/es/checkbox/Group';

interface RestListProps {
  listData: string[];
  checkData: string[];
  value?: string[];
  onChange?: (value: string[]) => void;
}

const RestList: React.SFC<RestListProps> = props => {
  const { listData, checkData, value, onChange } = props;

  const handleChange = (rest: string) => (checkedValues: CheckboxValueType[]) => {
    if (onChange) onChange([
      ...(value || []),
      ...checkedValues.map(key => `${key} ${rest}`)
    ])
  }

  const getValue = (rest: string): string[] => (value || []).filter(key => key.includes(rest)).map(key => key.split(' ')[0])

  return (
    <List
      itemLayout="horizontal"
      dataSource={listData}
      renderItem={item => (
        <List.Item>
          <List.Item.Meta title={item} />
          <div><Checkbox.Group options={checkData} defaultValue={getValue(item)} onChange={handleChange(item)} /></div>
        </List.Item>
      )}
    />
  )
}

export default RestList;
