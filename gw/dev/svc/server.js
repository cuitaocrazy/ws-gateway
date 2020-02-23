const express = require('express')

const app = express()

const commProc = (req, res) => {
  const obj = {
    url: req.url,
    method: req.method,
    headers: req.headers
  }
  console.log(JSON.stringify(obj, null, ' '))
  res.send(obj)
}

const resListProc = (_, res) => {
  const obj = [
    {
      uri: '/res1',
      ops: ['READ', 'CREATE', 'UPDATE', 'DELETE']
    },
    {
      uri: '/res2/{id}',
      ops: ['READ', 'CREATE', 'UPDATE', 'DELETE']
    },
    {
      uri: '/res3/**',
      ops: ['READ', 'CREATE', 'UPDATE', 'DELETE']
    }
  ]
  res.send(obj)
}
app.use('/*/res_list', resListProc)
app.use(commProc)

app.listen(3000, () => console.log('server is started!'))