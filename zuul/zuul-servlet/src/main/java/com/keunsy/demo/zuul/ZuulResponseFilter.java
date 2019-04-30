package com.keunsy.demo.zuul;

import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpOutboundSyncFilter;
import com.netflix.zuul.message.http.HttpResponseMessage;

public class ZuulResponseFilter extends HttpOutboundSyncFilter {

  @Override
  public int filterOrder() {
    return 999;
  }

  @Override
  public boolean shouldFilter(HttpResponseMessage request) {
    return true;
  }

  @Override
  public boolean needsBodyBuffered(HttpResponseMessage input) {
    return true;
  }

  @Override
  public HttpResponseMessage apply(HttpResponseMessage response) {
    SessionContext context = response.getContext();
    System.out.println("===========================" + context.get("traceContext"));

    System.out.println(response.getStatus());

    System.out.println("getError ===" + context.getError());
    if (context.getError() != null) {
      System.out.println("getError getMessage===" + context.getError().getMessage());
    }

    System.out.println("getStartTime==" + context.getTimings().getRequest().getStartTime());
    System.out.println("getDuration==" + context.getTimings().getRequest().getDuration());
    System.out.println("getOriginReportedDuration ===" + context.getOriginReportedDuration());

    System.out.println("getError ===" + context.getError());
    System.out.println("getClientIp ===" + response.getInboundRequest().getClientIp());
    System.out.println("reconstructURI ===" + response.getInboundRequest().reconstructURI());
    System.out.println("getQueryParams ===" + response.getInboundRequest().getQueryParams().toString());
    System.out.println("getInfoForLogging ===" + response.getInboundResponse().getInfoForLogging());
    System.out.println("getBodyAsText ===" + response.getBodyAsText());
    System.out.println("getBodyAsText ===" + response.getBody());
    System.out.println("getInfoForLogging ===" + response.getInfoForLogging());

    System.out.println("getStaticResponsegetBodyAsText ===" + context.getStaticResponse());
    System.out.println("-------------------");
    context.getFilterErrors().forEach(filterError -> {
      System.out.println(filterError.toString());
      System.out.println(filterError.getException().getMessage());
    });
    System.out.println("------------------------- ");
    //    if (context.getError() != null) {
    //      System.out.println("error ==" + context.getError().getMessage());
    //      System.out.println("cause msg ==" + context.getError().getStackTrace());
    //    }
    System.out.println(context.getTimings().getRequestProxy().getDuration());
    System.out.println("===========================");

    return response;
  }
}