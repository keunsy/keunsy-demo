package com.keunsy.demo.zuul.test.server;

import com.keunsy.demo.zuul.config.RouteConfig;
import com.keunsy.demo.zuul.config.ZuulRoute;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created on 2019/4/22.
 */
public class GetRouteVIPTest {

  @Test
  public void test() {
    Map<String, ZuulRoute> zuulRoutes = RouteConfig.zuulRoutes;

    ZuulRoute route1 = new ZuulRoute();
    route1.setPath("/test/**");
    zuulRoutes.put("short", route1);

    ZuulRoute route2 = new ZuulRoute();
    route2.setPath("/test/temp/**");
    zuulRoutes.put("media", route2);

    ZuulRoute route3 = new ZuulRoute();
    route3.setPath("/test/temp/haha/**");
    zuulRoutes.put("long", route3);

    String routeVIP = RouteConfig.getMatchedProxyName("/test/temp/1");
    Assert.assertEquals(routeVIP, "media");

    routeVIP = RouteConfig.getMatchedProxyName("/test/temp/haha/2");
    Assert.assertEquals(routeVIP, "long");
  }
}
