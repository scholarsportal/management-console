package org.duraspace.mainwebapp.rest;

/**
 * Provides interaction with services
 *
 * @author Bill Branan
 */
public class ServiceResource {

    /**
     * Provides a listing of all services that are available to DuraSpace users.
     * No customer-specific information is included.
     *
     * @return XML listing of services
     */
    public static String getServices() {
        String xml = "<services />";
        return xml;
    }

    /**
     * Provides a listing of all services for which a customer has subscribed.
     *
     * @param customerID
     * @return XML listing of service subscriptions
     */
    public static String getServiceSubscriptions(String customerID) {
        String xml = "<serviceSubscriptions />";
        return xml;
    }

    /**
     * Provides the configuration information for a service subscription.
     *
     * @param customerID
     * @param serviceID
     * @return XML service configuration
     */
    public static String getServiceConfiguration(String customerID,
                                          String serviceID) {
        String xml = "<serviceConfiguration />";
        return xml;
    }

    /**
     * Subscribes a customer to a service.
     *
     * @param customerID
     * @param serviceID
     * @param configurationXML
     * @return success
     */
    public static boolean addServiceSubscription(String customerID,
                                           String serviceID,
                                           String configurationXML) {
        return true;
    }

    /**
     * Updates the configuration of a service.
     *
     * @param customerID
     * @param serviceID
     * @param configurationXML
     * @return success
     */
    public static boolean updateServiceConfiguration(String customerID,
                                               String serviceID,
                                               String configurationXML) {
        return true;
    }

    /**
     * Removes a customer's service subscription.
     *
     * @param customerID
     * @param serviceID
     * @param configurationXML
     * @return success
     */
    public static boolean removeServiceSubscription(String customerID,
                                                     String serviceID) {
        return true;
    }
}