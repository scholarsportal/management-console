import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;

import java.util.List;
import java.util.Map;

/**
 * Example code which connects to the DuraCloud DuraStore REST API
 * by using the StoreClient.
 *
 * @author Bill Branan
 */
public class ExampleClient {

    private static final String HOST = "localhost";
	private static final String PORT = "8080";
	private static final String CONTEXT = "durastore";
    
    private ContentStore store;

    public ExampleClient() throws ContentStoreException {
        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(HOST, PORT, CONTEXT);
        store = storeManager.getPrimaryContentStore();  
    }

    public void printStores() throws ContentStoreException {
        List<String> storeIDs = store.getSpaces();
        for(String storeId : storeIDs) {
            System.out.println("Store ID: " + storeId);
            Map<String, String> spaceMetadata = store.getSpaceMetadata(storeId);
            for(String metadataKey : spaceMetadata.keySet()) {
                System.out.println(metadataKey + ": " +
                                   spaceMetadata.get(metadataKey));
            }
            System.out.println("---");
        }
    }

	public static void main(String[] args) throws Exception {
	    ExampleClient storesClient = new ExampleClient();
        storesClient.printStores();
	}

}
