package com.keunsy.demo.zuul.netty;

import com.netflix.spectator.api.Registry;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.origins.OriginManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2019/4/19.
 */
@Singleton
public class CustomNettyOriginManager implements OriginManager<CustomNettyOrigin> {
  private final Registry registry;
  private final ConcurrentHashMap<String, CustomNettyOrigin> originMappings;

  @Inject
  public CustomNettyOriginManager(Registry registry) {
    this.registry = registry;
    this.originMappings = new ConcurrentHashMap<>();
  }

  @Override
  public CustomNettyOrigin getOrigin(String name, String vip, String uri, SessionContext ctx) {
    return originMappings.computeIfAbsent(name, n -> createOrigin(name, vip, uri, false, ctx));
  }

  @Override
  public CustomNettyOrigin createOrigin(String name, String vip, String uri, boolean useFullVipName, SessionContext ctx) {
    return new CustomNettyOrigin(name, vip, registry);
  }

}
