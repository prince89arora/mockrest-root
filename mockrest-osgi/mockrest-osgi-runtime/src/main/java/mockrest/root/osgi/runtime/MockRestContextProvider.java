package mockrest.root.osgi.runtime;

import mockrest.root.osgi.runtime.common.MockRestContext;

/**
 * @author prince.arora
 */
public enum MockRestContextProvider {

    INSTANCE;

    private MockRestContext context;

    public MockRestContext getContext() {
        return context;
    }

    public void setContext(MockRestContext context) {
        this.context = context;
    }
}
