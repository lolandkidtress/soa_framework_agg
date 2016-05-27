package com.James.soa_agent;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by James on 16/5/25.
 * 织入时的中间class对象
 */
public class Agent_Advice_Class implements Serializable {
    private static final long serialVersionUID = -8990106273196548492L;
    private String class_name;
    private Set<Agent_Advice_Method> methods = new HashSet<>();
    private Set<Agent_Advice_Field> fields = new HashSet<>();

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public Set<Agent_Advice_Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Agent_Advice_Method> methods) {
        this.methods = methods;
    }

    public Set<Agent_Advice_Field> getFields() {
        return fields;
    }

    public void setFields(Set<Agent_Advice_Field> fields) {
        this.fields = fields;
    }

}
