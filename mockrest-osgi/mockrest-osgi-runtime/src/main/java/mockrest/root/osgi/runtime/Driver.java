package mockrest.root.osgi.runtime;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Framework framework = factory.newFramework(properties);
        logger.info("Initializing framework => "+ framework.getSymbolicName() + " - " + framework.getVersion());

        try {
            framework.start();
            this.installConsole(framework);
        } catch (BundleException | IOException e) {
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

    private void installConsole(Framework framework) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("default_bundles");
        System.out.println("original:: "+ url.getPath());
        System.out.println("fetching:: "+ StringUtils.substringAfter(url.getPath(), "file:"));
        System.out.println(
                "Files: " + new File(StringUtils.substringAfter(url.getPath(), "file:")).isDirectory()
        );
        File[] files = new File(StringUtils.substringAfter(url.getPath(), "file:")).listFiles();

        for (File file : files) {
            logger.info("Processing bundle : "+ file.toURI().toString());
            try {
                Bundle bundle = framework.getBundleContext().installBundle(file.toURI().toString());
                logger.info("Bundle installed: "+ bundle.getSymbolicName());
                bundle.start();
            } catch (BundleException e) {
                logger.error("Unable to install bundle: ", e);
            }
        }
    }
}
