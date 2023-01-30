package org.xuan.neo.audit.function;

import org.springframework.stereotype.Component;
import org.xuan.neo.audit.IParseFunction;

@Component
public class PhoneParse implements IParseFunction {

  @Override
  public String execute(String value) {
    switch (value) {
      case "110":
        return "匪警(110)";
      case "120":
        return "急救(120)";
      case "119":
        return "火警(119)";
      default:
        return "其他热线(" + value + ")";
    }
  }

  @Override
  public String name() {
    return "phoneParse";
  }
}
