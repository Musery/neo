package org.xuan.neo.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** 审计日志操作切面(接口化) */
@Slf4j
@Aspect
@Component
public class AuditOperateAspect {

  @Autowired private AuditTemplateExecute auditTemplateExecute;

  /** 配置注解切面 */
  @Pointcut("@annotation(org.xuan.neo.audit.AuditOperate)")
  public void auditPoint() {}

  @Around("auditPoint()")
  public Object around(ProceedingJoinPoint point) {
    return auditTemplateExecute.execute(point);
  }
}
