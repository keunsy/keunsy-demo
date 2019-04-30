package com.keunsy.demo.zuul.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created on 2019/4/22.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZuulRoute {
  private String proxyName;//代理名称
  private String path;//路径，AntPath规则，如为空，则默认"/proxyName/**"
  private String url;
  private boolean stripPrefix;//是否去除path规则前缀路径，默认去除
  private int readTimeout;//读取超时时间（默认30s），单位毫秒,动态配置
  private String loadBalanceRule;//负载均衡规则,不可动态配置，默认
  private int maxConnections;//最大的连接数
  //特殊定制
  //灰度企业
  private List<String> grayValues;//灰度头信息，值
  private String grayUrl;//仅在grayHeaderKey以及grayValues时使用
  private String grayHeaderKey;//灰度头信息的key,如"fs-ei",符合规则的值，将会转向grayUrl
  //path模块
  private List<String> moduleValues;//
  private String modulePrefix;//
  private String moduleUrl;//


}
