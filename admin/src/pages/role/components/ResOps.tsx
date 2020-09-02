import React from 'react';
import { Card, Checkbox } from 'antd';
import { CheckboxChangeEvent } from 'antd/lib/checkbox';
import { CheckboxValueType } from 'antd/lib/checkbox/Group';
import { Operator, SvcData } from '../data';

interface ResOpsProps {
  svcId: string;
  uri: string;
  ops: Operator[];
  value: SvcData[];
  onChange(value: SvcData[]): void;
}

const ResOps: React.FC<ResOpsProps> = props => {

  const { svcId, uri, ops, value, onChange } = props;

  const [indeterminate, setIndeterminate] = React.useState<boolean>(false);
  const [checkAll, setCheckAll] = React.useState<boolean>(false);
  const [checkedList, setCheckedList] = React.useState<Operator[]>([]);

  React.useEffect(() => {
    const valueOps = value.filter(svcRes => svcRes.id === svcId)
      .map(svcRes => svcRes.resources).reduce((a, b) => [...a, ...b], [])
      .filter(res => res.uri === uri).map(res => res.ops).reduce((a, b) => [...a, ...b], []);
    setIndeterminate(valueOps.length > 0 && valueOps.length < ops.length);
    setCheckAll(valueOps.length >= ops.length);
    setCheckedList(valueOps);
  }, [value]);

  const onCheckAllChange = (e: CheckboxChangeEvent) => {
    const otherRes = value.filter(svcRes => svcRes.id === svcId)[0]?.resources?.filter(res => res.uri !== uri) || [];
    if (e.target.checked) {
      onChange([
        ...value.filter(svcRes => svcRes.id !== svcId),
        {
          id: svcId,
          resources: [
            ...otherRes,
            { uri, ops },
          ],
        },
      ]);
    } else {
      onChange([
        ...value.filter(svcRes => svcRes.id !== svcId),
        {
          id: svcId,
          resources: [
            ...otherRes,
          ],
        },
      ]);
    }
  }

  const onCheckedChange = (checkedValue: CheckboxValueType[]) => {
    const otherRes = value.filter(svcRes => svcRes.id === svcId)[0]?.resources?.filter(res => res.uri !== uri) || [];
    if (checkedValue.length > 0) {
      onChange([
        ...value.filter(svcRes => svcRes.id !== svcId),
        {
          id: svcId,
          resources: [
            ...otherRes,
            { uri, ops: checkedValue.map(item => item as Operator), },
          ],
        },
      ]);
    } else {
      onChange([
        ...value.filter(svcRes => svcRes.id !== svcId),
        {
          id: svcId,
          resources: [
            ...otherRes,
          ],
        },
      ]);
    }
  }

  return (
    <Card
      size="small"
      title={uri}
      extra={<Checkbox
        indeterminate={indeterminate}
        checked={checkAll}
        onChange={onCheckAllChange}
      >全选</Checkbox>}
    >
      <Checkbox.Group
        options={ops}
        value={checkedList}
        onChange={onCheckedChange}
      />
    </Card>
  )
}

export default ResOps;
