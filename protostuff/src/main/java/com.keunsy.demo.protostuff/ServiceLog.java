package com.keunsy.demo.protostuff;

import io.protostuff.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2019/5/14.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceLog {
  @Tag(1)
  private String hostname;
  @Tag(2)
  private String calle;
  @Tag(3)
  private String app;
  @Tag(4)
  private long stamp;
  @Tag(5)
  private String module;
  @Tag(6)
  private String method;
  @Tag(7)
  private int totalCount;
  @Tag(8)
  private int failCount;
  @Tag(9)
  private int slowCount;
  @Tag(10)
  private int totalCost;
  @Tag(11)
  private int ms1;
  @Tag(12)
  private int ms10;
  @Tag(13)
  private int ms100;
  @Tag(14)
  private int ms1000;
  @Tag(15)
  private int ms10000;
  @Tag(16)
  private int msMore;

}
