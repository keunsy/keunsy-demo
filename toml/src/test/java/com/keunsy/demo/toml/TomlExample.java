package com.keunsy.demo.toml;

import com.moandjiezana.toml.Toml;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/4/17.
 */
public class TomlExample {

  @Test
  public void test1() {
    Toml toml = new Toml().read("a=1");
    long a = toml.getLong("a");
    Assert.assertEquals(a, 1);
  }

  @Test
  public void testToMap() {
    Map<String, Object> map = new Toml().read("a=1").toMap();
    assert map.get("a").equals(1L);

    Map<String, Object> map2 = new Toml().read("[database]\n" + "  ports = [ 8001, 8001, 8002 ]\n" + "  enabled = true").toMap();
    Map database = (Map) map2.get("database");
    assert database.get("enabled").equals(true);
  }

  @Test
  public void testToClass() {

    String str = "name = \"Mwanji Ezana\"\n" + "\n" + "[address]\n" + "  street = \"123 A Street\"\n" + "  city = \"AnyVille\"\n" + "  \n" + "[contacts]\n" +
      "  \"email address\" = \"me@example.com\"";
    User user = new Toml().read(new ByteArrayInputStream(str.getBytes())).to(User.class);

    User user2 = new Toml().read(new File("src/test/resources/testToClass.toml")).to(User.class);

    assert user.name.equals(user2.name);
    assert user.address.street.equals(user2.address.street);
    assert user.contacts.get("\"email address\"").equals(user2.contacts.get("\"email address\""));
    assert user.address.city.equals(user2.address.city);
  }


  @Test
  public void testAllKinds() {
    Toml toml = new Toml().read(new File("src/test/resources/testAllKinds.toml"));

    String title = toml.getString("title");
    String subTitle = toml.getString("\"sub title\"");


    Toml database = toml.getTable("database");

    String string = database.getString("\"a.test\"");

    List<Toml> rules = database.getTables("rules");

    Boolean enabled = toml.getBoolean("database.enabled");
    List<Long> ports = toml.getList("database.ports");
    String password = toml.getString("database.credentials.password");

    Toml servers = toml.getTable("servers");
    String cluster = servers.getString("cluster"); // navigation is relative to current Toml instance
    String ip = servers.getString("alpha.ip");

    Toml network1 = toml.getTable("networks[0]");
    String network2Name = toml.getString("networks[1].name"); // "Level 2"
    List<Toml> network3Operators = toml.getTables("networks[2].operators");
    String network3Operator2Location = toml.getString("networks[2].operators[1].location"); // "Paris"

  }

  @Test
  public void testSpecial() {
    Toml toml = new Toml().read(new File("src/test/resources/testSpecial.toml"));
    assert toml.getString("path").equals("/book/**");
  }


  class Address {
    String street;
    String city;
  }


  class User {
    String name;
    Address address;
    Map<String, Object> contacts;
  }


  class Temp {
    String a;
  }

}
