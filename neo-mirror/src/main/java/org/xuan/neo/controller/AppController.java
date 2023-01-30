package org.xuan.neo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xuan.neo.audit.AuditOperate;
import org.xuan.neo.audit.AuditOperation;
import org.xuan.neo.response.IResult;

@RestController
public class AppController {

  @AuditOperate(
      operation = {AuditOperation.select},
      content = "呼叫${#phone}, 回应信息: ${#_RETURN.data}")
  @GetMapping("/call")
  public IResult<String> call(String phone) {
    return IResult.success("对不起, 对方正忙, 请稍后再拨");
  }

  @AuditOperate(
      operation = {AuditOperation.select, AuditOperation.update},
      dynamicOperation = "#type>0?#_OPERATION[0].zh:#_OPERATION[1].zh",
      content = "呼叫${#_PARSE('phoneParse',#phone)}, 回应信息: ${#_RETURN.data}")
  @GetMapping("/callWithFunction")
  public IResult<String> callWithFunction(String phone, int type) {
    return IResult.success("对不起, 对方正忙, 请稍后再拨");
  }
}
