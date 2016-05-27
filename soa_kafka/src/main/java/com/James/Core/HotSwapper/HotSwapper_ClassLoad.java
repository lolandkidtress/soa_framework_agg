package com.James.Core.HotSwapper;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by James on 16/5/23.
 *
 * 自定义类加载器
 */
public class HotSwapper_ClassLoad extends URLClassLoader {

    public HotSwapper_ClassLoad(URL[] urls) {
        super(urls);
    }

    public HotSwapper_ClassLoad(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addJar(URL url) {
        this.addURL(url);
    }

}