#服务发现与调用框架

##FEATURE

* 1.agent+javasist实现的代码注入功能 
* 2.kafka的异步消息  
* 3.AVRO RPC服务发现和调用
* 4.HTTP服务发现和调用


##Required
* zookeeper (3.4.6)
* kafka 0.8.* (0.9+ 不支持)
* JDK1.8+(基于1.8.0.25开发测试)



##HOW TO RUN
* 1.git clone
* 2.cd soa_framework_agg

* 3.cd ./soa_agent
* 4.mvn clean compile package install

* 5.cd ..
* 6.mvn clean compile package install

* 7.cd ./soa_demo 
* 8.mvn compile package
* 9.java -jar ./target/soa_demo.jar

and see the console
or 
http://localhost:9093/monitor/providers 中查看服务提供节点信息
http://localhost:9094/monitor/providerTreeMap 中查看服务提供节点Hash环的分布情况

##FAQ/CONTACT
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件(lolandkidtress#gmail.com, 把#换成@)
* QQ: 248954468


##TODO:

* avro的client连接池
* 服务端流量控制
* 自动降级 (Mock假数据返回) 
* 服务地址IP路由