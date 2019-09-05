package mockrest.root.osgi.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;

/**
 * @author prince.arora
 */
public class MockRestStartupHelper {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public static int getProcessId() {
        return Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }



}
