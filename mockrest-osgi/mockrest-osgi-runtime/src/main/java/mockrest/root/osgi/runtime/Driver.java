package mockrest.root.osgi.runtime;

import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author prince.arora
 */
public class Driver {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        MockRestStructure.getStructure().initSetup();
        new Driver().init();
    }

    public static void exit() {
        Runtime.getRuntime().exit(0);
    }

    public void init() {
        ServiceLoader<FrameworkFactory> frameworkFactory = ServiceLoader.load(FrameworkFactory.class);
        FrameworkFactory factory = frameworkFactory.iterator().next();
        Map<String, String> properties = new HashMap<>();
        final Framework framework = factory.newFramework(properties);
        logger.info("Initializing framework => "+ framework.getSymbolicName() + " - " + framework.getVersion());

        try {
            framework.start();
        } catch (BundleException e) {
            logger.error("Unable to start OSGI framework : ", e);
        }

        Thread closeHook = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            logger.info("============ Stopping OSGI framework ============");
                            framework.stop();
                            framework.waitForStop(0);
                            logger.info("Stopped framework : " + framework.getSymbolicName() + " - " + framework.getVersion());
                            Driver.exit();
                        } catch (BundleException | InterruptedException e) {
                            logger.error("Unable to stop OSGI framework : ", e);
                        }
                    }
                }
        );

        Runtime.getRuntime().addShutdownHook(closeHook);
    }
}
