package mockrest.root.osgi.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author prince.arora
 */
public class MockRestStructure {

    public static MockRestStructure INSTANCE = null;

    private String baseDirectoryPath = "";

    private boolean isBaseDirectoryExist = false;

    static {
        INSTANCE = new MockRestStructure();
    }

    private MockRestStructure(){
        this.baseDirectoryPath = this.createBaseDirectoryPath();
    }

    public static MockRestStructure getStructure() {
        return INSTANCE;
    }

    public void initSetup() {
        try {
            this.getOrWriteBaseDir();
            if (this.isBaseDirectoryExist) {
                this.writeProcessFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @throws IOException
     */
    private void getOrWriteBaseDir() throws IOException {
        Path baseDirPath = Paths.get(this.baseDirectoryPath);
        try {
            Files.createDirectory(baseDirPath);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Already exist base..");
        }
        this.isBaseDirectoryExist = true;
    }

    private void writeProcessFile() throws IOException {
        String processIdFilePath = this.baseDirectoryPath + File.separator + "PID";
        Path path = Paths.get(processIdFilePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.write(path, String.valueOf(MockRestStartupHelper.getProcessId()).getBytes(), StandardOpenOption.WRITE);
    }

    private String createBaseDirectoryPath() {
        StringBuilder stringBuilder = new StringBuilder(System.getProperty("user.dir"));
        stringBuilder.append(File.separator).append("mockrest");
        return stringBuilder.toString();
    }
}
