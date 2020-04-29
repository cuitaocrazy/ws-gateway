# 说明

该项目实现两个功能

1. 统一认证
2. 实现网关

网关的谓词和过滤器可以用spring cloud gateway自带的, 可根据需要在配置文件里配置,参考:[谓词](https://cloud.spring.io/spring-cloud-gateway/reference/html/#gateway-request-predicates-factories), [过滤器](https://cloud.spring.io/spring-cloud-gateway/reference/html/#gatewayfilter-factories)

## 环境要求

jdk 1.8
mongodb 3.0+ 必须开启Replica Set已支持事务

## 用户管理

用户管理通过`http://{hostname}:{port}/admin`进行管理

管理员只有一个:admin

密码默认为:changepwd

管理员只能在这个用户使用对于网关链接的其他app无效

创建的用户默认密码:changepwd



## 认证

登录通过`http://{hostname}:{port}/login`

登录后token存放在cookie里,效期是1小时

可通过`http://{hostname}:{port}/refresh_token`的GET刷新token

当请求app需要认证时会自动跳转到这个页面

认证通过的app页面请求或service api请求都会在下游请求的headers加入两扩展:

1. `X-YADA-ORG-ID`: 机构Id
2. `X-YADA-USER-ID`:用户Id

以便于下游系统使用



app所要使用的api列表可以通过`http://{hostname}:{port}/filter_apis`以获取当前用户可访问的api列表

请求方法`GET`, `conten-type: application/json`

body例子:

```json
[
  {
    "uri": "/service-1/res1",
    "ops": ["READ", "WRITE"]
  },
	{
    "uri": "/service-1/res2",
    "ops": ["READ"]
  }
]
```

返回body和请求body一样

**注意: uri是/{serviceId}/{resPath}的组合, 不包含前缀**



## 项目配置

配置在`gw/src/main/resources/application.yaml`

### 基本配置


| key                            | 说明                                                         | 类型                                                         | 条件([bcp 14](https://tools.ietf.org/html/bcp14)) | 默认值        | 例子                                                         |
| ------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ---------------------------------------------- | ------------- | ------------------------------------------------------------ |
| `jwt.secret`                   | wt签名密钥                                                   | String                                                       | May                                            | `yadajwt`     |                                                              |
| `jwt.expire`                   | jwt时效                                                      | Long(秒)                                                     | May                                            | `3600`(1小时) |                                                              |
| `yada.user.defaultPwd`         | 用户的默认密码                                               | String                                                       | May                                            | `changepwd`   |                                                              |
| `yada.recaptcha`               | 验证码服务                                                   | 枚举类型:<br/>`recaptcha.Google`: 直接访问google人机验证<br/>`recaptcha.GoogleCN`: 中国区访问google人机验证<br/>`recaptcha.None`: 不验证 | Must                                           |               |                                                              |
| `yada.password.strength.score` | 密码强度                                                     | 枚举类型:<br/>`0` :Weak<br/>`1` :Fair<br/>`2` :Good<br/>`3` :Strong<br/>`4` :Very strong | May                                            | 1             |                                                              |
| `yada.admin.ipWhitelist`       | admin webapp访问白名单. 允许访问管理页面的ip, 不再白名单列表的ip都会返回404 | ipv4和ipv6列表,以逗号分隔                                    | May                                            | <空字符串>    | `127.0.0.1,0:0:0:0:0:0:0:1`                                  |
| `yada.db.mongo.db`             | 数据库名称                                                   | 要复合mongodb数据库名的规范                                  | May                                            | `yada_auth`   |                                                              |
| `yada.db.mongo.url`            | mongodb链接字符串                                            | mongodb必须带上Replica Set名称                               | Must                                           |               | `mongodb://localhost/?replicaSet=rs`                         |
| `server.port`                  | 服务端口                                                     | 参照[iana](https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml) | May                                            | 8080          |                                                              |
| `management.endpoints`         | Spring管理接口配置                                           |                                                              | May                                            |               | `management.endpoints.web.exposure.include: metrics,gateway`<br/>`management.endpoints.health.show-details: always` |



### App

当需要配置前端app时:

```yaml
spring.cloud.gateway.routes:
  - uri: http://localhost:3000 # app站点的uri
    predicates:
      - App=/app1 # app的前缀路径,也是app的页面网址
    filters:
      - Auth # 认证授权检查过滤器,只检查app页面网址,其他都转发,没有通过则跳转登录
```

该配置和下面的配置等效:

```yaml
spring.cloud.gateway.routes:
  - uri: http://localhost:3000
    predicates:
      - name: App
        path: /app1
    filters:
      - name: Auth
```



### Service

当前端app访问service api时需要配置service

```yaml
spring.cloud.gateway.routes:
  - uri: http://localhost:3000 # 服务站点的uri
    predicates:
      - Svc=/svc,service-1 # 第一个值是service的path前缀,第二个值是service的Id
    filters:
      - ApiAuth # 认证授权检查过滤器,如果不检查授权,建议: ApiAuth=uncheckPower
```

等效配置:

```yaml
spring.cloud.gateway.routes:
  - uri: http://localhost:3000
    predicates:
    	- name: Svc
    	  pathPrefix: /svc
    	  svcId: service-1
    filters:
      - name: ApiAuth
      	checkPower: checkPower # 默认值就是“checkPower”,如果不检查授权就配非“checkPower”的字符串(区分大小写),建议使用“uncheckPower”这个字符串
```



## 下游服务开发

### 资源曝露接口

网关需要提供资源列表,以便于网关抓去提供给app应用使用

uri的[规则](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/util/pattern/PathPattern.html)

op的种类:`READ` `CREATE` `UPDATE` `DELETE`

op和http method的映射:

```text
HttpMethod.GET -> Operator.READ
HttpMethod.OPTIONS -> Operator.READ
HttpMethod.TRACE -> Operator.READ
HttpMethod.POST -> Operator.CREATE
HttpMethod.PUT -> Operator.UPDATE
HttpMethod.PATCH -> Operator.UPDATE
HttpMethod.DELETE -> Operator.DELETE
```

例子:

```json
[
  {
    "uri": "/foo/{id}/{param}",
    "ops": ["READ", "CREATE"]
  },
  {
    "uri": "/bar",
    "ops": ["READ"]
  }
]
```

特定的uri:`res_list`

例如: http://localhost:3000/{context path}/res_list



### api请求headers扩展

服务api的请求会被网关做两个headers扩展:

`X-YADA-ORG-ID`: 请求用户的机构Id

`X-YADA-USER-ID`: 请求用户的用户Id

如果服务需要,可以通过request的headers读取



## 编译

安装admin webapp依赖

```zsh
cd admin
npm i
```

编译整个项目

```zsh
cd gw
gradle buildAll
```

打包都的jar在`gw/build/libs`目录


## 开发环境

docker compose在`gw/dev`目录下
第一步: 
```zsh
docker-compose build
```
主要构建app镜像和svc镜像

第二步:

```zsh
docker-compose up
```

启动所有服务, 可以忽略第一步,直接进行这一步

第三步:

```zsh
docker exec -it ${mongo docker container id} bash
mongo
rs.initiate({_id:"rs", members:[{_id:0, host:"127.0.0.1:27017"}]})
exit
exit
```

container id可用`docker ps`查看

配置mongodb的Replica Set

至此辅助环境配置完毕

#### Docker服务说明

##### mongo

数据服务

##### mongo-express

mongodb数据浏览器

url: http://localhost:8082

##### app

测试应用

context path为:/app/app-1

url: http://localhost:3010/app/app-1

##### svc

测试服务

context path: 无(接收任何uri)

url: http://localhost:3011

接受`GET` `POST` `PUT` `DELETE`方法,返回一些请求信息

#### gw开发配置

配置文件在`gw/src/main/resources/application-dev.yaml`

已经配置好了app,svc的测试,地址分别是:

http://localhost:8080/app/app-1

http://localhost:8080/svc/service-1

启动这个配置需要环境变量`spring.profiles.active=dev`, 详情查阅spring和idea的相关配置



## 关于动态路由

本来想写个动态路由, 但是它在spring cloud gateway已经实现.

[使用文档](https://cloud.spring.io/spring-cloud-gateway/multi/multi__actuator_api.html)

这个api只有一个毛病: 当`GET`请求`/actuator/gateway/routes`时, 他现在返回的json并没有返回整个结构, 而是返回谓词和过滤器的toString()字符串.

这个在`org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint#routes`方法上已经标注`// TODO: Flush out routes without a definition`, 因此就等待他的这个接口完善后就可以完美实现动态路由.

**注意: 在实验他的添加删除接口时, 别忘了调用下`POST` `/actuator/gateway/refresh`以刷新路由**

