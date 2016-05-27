package com.James.soa_agent;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;

/**
 * Created by James on 16/5/25.
 * 通过agent的transform方式注入
 * javasist修改的字节码
 */
public class Agent_Transformer implements ClassFileTransformer {

    private Agent_Advice_Class agent_Advice_Class = null;
    private Class<?> reload_class = null;
    private ClassPool class_pool = ClassPool.getDefault();

    public Agent_Transformer(Agent_Advice_Class agent_Advice_Claz, Class<?> reload_class) {
        this.agent_Advice_Class = agent_Advice_Claz;
        this.reload_class = reload_class;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals(agent_Advice_Class.getClass_name().replace(".", "/"))) {
            return classfileBuffer;
        }
        String class_name = agent_Advice_Class.getClass_name();
        try {
            class_pool.insertClassPath(new ClassClassPath(reload_class));// war包下使用必须
            CtClass ct_class = class_pool.get(class_name);
            ct_class.defrost();

            // 注入属性
            Set<Agent_Advice_Field> fields = agent_Advice_Class.getFields();
            for (Agent_Advice_Field Agent_Advice_Field : fields) {
                String insertAfter = new StringBuilder("this.").append(Agent_Advice_Field.getField_name()).append(" = ").append(Agent_Advice_Field.getValue()).toString();
                CtConstructor[] constructors = ct_class.getConstructors();
                for (CtConstructor ctConstructor : constructors) {
                    System.out.println(insertAfter);
                    ctConstructor.insertAfter(insertAfter);
                }
            }

            // 注入方法
            Set<Agent_Advice_Method> methods = agent_Advice_Class.getMethods();
            for (Agent_Advice_Method agent_advice_method : methods) {
                CtMethod[] declaredMethods = ct_class.getDeclaredMethods(agent_advice_method.getMethod_name());
                for (CtMethod ct_method : declaredMethods) {
                    CtClass[] parameterTypes = ct_method.getParameterTypes();
                    StringBuilder stringbuilder = new StringBuilder();
                    for (CtClass type : parameterTypes) {
                        stringbuilder.append(type.getName()).append(" ");
                    }
                    if (stringbuilder.toString().trim().equals(agent_advice_method.getMethod_parameters())) {
                        String long_local_variable = agent_advice_method.getLong_local_variable();
                        if (long_local_variable != null) {
                            ct_method.addLocalVariable(long_local_variable, CtClass.longType);
                        }
                        String insert_before = agent_advice_method.getInsert_before();
                        if (insert_before != null) {
                            ct_method.insertBefore(insert_before);
                        }
                        String insert_after = agent_advice_method.getInsert_after();
                        if (insert_after != null) {
                            ct_method.insertAfter(insert_after);
                        }
                        String add_catch = agent_advice_method.getAdd_catch();
                        if (add_catch != null) {
                            CtClass ctClass = class_pool.get("java.lang.Throwable");
                            ct_method.addCatch(add_catch, ctClass);
                        }
                        String set_exception_types = agent_advice_method.getSet_exception_types();
                        if (set_exception_types != null) {
                            String[] split = set_exception_types.trim().split(",");
                            CtClass[] types = new CtClass[split.length];
                            for (int i = 0; i < split.length; i++) {
                                types[i] = class_pool.get(split[i]);
                            }
                            ct_method.setExceptionTypes(types);
                        }

                    }
                }
            }

            return ct_class.toBytecode();
        } catch (Throwable e) {
            System.out.println("transform 字节码文件错误 :");
            System.out.println("1:请检查是不是javassist jar包冲突,例如hibernate等都会包含一个版本较低的javassist依赖 (使用maven可以尝试将infogen的jar包放到dependencies列表的最上方)");
            System.out.println("2:如有需要将返回值($_)作为参数调用回调方法,返回值必须是对象类型,否则会报类似");
            System.out.println("javassist.CannotCompileException: [source error] insert_after_call_back(boolean) not found in Handle_Execution的错误");
            e.printStackTrace();
            return classfileBuffer;
        }

    }
}
