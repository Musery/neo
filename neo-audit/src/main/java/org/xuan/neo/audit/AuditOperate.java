package org.xuan.neo.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 审计日志注解 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditOperate {
  /**
   * 操作类型 `数组原因是为了配合dynamicOperation进行动态操作类型的举证`
   *
   * @return
   */
  AuditOperation[] operation();

  /**
   * 只支持纯表达式 默认使用第一个
   *
   * @return
   */
  String dynamicOperation() default "#_OPERATION[0].zh";
  /**
   * 审计内容模板(支持SpEL) 注意: 业务对象会被修改导致字段变动
   *
   * @return
   */
  String content();
}
