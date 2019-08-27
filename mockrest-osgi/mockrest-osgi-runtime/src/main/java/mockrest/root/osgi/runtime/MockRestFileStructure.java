package mockrest.root.osgi.runtime;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static mockrest.root.osgi.runtime.Constants.BASE_DIR_NAME;
import static mockrest.root.osgi.runtime.Constants.OSGI_CONF_FILE;
import static mockrest.root.osgi.runtime.Constants.OUTPUT_DIR_CONF;
import static mockrest.root.osgi.runtime.Constants.OUTPUT_DIR_OSGI_FILE;
import static mockrest.root.osgi.runtime.Constants.PID_FILENAME;

/**
 * @author prince.arora
 */
public class MockRestFileStructure {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static MockRestFileStructure INSTANCE = null;

    private String baseDirectoryPath = "";
    private String configurationDirectoryPath = "";
    private String osgiConfigurationFilePath = "";

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
            }
        } catch (IOException e) {
            logger.error("Unable to setup file system for mockrest: ", e);
        }
    }

    /**
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

    private void writeProcessFile() throws IOException {
        String processIdFilePath = this.baseDirectoryPath + File.separator + PID_FILENAME;
        Path path = Paths.get(processIdFilePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.write(path, String.valueOf(MockRestStartupHelper.getProcessId()).getBytes(),
                StandardOpenOption.WRITE);
    }

    private String createBaseDirectoryPath() {
        StringBuilder stringBuilder = new StringBuilder(System.getProperty("user.dir"));
        stringBuilder.append(File.separator).append(BASE_DIR_NAME);
        return stringBuilder.toString();
    }

    private void setupConfigurations() {
        try {
            this.setupConfigurationDirectory();
            this.setupFelixConfig();
        } catch (IOException e) {
            logger.error("Unable to setup configuration : ", e);
        }
    }

    private void setupConfigurationDirectory() throws IOException {
        if (this.isBaseDirectoryExist) {
            Path confDirectoryPath = Paths.get(this.baseDirectoryPath + File.separatorChar + OUTPUT_DIR_CONF);
            if (!Files.exists(confDirectoryPath)) {
                Files.createDirectory(confDirectoryPath);
            }
            this.configurationDirectoryPath = confDirectoryPath.toString();
        }
    }

    private void setupFelixConfig() throws IOException {
        this.osgiConfigurationFilePath = this.configurationDirectoryPath + File.separatorChar + OUTPUT_DIR_OSGI_FILE;
        Path targetPath = Paths.get(this.osgiConfigurationFilePath);
        if (!Files.exists(targetPath)) {
            URL sourcePath = Thread.currentThread().getContextClassLoader().getResource(OSGI_CONF_FILE);
            FileUtils.copyURLToFile(sourcePath, targetPath.toFile());
        }
    }
}
