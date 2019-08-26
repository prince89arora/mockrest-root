package mockrest.root.osgi.runtime.common;

import org.osgi.framework.BundleContext;

/**
 * @author prince.arora
 */
public class MockRestContext {

    private final BundleContext bundleContext;

    public MockRestContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}
