package com.James.Exception;

import java.io.IOException;

import com.James.basic.Enum.Code;


/**
 * Created by James on 16/6/8.
 */
public class avroConnectionException extends IOException {
  private Integer code;
  private String message;

  public avroConnectionException() {
    super();
    this.code = Code.avro_Connection_not_available.code;
    this.message = Code.avro_Connection_not_available.name();
  }

  public avroConnectionException(Integer code,String message) {
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
    return "code:".concat(code.toString()).concat(" - message:".concat(message));
  }
}
