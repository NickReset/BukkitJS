package social.nickrest.bukkitjs.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class BukkitJSClassloader extends URLClassLoader {

    public BukkitJSClassloader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
