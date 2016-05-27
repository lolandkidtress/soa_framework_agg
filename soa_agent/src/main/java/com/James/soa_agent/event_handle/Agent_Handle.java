package com.James.soa_agent.event_handle;

import com.James.soa_agent.Agent_Advice_Method;

import java.lang.reflect.Method;

/**
 * Created by James on 16/5/26.
 */
public abstract class Agent_Handle {
    public abstract Agent_Advice_Method attach_method(String class_name, Method method);
}
