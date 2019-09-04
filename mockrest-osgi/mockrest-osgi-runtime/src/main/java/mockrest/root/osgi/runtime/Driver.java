package mockrest.root.osgi.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * @author prince.arora
 */
public class Driver {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        /*
        Init files structure setup for mockrest.
         */
        try {
            MockRestFileStructure.getStructure().initSetup();
            OSGiFrameworkFactory.init();
            MockRestStartupHelper.initSetup();
        } catch (Exception ex) {
            logger.error("Exception in Driver ", ex);
        }
    }
}
