package com.keunsy.demo.protostuff;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtil {

  private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

  private static <T> Schema<T> getSchema(Class<T> clazz) {
    Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
    if (schema == null) {
      schema = RuntimeSchema.getSchema(clazz);
      if (schema != null) {
        cachedSchema.put(clazz, schema);
      }
    }
    return schema;
  }

  /**
   * 序列化
   *
   * @param obj 序列化对象
   * @return 序列化后的byte[]值
   */
  public static <T> byte[] serializer(T obj) {
    Class<T> clazz = (Class<T>) obj.getClass();
    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    try {
      Schema<T> schema = getSchema(clazz);
      return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    } finally {
      buffer.clear();
    }
  }

  /**
   * 反序列化
   *
   * @param data  序列化后的byte[]值
   * @param clazz 反序列化后的对象
   * @return 返回的对象
   */
  public static <T> T deserializer(byte[] data, Class<T> clazz) {
    try {
      T obj = clazz.newInstance();
      Schema<T> schema = getSchema(clazz);
      ProtostuffIOUtil.mergeFrom(data, obj, schema);
      return obj;
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }


  public static <T> byte[] serializeList(List<T> objList) {
    if (objList == null || objList.isEmpty()) {
      throw new RuntimeException("序列化对象列表(" + objList + ")参数异常!");
    }
    Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
    LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
    byte[] protostuff;
    ByteArrayOutputStream bos = null;
    try {
      bos = new ByteArrayOutputStream();
      ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
      protostuff = bos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("序列化对象列表(" + objList + ")发生异常!", e);
    } finally {
      buffer.clear();
      try {
        if (bos != null) {
          bos.close();
        }
      } catch (IOException ignore) {

      }
    }
    return protostuff;
  }


  public static <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
    if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
      throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
    }
    Schema<T> schema = RuntimeSchema.getSchema(targetClass);
    List<T> result;
    try {
      result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
    } catch (IOException e) {
      throw new RuntimeException("反序列化对象列表发生异常!", e);
    }
    return result;
  }

  public static Map<String, Object> objectToMap(Object obj) {
    if (obj == null) {
      return null;
    }
    Map<String, Object> map = new HashMap<>();
    Field[] declaredFields = obj.getClass().getDeclaredFields();
    for (Field field : declaredFields) {
      field.setAccessible(true);
      try {
        map.put(field.getName(), field.get(obj));
      } catch (IllegalAccessException ignore) {

      }
    }
    return map;
  }


  public static void main(String[] args) {
    ServiceLog build = ServiceLog.builder().app("test").ms10(10).build();
    byte[] serializer = serializer(build);
    ServiceLog deserializer = deserializer(serializer, ServiceLog.class);

    System.out.println(deserializer);
    assert deserializer.getApp().equals("test");
    assert deserializer.getMs10() == 10;

    System.out.println(objectToMap(deserializer));

  }

}