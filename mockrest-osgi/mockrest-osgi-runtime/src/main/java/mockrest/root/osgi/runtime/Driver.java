package mockrest.root.osgi.runtime;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * @author prince.arora
 */
public class Driver {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        /*
        Init files structure setup for mockrest.
         */
        MockRestFileStructure.getStructure().initSetup();
        new Driver().init();
    }

    public static void exit() {
        Runtime.getRuntime().exit(0);
    }

    public void init() {
        ServiceLoader<FrameworkFactory> frameworkFactory = ServiceLoader.load(FrameworkFactory.class);
        FrameworkFactory factory = frameworkFactory.iterator().next();
        Map<String, String> properties = this.getOsgiProperties();
        Framework framework = factory.newFramework(properties);
        logger.info("Initializing framework => "+ framework.getSymbolicName() + " - " + framework.getVersion());

        try {
            framework.start();
            MockRestStartupHelper.initSetup(framework.getBundleContext());
        } catch (BundleException | IOException e) {
            logger.error("Unable to start OSGI framework : ", e);
        }

        /**
         * close hook to shutdown OSGI framework.
         */
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

    /**
     * Get properties from {@link MockRestFileStructure#getConfigurationDirectoryPath()} file.
     * convert properties to {@link Map} to be passed to framework.
     *
     * @return
     */
    private Map<String, String> getOsgiProperties() {
        Map<String, String> map = new HashMap<>();
        try (InputStream stream = new FileInputStream(MockRestFileStructure.getStructure()
                .getOsgiConfigurationFilePath())) {
            Properties properties = new Properties();
            properties.load(stream);
            //Add each property found in configuration properties file in map.
            properties.stringPropertyNames().stream().forEach(key -> {
                if (Objects.nonNull(properties.getProperty(key)) &&
                        !properties.getProperty(key).isEmpty()) {
                    map.put(key, properties.getProperty(key));
                }
            });
        } catch (IOException e) {
            logger.error("Error while preparing osgi properties: ", e);
        }
        return map;
    }
}
