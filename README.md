#服务发现调用框架

##FEATURE

* 1.agent+javasist实现的代码注入功能 
* 2.kafka的异步消息  
* 3.AVRO RPC服务发现和调用
* 4.HTTP服务发现和调用

##Required
* zookeeper (3.4.6)
* kafka 0.8.* (0.9+ 不支持)
* JDK1.8+(1.8.0.25)



##HOW TO RUN
* 1.git clone
* 2.cd ./soa_tools
* 3.mvn compile package install
* 4.cd ./soa_agent
* 5.mvn compile package install
* 6.cd ./soa_avro
* 7.mvn compile package install
* 8.cd ./soa_http
* 9.mvn compile package install
* 10.cd ./soa_kafka
* 11.mvn compile package install
* 12.cd ./soa_discovery
* 13.mvn compile package install
* 14.cd ./soa_demo 
* 15.mvn compile package
* 16.java -jar ./target/soa_demo.jar

and see the console
or 
http://localhost:9093/monitor/providers 中查看服务提供节点信息

##FAQ/CONTACT
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件(lolandkidtress#gmail.com, 把#换成@)
* QQ: 248954468


##TODO:

soa_demo中使用spring mvc,提供http服务(springboot实现 2016-08-03)

* avro的client连接池
* 服务端流量控制
* guava的eventBus实现自动降级 (Mock假数据返回)
* 服务地址IP路由