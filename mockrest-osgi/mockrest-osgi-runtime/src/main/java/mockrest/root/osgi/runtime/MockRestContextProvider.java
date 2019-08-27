package mockrest.root.osgi.runtime;

import mockrest.root.osgi.runtime.common.MockRestContext;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author prince.arora
 */
public class MockRestContextProvider {

    private static final Logger logger = LoggerFactory.getLogger(MockRestContextProvider.class);

    private static MockRestContextProvider mockRestContextProvider;

    private Framework framework;

    protected static MockRestContextProvider init(Framework framework) {
        mockRestContextProvider = new MockRestContextProvider(framework);
        return mockRestContextProvider;
    }

    public static MockRestContextProvider getProvider(){
        return mockRestContextProvider;
    }

    private MockRestContext context;

    private MockRestContextProvider(){}

    private MockRestContextProvider(Framework framework) {
        this.context = new MockRestContext(this.framework.getBundleContext());
    }

    public MockRestContext getContext() {
        return context;
    }
}
