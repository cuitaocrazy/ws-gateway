import React from 'react';
import { Dispatch } from 'redux';
import { connect } from 'dva';
import { AppData, RoleIdData } from './data';
import { ModelState } from './model';
import RoleOps from './RoleOps';

interface RoleFieldProps {
  dispatch: Dispatch<any>;
  apps: AppData[];
  value?: RoleIdData[];
  onChange?(value: RoleIdData[]): void;
}

const RoleField: React.FC<RoleFieldProps> = props => {
  const { dispatch, apps, value, onChange } = props;

  React.useEffect(() => {
    dispatch({ type: 'org/fetchApps' });
  }, []);

  const defaultChange = (value: RoleIdData[]) => { }

  return (
    <>
      {apps.map(app => (<RoleOps app={app} value={value || []} onChange={onChange || defaultChange} />))}
    </>
  );
}

export default connect(
  ({
    org,
    loading,
  }: {
    org: ModelState,
    loading: { models: { [key: string]: boolean } };
  }) => ({
    apps: org.apps,
    loading: loading.models.org,
  }),
)(RoleField);
