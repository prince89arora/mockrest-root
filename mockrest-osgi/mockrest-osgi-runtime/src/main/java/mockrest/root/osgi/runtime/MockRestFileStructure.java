package mockrest.root.osgi.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static mockrest.root.osgi.runtime.Constants.BASE_DIR_NAME;
import static mockrest.root.osgi.runtime.Constants.PID_FILENAME;

/**
 * @author prince.arora
 */
public class MockRestFileStructure {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static MockRestFileStructure INSTANCE = null;

    private String baseDirectoryPath = "";

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

    public void initSetup() {
        try {
            this.getOrWriteBaseDir();
            if (this.isBaseDirectoryExist) {
                this.writeProcessFile();
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
}
