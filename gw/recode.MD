# 关于事务的支持

## mongodb

mongodb的跨collection中3.0只支持单个collection原子操作，但不支持多个collection的事务，3.0之后支持跨collection的transaction，
**但是数据库必须为副本集（Replica Set）方式**，因此修改原先开发环境的的docker-compose的环境。

spring提供的spring-boot-starter-data-mongodb-reactive是支持mongodb的transaction的，需要提供`ReactiveMongoTransactionManager`才能正确使用。

docker-compose.yml中对mongo service加入了`command: mongod --replSet rs`使其支持Replica Set，并在mongo service启动后进入容器，执行mongodb客户端，对mongodb的rs进行配置：
`rs.initiate({_id:"rs", members:[{_id:0, host:"127.0.0.1:27017"}]})`

由于只是测试环境, 只有一个成员就足够了.