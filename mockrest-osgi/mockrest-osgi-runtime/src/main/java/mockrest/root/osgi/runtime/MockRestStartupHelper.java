package mockrest.root.osgi.runtime;

import mockrest.root.osgi.runtime.common.MockRestContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static mockrest.root.osgi.runtime.Constants.FILE_BUNDLES_PATH;

/**
 * @author prince.arora
 */
public class MockRestStartupHelper {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void initSetup(BundleContext bundleContext) throws FileNotFoundException, URISyntaxException {
        MockRestContextProvider.INSTANCE.setContext(
                new MockRestContext(bundleContext)
        );
        initMajorBundlesInstallation();
    }

    public static int getProcessId() {
        return Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    public static void initMajorBundlesInstallation() throws URISyntaxException, FileNotFoundException {
        File file = Paths.get(Thread.currentThread().getContextClassLoader()
                .getResource(FILE_BUNDLES_PATH).toURI()).toFile();
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader reader = new BufferedReader(fileReader);
            reader.lines().forEach(line -> {
                stringBuilder.append(line);
            });
            JSONArray bundlesArray = new JSONArray(stringBuilder.toString());
            bundlesArray.forEach(bundleInfo -> {
                JSONObject object = (JSONObject) bundleInfo;
                try {
                    installBundle(createBundlePath(
                            object.getString("name"),
                            object.getString("version")
                            ));
                } catch (BundleException | URISyntaxException e) {
                    logger.error("Unable to install default bundle: ", e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void installBundle(String filePath) throws BundleException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource(filePath);
        logger.info("Installing bundle : {}", url.toURI().toString());
        MockRestContextProvider.INSTANCE.getContext()
                .getBundleContext()
                .installBundle(url.toURI().toString())
                .start();
    }

    private static String createBundlePath(String fileName, String version) {
        return "default_bundles" + File.separatorChar + fileName + "-" + version + ".jar";
    }
}
