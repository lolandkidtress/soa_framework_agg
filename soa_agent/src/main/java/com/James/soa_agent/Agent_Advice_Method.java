package com.James.soa_agent;

import java.io.Serializable;

/**
 * Created by James on 16/5/25.
 * 织入的method对象
 */
public class Agent_Advice_Method implements Serializable {
    private static final long serialVersionUID = -57940195739144408L;
    private String method_name;
    private String method_parameters;
    private String long_local_variable;
    private String insert_before;
    private String insert_after;
    private String add_catch;
    private String set_exception_types;

    public static void main(String[] args) {

    }

    public String getMethod_parameters() {
        return method_parameters;
    }

    public void setMethod_parameters(String method_parameters) {
        this.method_parameters = method_parameters;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public String getLong_local_variable() {
        return long_local_variable;
    }

    public void setLong_local_variable(String long_local_variable) {
        this.long_local_variable = long_local_variable;
    }

    public String getInsert_before() {
        return insert_before;
    }

    public void setInsert_before(String insert_before) {
        this.insert_before = insert_before;
    }

    public String getInsert_after() {
        return insert_after;
    }

    public void setInsert_after(String insert_after) {
        this.insert_after = insert_after;
    }

    public String getAdd_catch() {
        return add_catch;
    }

    public void setAdd_catch(String add_catch) {
        this.add_catch = add_catch;
    }

    public String getSet_exception_types() {
        return set_exception_types;
    }

    public void setSet_exception_types(String set_exception_types) {
        this.set_exception_types = set_exception_types;
    }

}
