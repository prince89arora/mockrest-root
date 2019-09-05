package mockrest.root.osgi.runtime;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;

import static mockrest.root.osgi.runtime.Constants.DEFAULT_BUNDLES_DIR;
import static mockrest.root.osgi.runtime.Constants.FILE_BUNDLES_PATH;

/**
 * @author prince.arora
 */
public class OSGiFrameworkFactory {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PROP_BUNDLE_NAME = "name";

    private static final String PROP_BUNDLE_VERSION = "version";

    private static OSGiFrameworkFactory osGiFrameworkFactory;

    private Framework framework;

    public static boolean AVAILABLE = false;

    static {
        osGiFrameworkFactory = new OSGiFrameworkFactory();
    }

    private OSGiFrameworkFactory() {}

    public Framework getFramework() {
        return framework;
    }

    public static OSGiFrameworkFactory getInstance() {
        return osGiFrameworkFactory;
    }

    /**
     *
     */
    protected void init() {
        if (!AVAILABLE) {
            ServiceLoader<FrameworkFactory> frameworkFactory = ServiceLoader.load(FrameworkFactory.class);
            FrameworkFactory factory = frameworkFactory.iterator().next();
            this.framework = factory.newFramework(this.getOsgiProperties());
            logger.info(String.format("Initializing framework => %s - %s",
                            this.framework.getSymbolicName(),
                            this.framework.getVersion()));

            try {
                this.framework.start();
                AVAILABLE = true;
                this.initMajorBundlesInstallation();
            } catch (BundleException | IOException e) {
                logger.error("Unable to start OSGI framework : ", e);
            }
        }
    }

    /**
     *
     */
    protected void stop() {
        try {
            if (AVAILABLE && Objects.nonNull(this.framework)) {
                this.framework.stop();
                this.framework.waitForStop(0);

                logger.info(String.format("Stopped framework : %s - %s",
                                        framework.getSymbolicName(),
                                        framework.getVersion()));
            }
        } catch (InterruptedException | BundleException e) {
            logger.error("Unable to stop OSGI Framework properly : ", e);
        }
    }

    /**
     * Get properties from {@link MockRestFileStructure#getConfigurationDirectoryPath()} file.
     * convert properties to {@link Map} to be passed to framework.
     *
     * @return Map<String, String>
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

    private void initMajorBundlesInstallation() throws IOException {
        /**
         * preparing temp file to have list of default bundles and version.
         */
        Path tempPath = Files.createTempFile("bundles_tmp", ".json");
        //Copying detault bundles detail in temp json.
        FileUtils.copyInputStreamToFile(
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(FILE_BUNDLES_PATH),
                tempPath.toFile()
        );
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(tempPath.toFile())) {
            BufferedReader reader = new BufferedReader(fileReader);
            reader.lines().forEach(line -> {
                stringBuilder.append(line);
            });
            JSONArray bundlesArray = new JSONArray(stringBuilder.toString());
            //go through list of default bundles and install each bundle.
            bundlesArray.forEach(bundleInfo -> {
                JSONObject object = (JSONObject) bundleInfo;
                try {
                    this.installBundleFromResources(
                            object.getString(PROP_BUNDLE_NAME),
                            object.getString(PROP_BUNDLE_VERSION)
                    );
                } catch (BundleException | URISyntaxException e) {
                    logger.error("Unable to install default bundle: {} => {}",
                            object.getString(PROP_BUNDLE_NAME), e);
                }
            });
        } catch (IOException e) {
            logger.error("Unable to process default bundles list: ", e);
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    /**
     * Install bundle from default bundles directory in resources.
     *
     * @param fileName filename/symbolic name of bundle.
     * @param version version of bundle.
     *
     * @throws BundleException
     * @throws URISyntaxException
     */
    private void installBundleFromResources(String fileName, String version) throws BundleException, URISyntaxException { ;
        //bundle file path from default bundles directory in resources.
        StringBuilder bundleJarResourcePathBuilder = new StringBuilder(DEFAULT_BUNDLES_DIR);
        bundleJarResourcePathBuilder.append(File.separatorChar)
                .append(fileName).append("-").append(version).append(".jar");

        //url to be used as bundle identifier in osgi.
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                bundleJarResourcePathBuilder.toString()
        );
        this.framework.getBundleContext()
                .installBundle(
                        url.toString(),
                        Thread.currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream(bundleJarResourcePathBuilder.toString()))
                .start();
    }
}
