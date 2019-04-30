package com.keunsy.demo.zuul.netty;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.netty.common.accesslog.AccessLogPublisher;
import com.netflix.netty.common.channel.config.ChannelConfig;
import com.netflix.netty.common.channel.config.CommonChannelConfigKeys;
import com.netflix.netty.common.metrics.EventLoopGroupMetrics;
import com.netflix.netty.common.proxyprotocol.StripUntrustedProxyHeadersHandler;
import com.netflix.netty.common.status.ServerStatusManager;
import com.netflix.spectator.api.Registry;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.FilterUsageNotifier;
import com.netflix.zuul.RequestCompleteHandler;
import com.netflix.zuul.context.SessionContextDecorator;
import com.netflix.zuul.netty.server.BaseServerStartup;
import com.netflix.zuul.netty.server.DirectMemoryMonitor;
import com.netflix.zuul.netty.server.ZuulServerChannelInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomServerStartup extends BaseServerStartup {

  @Inject
  public CustomServerStartup(ServerStatusManager serverStatusManager,
                             FilterLoader filterLoader,
                             SessionContextDecorator sessionCtxDecorator,
                             FilterUsageNotifier usageNotifier,
                             RequestCompleteHandler reqCompleteHandler,
                             Registry registry,
                             DirectMemoryMonitor directMemoryMonitor,
                             EventLoopGroupMetrics eventLoopGroupMetrics,
                             EurekaClient discoveryClient,
                             ApplicationInfoManager applicationInfoManager,
                             AccessLogPublisher accessLogPublisher) {
    super(serverStatusManager, filterLoader, sessionCtxDecorator, usageNotifier, reqCompleteHandler, registry, directMemoryMonitor, eventLoopGroupMetrics, discoveryClient, applicationInfoManager, accessLogPublisher);
  }

  @Override
  protected Map<Integer, ChannelInitializer> choosePortsAndChannels(ChannelGroup clientChannels, ChannelConfig channelDependencies) {
    Map<Integer, ChannelInitializer> portsToChannels = new HashMap<>();

    int port = 7000;
    ChannelConfig channelConfig = BaseServerStartup.defaultChannelConfig();
    //config ,just for http  fixme remove useless
    channelConfig.set(CommonChannelConfigKeys.allowProxyHeadersWhen, StripUntrustedProxyHeadersHandler.AllowWhen.ALWAYS);//开放代理头（信任XFF）
    channelConfig.set(CommonChannelConfigKeys.preferProxyProtocolForClientIp, false);
    channelConfig.set(CommonChannelConfigKeys.isSSlFromIntermediary, false);
    channelConfig.set(CommonChannelConfigKeys.withProxyProtocol, false);
    channelConfig.set(CommonChannelConfigKeys.httpRequestReadTimeout, 60000);//defaul is 5000
    // to adjust
    //    channelConfig.set(CommonChannelConfigKeys.maxConnections, 20000);
    //    channelConfig.set(CommonChannelConfigKeys.maxRequestsPerConnection, 20000);
    //    channelConfig.set(CommonChannelConfigKeys.maxRequestsPerConnectionInBrownout, 100);

    portsToChannels.put(port, new ZuulServerChannelInitializer(port, channelConfig, channelDependencies, clientChannels));
    logPortConfigured(port, null);
    log.info("zuul server port is {}", port);

    return portsToChannels;
  }

}
