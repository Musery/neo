package org.xuan.neo.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

/** 审计日志上下文 */
@Slf4j
public class AuditOperateContext extends StandardEvaluationContext {

  private static final ThreadLocal<Map<String, Object>> threadLocalParams = new ThreadLocal<>();

  private static final Map<String, IParseFunction> parsePool = new HashMap<>(16);

  private AuditOperateContext() {
    this.setVariables(pop());
    try {
      this.registerFunction(
          AuditOperateConst._PARSE,
          AuditOperateContext.class.getDeclaredMethod("parse", String.class, String.class));
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private static Map<String, Object> get() {
    Map<String, Object> map = threadLocalParams.get();
    if (null == map) {
      map = new HashMap<>(16);
      threadLocalParams.set(map);
    }
    return map;
  }

  public static void register(IParseFunction function) {
    parsePool.put(function.name(), function);
  }

  public static String parse(String name, String value) {
    return parsePool.get(name).execute(value);
  }

  public static void push(String key, Object value) {
    get().put(key, value);
  }

  public static Map<String, Object> pop() {
    return get();
  }

  public static void clear() {
    get().clear();
  }

  public static AuditOperateContext build() {
    return new AuditOperateContext();
  }
}
