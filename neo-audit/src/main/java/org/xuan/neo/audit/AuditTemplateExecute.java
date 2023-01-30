package org.xuan.neo.audit;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 审计日志模板处理 1. 缓存(类,方法,参数和模板信息) 2. 动态解析SpEL */
@Slf4j
@Component
public class AuditTemplateExecute implements BeanPostProcessor {

  private ConcurrentHashMap<Method, AuditTemplate> auditCache;
  private SpelExpressionParser parser;

  private static final String EXPRESSION_PREFIX = "${";

  private static final String EXPRESSION_SUFFIX = "}";

  public AuditTemplateExecute() {
    this.auditCache = new ConcurrentHashMap<>(256);
    this.parser = new SpelExpressionParser();
  }

  /**
   * 获取日志
   *
   * @param point
   * @return
   */
  public Object execute(ProceedingJoinPoint point) {
    Method method = ((MethodSignature) point.getSignature()).getMethod();
    // 1. 获取日志模板
    AuditTemplate auditTemplate =
        this.auditCache.computeIfAbsent(
            method,
            k -> {
              // 解析method的入参信息
              Map<String, Integer> paramIndex = new HashMap<>();
              if (method.getParameterCount() > 0) {
                int index = 0;
                for (Parameter parameter : method.getParameters()) {
                  paramIndex.put(parameter.getName(), index++);
                }
              }
              AuditOperate auditOperate = method.getAnnotation(AuditOperate.class);
              AuditTemplate template =
                  new AuditTemplate()
                      .setOperations(auditOperate.operation())
                      .setContentExp(
                          this.parser.parseExpression(
                              auditOperate.content(),
                              new TemplateParserContext(EXPRESSION_PREFIX, EXPRESSION_SUFFIX)))
                      .setOperateExp(this.parser.parseExpression(auditOperate.dynamicOperation()))
                      .setParamIndex(paramIndex);

              return template;
            });
    // 2. 设置`参数`上下文
    if (auditTemplate.getParamIndex().size() > 0) {
      for (Map.Entry<String, Integer> index : auditTemplate.getParamIndex().entrySet()) {
        if (point.getArgs().length > index.getValue()) {
          // 需要考虑业务对象会被修改的问题
          AuditOperateContext.push(index.getKey(), point.getArgs()[index.getValue()]);
        }
      }
    }
    AuditOperateContext.push(AuditOperateConst._OPERATION, auditTemplate.getOperations());
    // 审计日志信息
    AuditOperateLog operateLog =
        new AuditOperateLog()
            .setTime(LocalDateTime.now())
            .setAuditOperator(
                (String)
                    AuditOperateContext.pop()
                        .getOrDefault(AuditOperateConst.AUDIT_OPERATOR, "anonymous"));
    try {
      // 3. 执行业务(业务代码中可以AuditOperateContext设置其他上下文)
      Object result = point.proceed();
      // 4. 设置返回上下文
      AuditOperateContext.push(AuditOperateConst._RETURN, result);
      operateLog
          .setResult("成功")
          .setContent(auditTemplate.contentExp.getValue(AuditOperateContext.build(), String.class))
          .setOperation(
              auditTemplate.getOperateExp().getValue(AuditOperateContext.build(), String.class));
      return result;
    } catch (Throwable e) {
      // 5. 设置抛出异常上下文
      AuditOperateContext.push(AuditOperateConst._THROWABLE, e);
      operateLog
          .setResult("失败")
          .setContent(auditTemplate.contentExp.getValue(AuditOperateContext.build(), String.class))
          .setOperation(
              auditTemplate.getOperateExp().getValue(AuditOperateContext.build(), String.class))
          .setThrowable(e.getMessage());
      throw new RuntimeException(e);
    } finally {
      log.info(operateLog.toString());
      AuditOperateContext.clear();
    }
  }

  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof IParseFunction) {
      AuditOperateContext.register((IParseFunction) bean);
    }
    return bean;
  }

  @Data
  @Accessors(chain = true)
  static class AuditTemplate {

    private AuditOperation[] operations;
    private Expression operateExp;
    private Expression contentExp;

    /** 参数索引 */
    private Map<String, Integer> paramIndex;
  }
}
