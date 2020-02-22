import React, { useState } from 'react'

export default () => {
  const [apiUrl, setApiUrl] = useState('http://localhost:8080/svc/service-1/api/test')
  const [apiResult, setApiResult] = useState('')
  return <div>
    <h3>api url</h3>
    <input style={{ width: '80%' }} type="text" defaultValue={apiUrl} onBlur={(e) => setApiUrl(e.target.value)} />
    <input type="button" value="call get" onClick={() => {
      fetch(apiUrl).then(res => {
        return res.json()
      }).then(body => setApiResult(JSON.stringify(body, null, ' ')))
        .catch(e => setApiResult(e.toString()))
    }} />
    <p>结果:</p>
    <pre style={{ textAlign: 'left' }}>{apiResult}</pre>
  </div>
}