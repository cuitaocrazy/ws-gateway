# 说明

该项目实现两个功能

1. 统一认证
2. 实现网关



## 用户管理

用户管理通过`http://{hostname}:{porg}/admin`进行管理

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



## 项目配置

配置在`gw/src/main/resources/application.yaml`

主要配置说明:

### `jwt.secret`

jwt签名密钥



### MongoDb

`yada.db.mongo.db`:数据库名称, 默认值:`yada_auth`

`yada.db.mongo.url:mongodb`:数据库的url, 例如:`mongodb://localhost/?replicaSet=rs`

因为这个项目中用到了mongodb的事务,因此MongoDb必须配置Replica Set



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
      - Svc=/svc,service-1 # 第一个值是service的path前缀,第二个值是service的名字
    filters:
      - AuthApi # 认证授权检查过滤器,检查所有符合{path前缀}/{service的名字}的path,如果没有通过则返回401
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
      - name: AuthApi
```



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