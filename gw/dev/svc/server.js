const express = require('express')

const app = express()

const proc = (req, res) => {
  const obj = {
    url: req.url,
    method: req.method,
    headers: req.headers
  }
  console.log(JSON.stringify(obj, null, ' '))
  res.send(obj)
}
app.get('*', proc)
app.put('*', proc)
app.delete('*', proc)
app.post('*', proc)

app.listen(3000, () => console.log('server is started!'))