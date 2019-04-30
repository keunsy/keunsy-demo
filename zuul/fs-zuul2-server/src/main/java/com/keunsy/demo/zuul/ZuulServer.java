package com.keunsy.demo.zuul;

import com.google.inject.Injector;
import com.keunsy.demo.zuul.config.RouteConfig;
import com.keunsy.demo.zuul.module.CustomBeanModule;
import com.netflix.governator.InjectorBuilder;
import com.netflix.zuul.netty.server.BaseServerStartup;
import com.netflix.zuul.netty.server.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created on 2019/4/22.
 */
@Slf4j
@Component
public class ZuulServer implements ApplicationListener<ContextRefreshedEvent> {
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    new Thread(() -> bootstrap()).start();
  }

  public void bootstrap() {
    // TODO: 2019/4/28 熔断（避免服务无限访问）、放大每秒限制
    log.info("start to bootstrap zuul server");
    Server server = null;
    try {
      //      DynamicConfiguration configuration = new DynamicConfiguration(new CustomPolledConfigurationSource(), new FixedDelayPollingScheduler());
      //        BaseConfiguration configuration = new BaseConfiguration();
      // init filter（未使用groovy，无需动态调用过滤器）
      RouteConfig.init();

      Injector injector = InjectorBuilder.fromModule(new CustomBeanModule()).createInjector();
      BaseServerStartup serverStartup = injector.getInstance(BaseServerStartup.class);
      server = serverStartup.server();
      server.start(true);
      log.info("zuul server starting up.");
    } catch (Exception e) {
      log.error("start up zuul server failed.", e);
    } finally {
      if (server != null) {
        server.stop();
        log.warn("zuul server stop.");
      }
    }
  }
}
