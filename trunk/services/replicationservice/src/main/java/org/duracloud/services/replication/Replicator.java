
package org.duracloud.services.replication;

/**
 * Performs replication activities
 *
 * @author Bill Branan
 */
public class Replicator {

    private String baseURL;
    private String fromStoreID;
    private String toStoreID;

    public Replicator(String baseURL, String fromStoreID, String toStoreID) {
        this.baseURL = baseURL;
        this.fromStoreID = fromStoreID;
        this.toStoreID = toStoreID;
    }

    public void replicate(String spaceID, String contentID) {
        String contentURL = baseURL + "/" + spaceID + "/" + contentID;
        String fromURL = contentURL + "?storeID=" + fromStoreID;
        String toURL = contentURL + "?storeID=" + toStoreID;

        System.out.println("Performing Mock Replication...");
        System.out.println("Replicating content item from " + fromURL);
        System.out.println("Replicating content item to " + toURL);
        System.out.println("Mock Replication Complete");
    }

}
