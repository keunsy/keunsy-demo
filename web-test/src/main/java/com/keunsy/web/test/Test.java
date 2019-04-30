package com.keunsy.web.test;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created on 2019/4/22.
 */
@Service
public class Test {

  @PostConstruct
  void init() {
    System.out.println("!!!!!!!!!");
  }
}
