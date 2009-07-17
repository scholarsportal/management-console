
package org.duraspace.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.storage.domain.StorageAccount;
import org.duraspace.storage.domain.StorageAccountManager;
import org.duraspace.storage.domain.StorageException;

/**
 * Provides facilities for connecting to a set of content stores
 *
 * @author Bill Branan
 */
public class ContentStoreManager {

    private static final String DEFAULT_CONTEXT = "customerwebapp";

    private String baseURL = null;

    private static RestHttpHelper restHelper = new RestHttpHelper();

    public ContentStoreManager(String host, String port) {
        this(host, port, DEFAULT_CONTEXT);
    }

    public ContentStoreManager(String host, String port, String context) {
        if (host == null || host.equals("")) {
            throw new IllegalArgumentException("Host must be a valid server host name");
        }

        if (context == null) {
            context = DEFAULT_CONTEXT;
        }

        if (port == null || port.equals("")) {
            baseURL = "http://" + host + "/" + context;
        } else {
            baseURL = "http://" + host + ":" + port + "/" + context;
        }
    }

    public Map<String, ContentStore> getContentStores() throws StorageException {
        StorageAccountManager acctManager = getStorageAccounts();
        Map<String, StorageAccount> accounts = acctManager.getStorageAccounts();
        Map<String, ContentStore> contentStores =
            new HashMap<String, ContentStore>();
        Iterator<String> acctIDs = accounts.keySet().iterator();
        while (acctIDs.hasNext()) {
            String acctID = acctIDs.next();
            StorageAccount acct = accounts.get(acctID);
            ContentStore contentStore =
                new ContentStore(baseURL, acct.getType(), acct.getId());
            contentStores.put(acctID, contentStore);
        }
        return contentStores;
    }

    public ContentStore getPrimaryContentStore() throws StorageException {
        StorageAccountManager acctManager = getStorageAccounts();
        StorageAccount acct = acctManager.getPrimaryStorageAccount();
        ContentStore contentStore =
            new ContentStore(baseURL, acct.getType(), acct.getId());
        return contentStore;
    }

    private StorageAccountManager getStorageAccounts() throws StorageException {
        String url = baseURL + "/stores";
        HttpResponse response;
        String error = "Error retrieving content stores. ";
        try {
            response = restHelper.get(url);
            if (response.getStatusCode() == 200) {
                String storesXML = response.getResponseBody();
                if (storesXML != null) {
                    InputStream is = new ByteArrayInputStream(storesXML.getBytes());
                    return new StorageAccountManager(is, true);
                } else {
                    throw new StorageException(error + "Response content was null");
                }
            } else {
                throw new StorageException("Response code was "
                                           + response.getStatusCode() +
                                           ", expected value was 200.");
            }
        } catch (Exception e) {
            throw new StorageException(error + e.getMessage(), e);
        }
    }
}
