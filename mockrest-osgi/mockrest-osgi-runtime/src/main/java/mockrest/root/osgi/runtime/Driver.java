package mockrest.root.osgi.runtime;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.util.HashMap;
import java.util.Map;
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
        Map<String, String> properties = new HashMap<>();
        final Framework framework = factory.newFramework(properties);

        try {
            framework.start();
        } catch (BundleException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            framework.start();
                            System.out.println("Waiting for framework to stop..");
                            framework.waitForStop(0);
                            System.out.println("Framework stopped");
                            System.exit(0);
                        } catch (BundleException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}
