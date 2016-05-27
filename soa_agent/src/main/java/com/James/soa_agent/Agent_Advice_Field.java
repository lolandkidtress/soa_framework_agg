package com.James.soa_agent;

import java.io.Serializable;

/**
 * Created by James on 16/5/25.
 * 织入时的字段
 */
public class Agent_Advice_Field implements Serializable {
    private static final long serialVersionUID = 4011923307122937602L;
    private String field_name;
    private String value;

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}