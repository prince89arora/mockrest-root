package mockrest.root.osgi.runtime;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import sun.java2d.loops.ProcessPath;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author prince.arora
 */
public class Driver {

    public static void main(String[] args) {
        MockRestStructure.getStructure().initSetup();

//        Driver driver = new Driver();
//        driver.init();
    }

    public static void exit() {
        System.out.println("Closing the runtime..");
        System.out.println("Process id : "+ ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        Runtime.getRuntime().exit(0);
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

        Thread closeHook = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            framework.stop();
                            System.out.println("Waiting for framework to stop..");
                            FrameworkEvent event = framework.waitForStop(0);
                            System.out.println(event.getType());
                            Driver.exit();
                        } catch (BundleException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        Runtime.getRuntime().addShutdownHook(closeHook);
    }
}
