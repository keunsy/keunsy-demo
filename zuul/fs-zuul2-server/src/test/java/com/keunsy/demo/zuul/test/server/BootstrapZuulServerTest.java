package com.keunsy.demo.zuul.test.server;

import com.keunsy.demo.zuul.ZuulServer;

/**
 * Created on 2019/4/22.
 */
public class BootstrapZuulServerTest {

  static {
    System.setProperty("process.profile", "firstshare");
  }

  public static void main(String[] args) {
    ZuulServer zuulServer = new ZuulServer();
    zuulServer.bootstrap();
  }
}
