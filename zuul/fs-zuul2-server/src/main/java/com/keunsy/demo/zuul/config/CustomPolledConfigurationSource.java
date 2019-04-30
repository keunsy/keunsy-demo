package com.keunsy.demo.zuul.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;

import java.util.Map;
import java.util.Queue;

/**
 * Created on 2019/4/28.
 */
public class CustomPolledConfigurationSource implements PolledConfigurationSource {

  @Override
  public PollResult poll(boolean initial, Object checkPoint) {
    if (RouteConfig.dynamicQueue.isEmpty()) {
      return PollResult.createFull(null);
    }
    Queue<Map<String, Object>> tmp = RouteConfig.dynamicQueue;
    RouteConfig.dynamicQueue = Queues.newLinkedBlockingQueue();

    Map<String, Object> all = Maps.newHashMap();
    Map<String, Object> map = tmp.poll();
    while (map != null) {
      all.putAll(map);
      map = tmp.poll();
    }
    return PollResult.createFull(all);
  }
}
