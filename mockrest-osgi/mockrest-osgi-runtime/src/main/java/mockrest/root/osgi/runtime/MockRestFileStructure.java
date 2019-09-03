package mockrest.root.osgi.runtime;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Properties;

import static mockrest.root.osgi.runtime.Constants.*;

/**
 * @author prince.arora
 */
public class MockRestFileStructure {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static MockRestFileStructure INSTANCE = null;

    private static final String CONF_PROP_APP_BASE = "mr_base";

    private static final String CONF_PROP_APP_JARFILE = "mr_executor";

    //path to base mockrest directory
    private String baseDirectoryPath;

    //path to conf directory
    private String configurationDirectoryPath;

    private String osgiConfigurationFilePath;

    private String binDirectoryPath;

    //state of base directory presence.
    private boolean isBaseDirectoryExist = false;

    static {
        //Init class singleton instance.
        INSTANCE = new MockRestFileStructure();
    }

    private MockRestFileStructure(){
        this.baseDirectoryPath = this.createBaseDirectoryPath();
    }

    public static MockRestFileStructure getStructure() {
        return INSTANCE;
    }

    public String getBaseDirectoryPath() {
        return baseDirectoryPath;
    }

    public String getConfigurationDirectoryPath() {
        return configurationDirectoryPath;
    }

    public String getOsgiConfigurationFilePath() {
        return osgiConfigurationFilePath;
    }

    public void initSetup() {
        try {
            this.getOrWriteBaseDir();
            if (this.isBaseDirectoryExist) {
                this.writeProcessFile();
                this.setupConfigurations();
                this.setupBin();
            }
        } catch (IOException e) {
            logger.error("Unable to setup file system for mockrest: ", e);
        }
    }

    /**
     * Create mockrest output base directory if it does not exist.
     *
     * @throws IOException
     */
    private void getOrWriteBaseDir() throws IOException {
        Path baseDirPath = Paths.get(this.baseDirectoryPath);
        if (!Files.exists(baseDirPath)) {
            Files.createDirectory(baseDirPath);
        }
        this.isBaseDirectoryExist = true;
    }

    /**
     * Write process file to capture process id.
     *
     * @throws IOException
     */
    private void writeProcessFile() throws IOException {
        String processIdFilePath = this.baseDirectoryPath + File.separator + PID_FILENAME;
        Path path = Paths.get(processIdFilePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.write(path,
                String.valueOf(MockRestStartupHelper.getProcessId()).getBytes(),
                StandardOpenOption.WRITE);
    }

    /**
     * full path in file system for mockrest base directory.
     *
     * @return
     */
    private String createBaseDirectoryPath() {
        StringBuilder stringBuilder = new StringBuilder(System.getProperty("user.dir"));
        stringBuilder.append(File.separator).append(BASE_DIR_NAME);
        return stringBuilder.toString();
    }

    /**
     * Setup configuration files in output base directory.
     */
    private void setupConfigurations() {
        try {
            this.setupConfigurationDirectory();
            this.setupFelixConfig();
            this.setupApplicationConfig();
        } catch (IOException e) {
            logger.error("Unable to setup configuration : ", e);
        }
    }

    /**
     * Create conf directory if does not exist.
     *
     * @throws IOException
     */
    private void setupConfigurationDirectory() throws IOException {
        Path confDirectoryPath = Paths.get(this.baseDirectoryPath + File.separatorChar + OUTPUT_DIR_CONF);
        this.configurationDirectoryPath = confDirectoryPath.toString();
        if (Files.exists(confDirectoryPath)) {
            return;
        }
        Files.createDirectory(confDirectoryPath);
    }

    /**
     * save felix configuration properties file.
     *
     * @throws IOException
     */
    private void setupFelixConfig() throws IOException {
        this.osgiConfigurationFilePath = this.configurationDirectoryPath + File.separatorChar + OUTPUT_DIR_OSGI_FILE;
        Path targetPath = Paths.get(this.osgiConfigurationFilePath);
        if (!Files.exists(targetPath)) {
            URL sourcePath = Thread.currentThread().getContextClassLoader().getResource(OSGI_CONF_FILE);
            FileUtils.copyURLToFile(sourcePath, targetPath.toFile());
        }
    }

    private void setupBin() throws IOException {
        this.binDirectoryPath = this.baseDirectoryPath + File.separatorChar + DIR_OUTPUT_BIN;
        Path binPath = Paths.get(this.binDirectoryPath);
        if (!Files.exists(binPath)) {
            Files.createDirectory(binPath);
        }
        if (Files.list(binPath).toArray().length == 0) {
            this.copyBinContent();
        }
    }

    /**
     * Setup application configuration properties file under conf.
     *
     * @throws IOException
     */
    private void setupApplicationConfig() throws IOException {
        Path outputConf = Paths.get(this.configurationDirectoryPath + File.separatorChar + OUTPUT_DIR_CONFIG_FILE);
        if (!Files.exists(outputConf)) {
            Files.createFile(outputConf);
        }
        this.manageApplicationConfigurations(outputConf);
    }

    /**
     * Preparing application conf properties.
     * Checking conf file for existing properties, validating them and
     * replacing if needed.
     *
     * properties contains
     *  <ul>
     *      <li>application base path</li>
     *      <li>executable jar file name.</li>
     *  </ul>
     *
     * @param confPath
     * @throws IOException
     */
    private void manageApplicationConfigurations(Path confPath) throws IOException {
        FileReader fileReader = new FileReader(confPath.toFile());
        boolean needToWrite = false;

        //read existing properties from conf.
        Properties properties = new Properties();
        properties.load(fileReader);

        //check application base path and update if missing or not correct.
        String basePath = StringUtils.substringBeforeLast(this.baseDirectoryPath,
                String.valueOf(File.separatorChar));
        if (properties.getProperty(CONF_PROP_APP_BASE, "").isEmpty() ||
                !properties.get(CONF_PROP_APP_BASE).equals(basePath)) {
            properties.put(CONF_PROP_APP_BASE, basePath);
            needToWrite = true;
        }

        //check jar file name and update if missing or incorrect.
        File jarFile = new File(MockRestFileStructure.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (Objects.nonNull(jarFile) && (properties.getProperty(CONF_PROP_APP_JARFILE, "").isEmpty() ||
                                    !properties.getProperty(CONF_PROP_APP_JARFILE).equals(jarFile.getName()))) {
            properties.put(CONF_PROP_APP_JARFILE, jarFile.getName());
            needToWrite = true;
        }
        //write properties file if any property is changes or updated.
        if (needToWrite) {
            try(Writer fileWriter = new FileWriter(confPath.toFile())) {
                properties.store(fileWriter, "");
            }
        }
    }

    private void copyBinContent() {
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(FILE_BIN_CONF_PATH);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()))) {
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(line -> {
                builder.append(line);
            });

            JSONArray resourceArray = new JSONArray(builder.toString());
            this.copyResources(resourceArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyResources(JSONArray resourcesList) {
        resourcesList.forEach(conf -> {
            JSONObject object = (JSONObject) conf;
            InputStream stream = MockRestFileStructure.class.getClassLoader()
                    .getResourceAsStream(object.getString(RESOURCE_TO_COPY_FILE));
            if (Objects.nonNull(stream)) {
                String outputPath = this.baseDirectoryPath + File.separatorChar + object.getString(RESOURCE_TO_COPY_DEST);
                try {
                    final File outputFile = new File(outputPath);
                    FileUtils.copyInputStreamToFile(stream, outputFile);
                    if (object.getBoolean(RESOURCE_TO_COPY_EXECUTABLE)) {
                        outputFile.setExecutable(true);
                    }
                } catch (IOException e) {
                    logger.error(
                            String.format(
                                    "Unable to copy resource %s destination %s ",
                                    object.getString(RESOURCE_TO_COPY_FILE),
                                    object.getString(RESOURCE_TO_COPY_DEST)), e);
                } finally {
                    try {
                        stream.close();
                        stream = null;
                    } catch (IOException e) {
                        logger.error("Unable to close stream : ", e);
                    }
                }
            }

        });
    }
}
