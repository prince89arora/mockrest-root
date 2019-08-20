package mockrest.root.osgi.runtime;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @author prince.arora
 */
public class OsgiFramework implements Framework {

    public OsgiFramework() {
    }

    public void init() throws BundleException {

    }

    public void init(FrameworkListener... listeners) throws BundleException {

    }

    public FrameworkEvent waitForStop(long timeout) throws InterruptedException {
        return null;
    }

    public void start() throws BundleException {
        System.out.println("Inside Start..");
    }

    public int getState() {
        return 0;
    }

    public void start(int options) throws BundleException {
        System.out.println("Inside Start options..");
    }

    public void stop() throws BundleException {

    }

    public void stop(int options) throws BundleException {

    }

    public void uninstall() throws BundleException {

    }

    public Dictionary<String, String> getHeaders() {
        return null;
    }

    public void update() throws BundleException {

    }

    public void update(InputStream in) throws BundleException {

    }

    public long getBundleId() {
        return 0;
    }

    public String getLocation() {
        return null;
    }

    public ServiceReference<?>[] getRegisteredServices() {
        return new ServiceReference[0];
    }

    public ServiceReference<?>[] getServicesInUse() {
        return new ServiceReference[0];
    }

    public boolean hasPermission(Object permission) {
        return false;
    }

    public URL getResource(String name) {
        return null;
    }

    public Dictionary<String, String> getHeaders(String locale) {
        return null;
    }

    public String getSymbolicName() {
        return null;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return null;
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        return null;
    }

    public Enumeration<String> getEntryPaths(String path) {
        return null;
    }

    public URL getEntry(String path) {
        return null;
    }

    public long getLastModified() {
        return 0;
    }

    public Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
        return null;
    }

    public BundleContext getBundleContext() {
        return null;
    }

    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int signersType) {
        return null;
    }

    public Version getVersion() {
        return null;
    }

    public <A> A adapt(Class<A> type) {
        return null;
    }

    public File getDataFile(String filename) {
        return null;
    }

    public int compareTo(Bundle o) {
        return 0;
    }
}
