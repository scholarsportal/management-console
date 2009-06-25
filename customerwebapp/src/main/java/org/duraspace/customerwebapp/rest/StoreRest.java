package org.duraspace.customerwebapp.rest;

import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.duraspace.customerwebapp.rest.RestUtil.RequestContent;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.domain.StorageProviderType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * Provides interaction with storage providers accounts via REST
 *
 * @author Bill Branan
 */
@Path("/stores")
public class StoreRest extends BaseRest {

    /**
     * Initializes the instance. Expects as POST data
     * an XML file which includes credentials for all
     * available storage providers accounts.
     *
     * @return 200 on success
     */
    @POST
    public Response initializeStores(){
        RequestContent content = null;
        try {
            RestUtil restUtil = new RestUtil();
            content = restUtil.getRequestContent(request, headers);
            StorageProviderFactory.initialize(content.getContentStream());
            String responseText = "Initialization Successful";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Provides a listing of all available storage provider accounts
     *
     * @return 200 response with XML file listing stores
     */
    @GET
    public Response getStores(){
        Element accounts = new Element("storageProviderAccounts");

        try {
            // Get the list of storage provider ids
            Iterator<String> storageIDs =
                StorageProviderFactory.getStorageProviderAccountIds();

            // Get the primary storage provider id
            String primaryId =
                StorageProviderFactory.getPrimaryStorageProviderAccountId();

            while(storageIDs.hasNext()) {
                String storageID = storageIDs.next();
                StorageProviderType providerType =
                    StorageProviderFactory.getStorageProviderType(storageID);

                Element storageAcct = new Element("storageAcct");
                storageAcct.setAttribute("isPrimary",
                    new Boolean(storageID.equals(primaryId)).toString());
                storageAcct.addContent(new Element("id").setText(storageID));
                storageAcct.addContent(new Element("storageProviderType").
                                       setText(providerType.name()));
                accounts.addContent(storageAcct);
            }
        } catch(StorageException se) {
            return Response.serverError().entity(se.getMessage()).build();
        }

        Document storesDocument = new Document(accounts);
        XMLOutputter outputter = new XMLOutputter();
        String storesXml = outputter.outputString(storesDocument);
        return Response.ok(storesXml, TEXT_PLAIN).build();
    }

}
