package com.James.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;


/**
 * Created by James on 16/5/31.
 * 出参
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class OutputParam {
  private String name;

  private String type;
  private Boolean required = true;
  private String default_value;
  private String describe;

  public OutputParam() {
  }

  public OutputParam(String name, String type, Boolean required, String default_value, String describe) {
    super();
    this.name = name;
    this.type = type;
    this.required = required;
    this.default_value = default_value;
    this.describe = describe;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescribe() {
    return describe;
  }

  public void setDescribe(String describe) {
    this.describe = describe;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public String getDefault_value() {
    return default_value;
  }

  public void setDefault_value(String default_value) {
    this.default_value = default_value;
  }
}
