package org.xuan.neo.audit;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AuditOperateLog {
  private String auditOperator;

  private String content;

  private String operation;

  private String result;

  private String throwable;

  private LocalDateTime time;

  @Override
  public String toString() {
    return "操作员: "
        + auditOperator
        + " 行为: "
        + operation
        + " 详情: "
        + content
        + " 操作结果: "
        + result
        + " 操作时间: "
        + time
        + (null != throwable ? " 失败原因: " + throwable : "");
  }
}
