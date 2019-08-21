package mockrest.root.osgi.runtime;

import java.lang.management.ManagementFactory;

/**
 * @author prince.arora
 */
public class MockRestStartupHelper {

    public static int getProcessId() {
        return Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }
}
