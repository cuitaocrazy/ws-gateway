# 关于事务的支持

## mongodb

mongodb的跨collection中3.0只支持单个collection原子操作，但不支持多个collection的事务，3.0之后支持跨collection的transaction，
**但是数据库必须为副本集（Replica Set）方式**，因此修改原先开发环境的的docker-compose的环境。

spring提供的spring-boot-starter-data-mongodb-reactive是支持mongodb的transaction的，需要提供`ReactiveMongoTransactionManager`才能正确使用。

docker-compose.yml中对mongo service加入了`command: mongod --replSet rs`使其支持Replica Set，并在mongo service启动后进入容器，执行mongodb客户端，对mongodb的rs进行配置：
`rs.initiate({_id:"rs", members:[{_id:0, host:"127.0.0.1:27017"}]})`

由于只是测试环境, 只有一个成员就足够了.

# WebFlux

## 注意事项

当json api响应返回数组时，可以返回`Flux<T>`或`Mono<List<T>>`
但当`T`时`String`类型时只能返回`Mono<List<String>>`
原因是`Flux<String>`会按**chunk**处理，这也是合理的，例如返回大型html时
因此WebFlux认为，你返回`Flux<String>`你就是让我按chunk处理的
例子：
ok().body(Flux.just(ofList("1", "2")))
将返回`12`
ok().body(Mono.just(ofList("1", "2")))
将返回`["1", "2"]`

为了不引起奇异，可能会把`Flux<T>`都改成`Mono<List<T>>`,
就算你返回`Flux<T>`只要`T`不是`String`，也是在服务端把
这些Object读完转成json数组再返回请求方