
package org.duraspace.serviceprovider.mgmt;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class is a factory for creating instances of ComputeProviders.
 * <p>
 * It holds a map of compute-provider-ids and their class-names.
 * ComputeProvider instances ARE NOT cached after being created.
 * </p>
 *
 * @author Andrew Woods
 */
public class ComputeProviderFactoryImpl {

    protected static final Logger log =
            Logger.getLogger(ComputeProviderFactory.class);

    private static Map<String, String> idToClassMap;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("null")
    public static ServiceProvider getComputeProvider(String providerId)
            throws Exception {
        ServiceProvider provider = null;

        Exception exception = null;

        String className = getClassNameFromId(providerId);
        log.info("class for id: '" + providerId + "' : '" + className + "'");

        Class<?> clazz = getClass(className, exception);
        if (clazz != null) {
            provider = getInstance(clazz, exception);
        }

        if (provider == null) {
            throw exception;
        }
        return provider;
    }

    private static String getClassNameFromId(String providerId)
            throws Exception {
        String className = null;
        try {
            className = idToClassMap.get(providerId);
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
        return className;
    }

    private static Class<?> getClass(String className, Exception exception) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("No class found for classname: '" + className + "'");
            log.error(e);
            exception = e;
        }
        return clazz;
    }

    private static ServiceProvider getInstance(Class<?> clazz,
                                               Exception exception) {
        ServiceProvider provider = null;
        try {
            provider = (ServiceProvider) clazz.newInstance();
        } catch (InstantiationException e) {
            log.error(e);
            exception = e;
        } catch (IllegalAccessException e) {
            log.error(e);
            exception = e;
        }
        return provider;
    }

    public Map<String, String> getIdToClassMap() {
        return idToClassMap;
    }

    public static void setIdToClassMap(Map<String, String> map) {
        idToClassMap = map;
    }

}
