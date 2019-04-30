[toc]

### 参考：
- 文档：https://github.com/Netflix/zuul/wiki
- sample：https://github.com/Netflix/zuul/tree/2.1/zuul-sample
- 动态配置参考：https://github.com/lexburner/zuul-gateway-demo
- 无需spring cloud、boot：https://github.com/Netflix/zuul/tree/1.x/zuul-simple-webapp
- 与ngnix性能比较：https://instea.sk/2015/04/netflix-zuul-vs-nginx-performance/
- 各类说明：http://www.cnblogs.com/hellxz/p/9282756.html
- 各类官方案例说明：https://www.baeldung.com/?s=zuul
- 过滤器更改路由：https://github.com/spring-cloud/spring-cloud-netflix/issues/1754
- 与spring cloud gateway对比：https://juejin.im/post/5ba8daa56fb9a05cfe486ebf
- 1与2对比：http://blog.didispace.com/api-gateway-Zuul-1-zuul-2-how-to-choose/

## 功能点
- 鉴权
- 过滤器（pre、route、post）
- hystrix、ribbon
- eureka
- 自定义路由规则


### 注意点
- 区分版本：1.0与2.0差异大
- 过滤器只过滤配置了route规则的（pre？待确认）
- properties无法保证路由规则的顺序，推荐使用yml格式配置文件
- path与url模式无法使用熔断功能，path与serviceId方可

## zuul2
- 中文编程参考（或直接查看官方文档）：https://blog.csdn.net/weixin_43364172/article/details/82979749
- 源码分析：https://blog.csdn.net/lengyue309/article/details/82192118
- 三类过滤器（inbound filter修改路由）
- 编程式加载配置文件 ChannelConfig channelConfig = BaseServerStartup.defaultChannelConfig();