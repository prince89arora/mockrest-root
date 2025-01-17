package mockrest.root.osgi.runtime;

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
            long startTime = System.currentTimeMillis();
            //shutdown hook for close.
            Driver.shutdownHook();
            //Initialize folder structure setup.
            MockRestFileStructure.getStructure().initSetup();
            //Initialize osgi setup.
            OSGiFrameworkFactory.getInstance().init();
            logger.info("started in : "+ String.valueOf(System.currentTimeMillis() - startTime)+ " ms");
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
                        OSGiFrameworkFactory.getInstance().stop();
                    }
                }
        );
        Runtime.getRuntime().addShutdownHook(closeHook);
    }
}
