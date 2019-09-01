package mockrest.root.osgi.runtime;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static mockrest.root.osgi.runtime.Constants.DEFAULT_BUNDLES_DIR;
import static mockrest.root.osgi.runtime.Constants.FILE_BUNDLES_PATH;

/**
 * @author prince.arora
 */
public class MockRestStartupHelper {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PROP_BUNDLE_NAME = "name";

    private static final String PROP_BUNDLE_VERSION = "version";

    public static void initSetup() throws IOException {
        initMajorBundlesInstallation();
    }

    public static int getProcessId() {
        return Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    public static void initMajorBundlesInstallation() throws IOException {
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
                    installBundleFromResources(
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
    public static void installBundleFromResources(String fileName, String version) throws BundleException, URISyntaxException { ;
        //bundle file path from default bundles directory in resources.
        StringBuilder bundleJarResourcePathBuilder = new StringBuilder(DEFAULT_BUNDLES_DIR);
        bundleJarResourcePathBuilder.append(File.separatorChar)
                .append(fileName).append("-").append(version).append(".jar");

        //url to be used as bundle identifier in osgi.
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                bundleJarResourcePathBuilder.toString()
        );
        MockRestContextProvider.getProvider().getContext()
                .getBundleContext()
                .installBundle(
                        url.toString(),
                        Thread.currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream(bundleJarResourcePathBuilder.toString()))
                .start();
    }

}
