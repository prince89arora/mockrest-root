package mockrest.root.osgi.runtime;

import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Driver/main class to start folder structure setup and osgi framework.
 *
 * @author prince.arora
 */
public class Driver {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        try {
            //shutdown hook for close.
            Driver.shutdownHook();
            //Initialize folder structure setup.
            MockRestFileStructure.getStructure().initSetup();
            //Initialize osgi setup.
            OSGiFrameworkFactory.getInstance().init();
        } catch (Exception ex) {
            logger.error("Exception in Driver ", ex);
        }
    }

    /**
     * Add a shutdown hook to stop osgi framework before java process
     * is closed.
     */
    public static void shutdownHook() {
        /**
         * close hook to shutdown OSGI framework.
         */
        Thread closeHook = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (OSGiFrameworkFactory.AVAILABLE) {
                                final Framework framework = OSGiFrameworkFactory.getInstance().getFramework();
                                framework.stop();
                                framework.waitForStop(0);

                                logger.info(String.format("Stopped framework : %s - %s",
                                        framework.getSymbolicName(),
                                        framework.getVersion()));
                            }
                        } catch (BundleException | InterruptedException e) {
                            logger.error("Unable to stop OSGI framework : ", e);
                        }
                    }
                }
        );
        Runtime.getRuntime().addShutdownHook(closeHook);
    }
}
