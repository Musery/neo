package org.xuan.neo.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 日志操作类型 */
@Getter
@AllArgsConstructor
public enum AuditOperation {
  select("查询", "Select"),
  insert("新增", "Insert"),
  delete("删除", "Delete"),
  update("更新", "Update"),
  ;

  private String zh;

  private String en;
}
