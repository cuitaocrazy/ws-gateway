import React, { useState, useEffect } from 'react'
import cookie from 'cookie'
import jwt from 'jsonwebtoken'


export default () => {
  const [loginInfo, setLoginInfo] = useState('')
  useEffect(() => {
    const token = cookie.parse(document.cookie)['token']
    if (token != null) {
      setLoginInfo(JSON.stringify(jwt.decode(token), null, ' '))
    } else {
      setLoginInfo('')
    }
  })

  return <div>
    <h3> 登录信息</h3>
    <pre style={{ textAlign: 'left' }}>{loginInfo}</pre>
  </div>
}
