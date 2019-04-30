package com.keunsy.demo.zuul.config;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.moandjiezana.toml.Toml;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Created on 2019/4/22.
 */
@Slf4j
public class RouteConfig {
  private static final AntPathMatcher matcher = new AntPathMatcher();
  private static final boolean DEFAULT_STRIP_PREFIX = false;
  private static final long DEFAULT_READ_TIMEOUT = 30 * 1000l;
  private static final long DEFAULT_MAX_CONNECTIONS_PER_HOST = 1000;

  public static Map<String, ZuulRoute> zuulRoutes = Maps.newConcurrentMap();
  public static Map<String, String> extraConfig = Maps.newConcurrentMap();
  public static Queue<Map<String, Object>> dynamicQueue = Queues.newLinkedBlockingDeque();
  public static Set<String> loadBalanceRules;

  public static void init() {
    ConcurrentMapConfiguration configuration = new ConcurrentMapConfiguration();
    configuration.setProperty("zuul.filters.classes", "RouteInboundFilter");
    configuration.setProperty("zuul.filters.packages", "com.netflix.zuul.filters.common");
    // disable eureka
    configuration.setProperty("eureka.registration.enabled", false);
    configuration.setProperty("eureka.shouldFetchRegistry", false);
    configuration.setProperty("eureka.validateInstanceId", false);
    ConfigurationManager.loadPropertiesFromConfiguration(configuration);

    //    ConfigFactory.getConfig("fs-zuul-route-toml", config -> { dynamic config system
    try {
      //      Toml toml = new Toml().read(config.getString());//config content or file
      Toml toml = new Toml().read("");//config content or file, also can use netflix dynamic config
      Map<String, ZuulRoute> newRouteConfig = Maps.newConcurrentMap();
      //        Map<String, Object> ribbonMap = Maps.newHashMap();
      for (Map.Entry<String, Object> entry : toml.entrySet()) {
        String proxyName = entry.getKey();
        Toml value = (Toml) entry.getValue();
        String path = value.getString("path", "/" + proxyName + "/**");
        String url = value.getString("url");
        if (Strings.isNullOrEmpty(proxyName) || Strings.isNullOrEmpty(url)) {
          log.warn("param is not valid, config is {}", value.toString());
          continue;
        }
        ZuulRoute route = ZuulRoute
          .builder()
          .proxyName(proxyName)
          .path(path)
          .url(url)
          .grayHeaderKey(value.getString("grayHeaderKey"))
          .grayValues(value.getList("grayValues"))
          .grayUrl(value.getString("grayUrl"))
          .stripPrefix(value.getBoolean("stripPrefix", DEFAULT_STRIP_PREFIX))
          .readTimeout(value.getLong("readTimeout", DEFAULT_READ_TIMEOUT).intValue())
          .loadBalanceRule(value.getString("loadBalanceRule"))
          .maxConnections(value.getLong("maxConnections", DEFAULT_MAX_CONNECTIONS_PER_HOST).intValue())
          .build();
        newRouteConfig.put(proxyName, route);
        // 第一次不处理
        configuration.setProperty(proxyName + ".ribbon." + CommonClientConfigKey.ListOfServers.key(), route.getUrl());
        configuration.setProperty(proxyName + ".ribbon." + CommonClientConfigKey.ReadTimeout.key(), route.getReadTimeout());
        configuration.setProperty(proxyName + ".ribbon." + CommonClientConfigKey.MaxConnectionsPerHost.key(), route.getMaxConnections());
        //          ribbonMap.put(proxyName + ".ribbon." + CommonClientConfigKey.ListOfServers.key(), route.getUrl());
        //          ribbonMap.put(proxyName + ".ribbon." + CommonClientConfigKey.ReadTimeout.key(), route.getReadTimeout());
        //          ribbonMap.put(proxyName + ".ribbon." + CommonClientConfigKey.MaxConnectionsPerHost.key(), route.getMaxConnections());
        //          for (Map.Entry<String, String> extraEntry : extraConfig.entrySet()) {
        //            if (extraEntry.getValue().equals(proxyName)) {
        //              ribbonMap.put(extraEntry.getKey() + ".ribbon." + CommonClientConfigKey.ListOfServers.key(), route.getUrl());
        //              ribbonMap.put(extraEntry.getKey() + ".ribbon." + CommonClientConfigKey.ReadTimeout.key(), route.getReadTimeout());
        //              ribbonMap.put(extraEntry.getKey() + ".ribbon." + CommonClientConfigKey.MaxConnectionsPerHost.key(), route.getMaxConnections());
        //            }
        //          }
      }
      //        dynamicQueue.add(ribbonMap);
      zuulRoutes = newRouteConfig;
    } catch (Exception e) {
      log.error("load road config filed", e);
    }
    //    });
    loadBalanceRules = Sets.newHashSet("RoundRobinRule", "AvailabilityFilteringRule", "WeightedResponseTimeRule", "ZoneAvoidanceRule", "BestAvailableRule", "RandomRule", "RetryRule");
  }

  public static String getMatchedProxyName(String path) {
    // 最长匹配
    return zuulRoutes.entrySet().stream().filter(entry -> matcher.match(entry.getValue().getPath(), path)).sorted((entry1, entry2) -> {
      if (entry1.getValue().getPath().length() >= entry2.getValue().getPath().length()) {
        return -1;
      }
      return 1;
    }).findFirst().map(Map.Entry::getKey).orElse("");
  }
}
