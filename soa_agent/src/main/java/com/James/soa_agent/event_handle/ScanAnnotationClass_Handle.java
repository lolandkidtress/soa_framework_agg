package com.James.soa_agent.event_handle;

import java.lang.annotation.Annotation;
import java.util.Set;


/**
 * Created by James on 16/5/26.
 */
public abstract interface ScanAnnotationClass_Handle {
    public abstract Object attach_annotation(Set<Class<?>> Classes,Class<? extends Annotation> annotationClass);
}
