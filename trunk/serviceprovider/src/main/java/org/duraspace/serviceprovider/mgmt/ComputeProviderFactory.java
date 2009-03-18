
package org.duraspace.serviceprovider.mgmt;

import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.serviceprovider.domain.ComputeProviderType;

/**
 * This class is a factory for creating instances of ComputeProviders.
 * <p>
 * It holds a map of compute-provider-types and their class-names.
 * The mapping between the id & classes is configured in the Spring config files.
 *
 * ComputeProvider instances ARE NOT cached after being created.
 * </p>
 *
 * @author Andrew Woods
 */
public class ComputeProviderFactory {

    protected static final Logger log =
            Logger.getLogger(ComputeProviderFactory.class);

    private static Map<String, String> typeToClassMap;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("null")
    public static ServiceProvider getComputeProvider(ComputeProviderType providerType)
            throws Exception {
        ServiceProvider provider = null;

        Exception exception = null;

        String className = getClassNameFromId(providerType);
        log.info("class for id: '" + providerType + "' : '" + className + "'");

        Class<?> clazz = getClass(className, exception);
        if (clazz != null) {
            provider = getInstance(clazz, exception);
        }

        if (provider == null) {
            throw exception;
        }
        return provider;
    }

    private static String getClassNameFromId(ComputeProviderType providerType)
            throws Exception {
        String className = null;
        try {
            className = typeToClassMap.get(providerType.toString());
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
        return typeToClassMap;
    }

    public static void setIdToClassMap(Map<String, String> map) {
        typeToClassMap = map;
    }

}
