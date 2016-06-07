package com.James.http_exception;

import java.io.IOException;


/**
 * Created by James on 16/6/7.
 */
public class Http_Fail_Exception  extends IOException {
  private static final long serialVersionUID = 153970941852883330L;
  private Integer code;
  private String message;

  public Http_Fail_Exception(Integer code, String message) {
    super();
    this.code = code;
    this.message = message;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Throwable#toString()
   */
  @Override
  public String toString() {
    return "HTTP调用时response.isSuccessful()出现为false的错误 code:".concat(code.toString()).concat(" - message:".concat(message));
  }
}
