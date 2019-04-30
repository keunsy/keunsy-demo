package com.keunsy.demo.zuul;

import com.google.common.base.CharMatcher;

/**
 * Created on 2019/4/18.
 */
public class Temp {
  public static void main(String[] args) {

    System.out.println(CharMatcher.anyOf("[a-z|A-Z|\\-]").matchesAllOf("fsfes-efs"));
    System.out.println(CharMatcher.anyOf("a-z").matchesAllOf("fsfes"));


  }
}
