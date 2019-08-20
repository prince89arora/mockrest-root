package mockrest.root.osgi.runtime;

import org.osgi.framework.launch.FrameworkFactory;

import java.util.ServiceLoader;

/**
 * @author prince.arora
 */
public class Driver {

    public static void main(String[] args) {
        new Driver().init();
    }

    public void init() {
        ServiceLoader<FrameworkFactory> frameworkFactory = ServiceLoader.load(FrameworkFactory.class);
        FrameworkFactory factory = frameworkFactory.iterator().next();
        System.out.println(factory);
    }
}
