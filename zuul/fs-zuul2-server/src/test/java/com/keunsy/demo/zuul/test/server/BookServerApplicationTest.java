package com.keunsy.demo.zuul.test.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class BookServerApplicationTest {

  @RequestMapping(value = "/getBook")
  public String getBook() {
    return "book";
  }

  @RequestMapping(value = "/")
  public String index() {
    return "ok了";
  }

  @RequestMapping(value = "/borrow")
  public String borrow(@RequestParam String name) {
    return name;
  }

  @RequestMapping(value = "/demo")
  public String demo() {
    return "demo";
  }

  @RequestMapping(value = "/demo/test")
  public String demoTest() {
    return "demoTest";
  }

  @RequestMapping(value = "/testerror")
  public void error() {
    int i = 1 / 0;
  }

  public static void main(String[] args) {
    new SpringApplicationBuilder(BookServerApplicationTest.class).properties("server.port=8061", "spring.application.name=book").run(args);
  }
}