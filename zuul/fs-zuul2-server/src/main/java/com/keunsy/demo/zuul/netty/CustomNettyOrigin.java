package com.keunsy.demo.zuul.netty;

import com.google.common.base.Strings;
import com.keunsy.demo.zuul.config.RouteConfig;
import com.keunsy.demo.zuul.config.ZuulRoute;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.spectator.api.Registry;
import com.netflix.zuul.origins.BasicNettyOrigin;

/**
 * Created on 2019/4/19.
 */
public class CustomNettyOrigin extends BasicNettyOrigin {
  public CustomNettyOrigin(String name, String vip, Registry registry) {
    super(name, vip, registry);
  }

  @Override
  protected IClientConfig setupClientConfig(String name) {
    DefaultClientConfigImpl clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues(name);
    clientConfig.set(CommonClientConfigKey.ClientClassName, name);
    clientConfig.loadProperties(name);
    ZuulRoute route = setConfig(clientConfig, name);
    if (route != null) {
      if (!Strings.isNullOrEmpty(route.getLoadBalanceRule()) && RouteConfig.loadBalanceRules.contains(route.getLoadBalanceRule())) {
        clientConfig.set(CommonClientConfigKey.NFLoadBalancerRuleClassName, "com.netflix.loadbalancer." + route.getLoadBalanceRule());
      }
      clientConfig.set(CommonClientConfigKey.MaxConnectionsPerHost, route.getMaxConnections());//default is 50
    }
    return clientConfig;
  }

  @Override
  public IClientConfig getClientConfig() {
    // 重新设置 防止出现变更
    IClientConfig clientConfig = super.getClientConfig();
    setConfig(clientConfig, clientConfig.getClientName());
    return clientConfig;
  }

  private ZuulRoute setConfig(IClientConfig clientConfig, String name) {
    String proxyName = null;
    boolean useGrayUrl = false;
    ZuulRoute route = null;

    if (RouteConfig.zuulRoutes.containsKey(name)) {
      proxyName = name;
    } else if (RouteConfig.extraConfig.containsKey(name)) {
      proxyName = RouteConfig.extraConfig.get(name);
      useGrayUrl = true;
    }
    if (proxyName != null) {
      route = RouteConfig.zuulRoutes.get(proxyName);
      clientConfig.set(CommonClientConfigKey.ListOfServers, useGrayUrl ? route.getGrayUrl() : route.getUrl());
      clientConfig.set(CommonClientConfigKey.ReadTimeout, route.getReadTimeout());
    }
    return route;
  }

}
