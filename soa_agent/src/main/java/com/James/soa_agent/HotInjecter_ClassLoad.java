package com.James.soa_agent;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by James on 16/5/24.
 */
public class HotInjecter_ClassLoad extends URLClassLoader {

    public HotInjecter_ClassLoad(URL[] urls) {
        super(urls);
    }

    public HotInjecter_ClassLoad(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addJar(URL url) {
        this.addURL(url);
    }

}
