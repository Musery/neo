package org.xuan.neo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 返回结果接口
 *
 * @author Zhou Changjian
 * @version 1.0
 * @since 2021/5/27
 */
@Data
public class IResult<T> {

  /**
   * 获取返回码
   *
   * @return
   */
  @Schema(description = "返回码，0表示成功")
  private int code;
  /**
   * 获取返回信息
   *
   * @return
   */
  @Schema(description = "返回信息")
  private String msg;

  /**
   * 获取返回数据
   *
   * @return
   */
  @Schema(description = "返回数据")
  private T data;

  private IResult(int code, T data, String msg) {
    this.code = code;
    this.data = data;
    this.msg = msg;
  }

  public static <T> IResult<T> success() {
    return success(null);
  }

  public static <T> IResult<T> success(T data) {
    return new IResult<>(0, data, null);
  }
}
