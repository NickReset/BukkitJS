package social.nickrest.bukkitjs.js.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class GraalVMClassLoader extends URLClassLoader {

    public GraalVMClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
