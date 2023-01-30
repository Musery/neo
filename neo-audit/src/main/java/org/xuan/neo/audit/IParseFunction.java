package org.xuan.neo.audit;

/** 审计日志处理扩展接口 主要用于扩充日志解析过程操作 */
public interface IParseFunction {

  default String execute(String value) {
    return "";
  }

  String name();
}
