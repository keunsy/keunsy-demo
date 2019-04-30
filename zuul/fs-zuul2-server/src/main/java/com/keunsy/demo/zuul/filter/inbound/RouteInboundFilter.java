package com.keunsy.demo.zuul.filter.inbound;

import com.google.common.base.Strings;
import com.keunsy.demo.zuul.config.RouteConfig;
import com.keunsy.demo.zuul.config.ZuulRoute;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpInboundSyncFilter;
import com.netflix.zuul.message.http.HttpRequestMessage;
import com.netflix.zuul.netty.filter.ZuulEndPointRunner;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义动态路由过滤器（无阻塞使用sync过滤器）
 * Created on 2019/4/22.
 */
@Slf4j
public class RouteInboundFilter extends HttpInboundSyncFilter {
  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public boolean shouldFilter(HttpRequestMessage httpRequestMessage) {
    return true;
  }

  @Override
  public HttpRequestMessage apply(HttpRequestMessage request) {
    SessionContext context = request.getContext();
    context.setEndpoint(ZuulEndPointRunner.PROXY_ENDPOINT_FILTER_NAME);
    //todo 处理trace 1.参数获取 2.header获取 3.
    String path = request.getPath();
    String proxyName = RouteConfig.getMatchedProxyName(path);
    ZuulRoute route = RouteConfig.zuulRoutes.get(proxyName);
    if (route != null) {
      setRouteVIP(request, context, path, proxyName, route);
      rewritePath(request, path, route);
    } else {
      log.warn("request path {} is not configured", path);
    }
    return request;
  }

  private void setRouteVIP(HttpRequestMessage request, SessionContext context, String path, String proxyName, ZuulRoute route) {
    String routeVIP = proxyName;
    if (!Strings.isNullOrEmpty(route.getGrayUrl())) {
      String grayHeaderKey = route.getGrayHeaderKey();
      String grayValue = request.getHeaders().getFirst(grayHeaderKey);
      if (route.getGrayValues() != null && !Strings.isNullOrEmpty(grayValue) && route.getGrayValues().contains(grayValue)) {
        routeVIP = proxyName + "\u0003\u0004" + "gray";//用于区别
        RouteConfig.extraConfig.put(routeVIP, proxyName);
        log.info("request path {} match proxy name {}, set gray route vip {}, gray url {}", path, proxyName, routeVIP, route.getGrayUrl());
      }
    }
    context.setRouteVIP(routeVIP);
  }

  /**
   * 重写请求path，支持无contextPath的情况
   *
   * @param request
   * @param path
   * @param route
   */
  private void rewritePath(HttpRequestMessage request, String path, ZuulRoute route) {
    if (route.isStripPrefix()) {
      int index = route.getPath().indexOf("*") - 1;
      if (index > 0) {
        String routePrefix = route.getPath().substring(0, index);
        String targetPath = path.replaceFirst(routePrefix, "");
        request.setPath(targetPath.isEmpty() ? "/" : targetPath);
      }
    }
  }
}
