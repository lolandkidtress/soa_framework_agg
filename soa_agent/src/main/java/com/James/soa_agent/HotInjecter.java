package com.James.soa_agent;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.soa_agent.event_handle.Agent_Handle;

import UtilsTools.NativePath;
import UtilsTools.agentJsonConvert;


/**
 * Created by James on 16/5/26.
 */
public class HotInjecter {

    private static final Log LOGGER = LogFactory.getLog(HotInjecter.class.getName());
    private static class InnerInstance {

        public static final HotInjecter instance = new HotInjecter();
    }

    public static HotInjecter getInstance() {
        return InnerInstance.instance;
    }

    private static final HotInjecter_ClassLoad classLoader = new HotInjecter_ClassLoad(new URL[]{}, null);
    private Method loadAgent = null;
    private Object virtualmachine_instance = null;

    private HotInjecter() {
        try {
            classes = auto_scan_absolute(NativePath.get_class_path());

            String java_home = System.getProperty("java.home");
            System.out.println("java.home  => " + java_home);
            if (System.getProperty("os.name").indexOf("Windows") != -1) {
                if (java_home.contains("jdk")) {
                    java_home = java_home.replace("jre", "").concat("lib/tools.jar");
                } else {
                    java_home = java_home.replace("jre", "jdk").concat("/lib/tools.jar");
                }
            } else {
                java_home = java_home.replace("jre", "").concat("lib/tools.jar");
            }
            LOGGER.info("jdk home dir => " + java_home);
            classLoader.addJar(Paths.get(java_home).toUri().toURL());

            Class<?> clazz = classLoader.loadClass("com.sun.tools.attach.VirtualMachine");
            Method attach = clazz.getMethod("attach", new Class[]{String.class});
            String port = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            virtualmachine_instance = attach.invoke(null, new Object[]{port});
            loadAgent = clazz.getMethod("loadAgent", new Class[]{String.class, String.class});
            // TODO 如果只加载一次应该在使用完成后close
            // detach = clazz.getMethod("detach", new Class[] {});
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("初始化AOP失败-如遇到找不到VirtualMachine类,请检查是否只安装了JRE没有安装JDK" , e);
        }
    }

    ///////////////////////////////////////////////////////////////
    // 面向切面
    private Map<Class<? extends Annotation>, Agent_Handle> advice_methods = new HashMap<>();
    // 依赖注入
    private Map<String, Set<Agent_Advice_Field>> map_autowireds = new HashMap<>();

    public void add_advice_method(Class<? extends Annotation> clazz, Agent_Handle instance) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(instance);
        try {
            advice_methods.put(clazz, instance);
        } catch (java.lang.ClassCastException e) {
            e.printStackTrace();
            LOGGER.error("clazz 不是注解类型", e);
        }
    }

    public void add_autowired_field(String class_name, String field_name, String value) {
        Agent_Advice_Field agent_Advice_Field = new Agent_Advice_Field();
        agent_Advice_Field.setField_name(field_name);
        agent_Advice_Field.setValue(value);

        Set<Agent_Advice_Field> orDefault = map_autowireds.getOrDefault(class_name, new HashSet<Agent_Advice_Field>());
        orDefault.add(agent_Advice_Field);
        map_autowireds.put(class_name, orDefault);
    }

    ////////////////////////////////////////////////////////////////////////////////////
    public void generate_agent_advice_class(Class<?> clazz) {
        String class_name = clazz.getName();
        Set<Agent_Advice_Field> fields = map_autowireds.getOrDefault(class_name, new HashSet<Agent_Advice_Field>());
        Set<Agent_Advice_Method> methods = new HashSet<>();

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            for (Class<? extends Annotation> key : advice_methods.keySet()) {
                Agent_Handle handle = advice_methods.get(key);

                Annotation[] annotations = method.getAnnotationsByType(key);
                if (annotations.length == 0) {
                    continue;
                }

                Agent_Advice_Method attach_method = handle.attach_method(class_name, method);
                if (attach_method == null) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                StringBuilder stringbuilder = new StringBuilder();
                for (Class<?> type : parameterTypes) {
                    stringbuilder.append(type.getName()).append(" ");
                }
                attach_method.setMethod_parameters(stringbuilder.toString().trim());

                methods.add(attach_method);
            }
        }

        if (methods.isEmpty() && fields.isEmpty()) {
            return;
        }

        Agent_Advice_Class advice_class = new Agent_Advice_Class();
        advice_class.setClass_name(class_name);
        advice_class.setMethods(methods);
        advice_class.setFields(fields);

        Agent_Cache.class_advice_map.put(class_name, agentJsonConvert.toJson(advice_class));
    }

    public Boolean isadvice = false;
    private final byte[] advice_lock = new byte[0];

    ///////////////////////////////////////////////////////////////////////
    public void advice() {
        isadvice = true;
        synchronized (advice_lock) {
            classes.forEach((clazz) -> {
                generate_agent_advice_class(clazz);
            });
        }
        try {
            loadAgent.invoke(virtualmachine_instance, new Object[]{Agent_LocalPath.path(), ""});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            LOGGER.error("注入代码失败", e);
        }
    }

    // ///////////////////////////////////////////////////////////component_scan/////////////////////////////////////////////////
    private Set<Class<?>> classes = new LinkedHashSet<>();

    public Set<Class<?>> getClasses() {
        return classes;
    }

    public void addClasses(Class<?> clazz) {
        classes.add(clazz);
    }

    private Pattern anonymous_inner_class_compile = Pattern.compile("^*[$][0-9]+\\.class$");

    @SuppressWarnings("resource")
    private Set<Class<?>> auto_scan_absolute(String class_path) throws IOException {
        Set<Class<?>> classes = new LinkedHashSet<>();
        if (class_path.endsWith(".jar")) {
            Enumeration<JarEntry> entries = new JarFile(class_path).entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String class_name = entry.getName();
                if (!class_name.toString().endsWith(".class") && !anonymous_inner_class_compile.matcher(class_name).find()) {
                    continue;
                }
                class_name = class_name.replace(".class", "").replace("/", ".");
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(class_name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    LOGGER.error("加载class失败:", e);
                }
            }
        } else {
            Files.walk(Paths.get(class_path)).filter((path) -> {
                String path_string = path.toString();
                return path_string.endsWith(".class") && !anonymous_inner_class_compile.matcher(path_string).find();
            }).forEach((name) -> {
                String class_name = name.toString();
                if (System.getProperty("os.name").indexOf("Windows") != -1) {
                    class_name = class_name.substring(class_name.indexOf("\\classes\\") + 9).replace(".class", "").replace("\\", ".");
                } else {
                    class_name = class_name.substring(class_name.indexOf("/classes/") + 9).replace(".class", "").replace("/", ".");
                }
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(class_name));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("加载class失败:" ,e);
                }
            });
        }
        return classes;
    }
}
