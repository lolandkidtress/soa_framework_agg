package com.James.Core.HotSwapper;

import com.James.Basic.UtilsTools.NativePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Created by James on 16/5/23.
 * javasist实现的重加载
 */
public class HotSwapper {
    private final static Logger LOGGER = LoggerFactory.getLogger(HotSwapper.class.getName());

    private static class InnerInstance {
        public static final HotSwapper instance = new HotSwapper();
    }

    public static HotSwapper getInstance() {
        return InnerInstance.instance;
    }

    private static final HotSwapper_ClassLoad classLoader = new HotSwapper_ClassLoad(new URL[] {}, null);
    private Method loadAgent = null;
    private Object virtualmachine_instance = null;

    private Set<Class<?>> classes = new LinkedHashSet<>();

    public Set<Class<?>> getClasses() {
        return classes;
    }

    public void addClasses(Class<?> clazz) {
        classes.add(clazz);
    }

    private HotSwapper() {
        try {
            classes = auto_scan_absolute(NativePath.get_class_path());

            String java_home = System.getProperty("java.home");
            LOGGER.info("java.home  => " + java_home);
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
            Method attach = clazz.getMethod("attach", new Class[] { String.class });
            String port = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            virtualmachine_instance = attach.invoke(null, new Object[] { port });
            loadAgent = clazz.getMethod("loadAgent", new Class[] { String.class, String.class });
            // TODO 如果只加载一次应该在使用完成后close
            // detach = clazz.getMethod("detach", new Class[] {});
        } catch (Exception e) {
            LOGGER.error("初始化AOP失败-如遇到找不到VirtualMachine类,请检查是否只安装了JRE没有安装JDK", e);
        }
    }

    // ///////////////////////////////////////////////////////////component_scan/////////////////////////////////////////////////
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
                    LOGGER.error("加载class失败:", e);
                }
            });
        }
        return classes;
    }
}
