package com.James.basic.Exception;

import java.io.IOException;

import com.James.basic.Enum.BasicCode;


/**
 * Created by James on 16/6/8.
 */
public class Method_Not_Found_Exception extends IOException {
  private Integer code;
  private String message;

  public Method_Not_Found_Exception() {
    super();
    this.code = BasicCode.service_not_found.code;
    this.message = BasicCode.service_not_found.name();
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
    return "code:".concat(code.toString()).concat(" - message:".concat(message));
  }
}
