import React from 'react';
import { Tree } from 'antd';
import { TreeNodeNormal, AntTreeNodeCheckedEvent } from 'antd/lib/tree/Tree';

interface MenuTreeProps {
  treeData: TreeNodeNormal[];
  value?: string[];
  onChange?: (value: string[]) => void;
}

const MenuTree: React.SFC<MenuTreeProps> = props => {
  const { treeData, value, onChange } = props;

  const handleCheck = (v: string[] | {
    checked: string[];
    halfChecked: string[];
  }, _: AntTreeNodeCheckedEvent) => {
    if (v instanceof Array) {
      if (onChange) onChange(v as string[])
    } else {
      if (onChange) onChange((v as { checked: string[]; halfChecked: string[]; }).checked)
    }
  }

  return (
    <Tree
      checkable
      checkStrictly
      defaultExpandAll
      selectable={false}
      treeData={treeData}
      checkedKeys={value}
      onCheck={handleCheck}
    />
  )
}

export default MenuTree;
