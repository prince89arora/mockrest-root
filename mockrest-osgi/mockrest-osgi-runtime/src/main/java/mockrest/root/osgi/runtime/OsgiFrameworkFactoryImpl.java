package mockrest.root.osgi.runtime;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.util.Map;

/**
 * @author prince.arora
 */
public class OsgiFrameworkFactoryImpl implements FrameworkFactory {

    public OsgiFrameworkFactoryImpl() {
    }


    public Framework newFramework(Map<String, String> configuration) {
        System.out.println("starting osgi framework...");
        Framework framework = new OsgiFramework();
        return framework;
    }
}
