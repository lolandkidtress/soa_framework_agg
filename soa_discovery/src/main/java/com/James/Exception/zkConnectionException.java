package com.James.Exception;

import java.io.IOException;

import com.James.basic.Enum.BasicCode;


/**
 * Created by James on 16/6/8.
 */
public class zkConnectionException extends IOException {
  private Integer code;
  private String message;

  public zkConnectionException() {
    super();
    this.code = BasicCode.zk_Connection_Exception.code;
    this.message = BasicCode.zk_Connection_Exception.name();
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
