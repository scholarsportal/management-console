/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.httpclient.HttpStatus.SC_CONFLICT;
import static org.apache.commons.httpclient.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.commons.httpclient.HttpStatus.SC_SERVICE_UNAVAILABLE;

/**
 * This class is a utility that has the arg db execute the arg request and retry
 * if there is a server or connection error.
 *
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class AmazonSimpleDBCaller {

    private static final Logger log = LoggerFactory.getLogger(
        AmazonSimpleDBCaller.class);

    public ListDomainsResult listDomains(final AmazonSimpleDBAsync db,
                                         final ListDomainsRequest listRequest) {
        return new Caller<ListDomainsResult>() {
            protected ListDomainsResult doCall() throws Exception {
                return db.listDomains(listRequest);
            }
        }.call();
    }

    public void createDomain(final AmazonSimpleDBAsync db,
                             final CreateDomainRequest createRequest) {
        new Caller<Integer>() {
            protected Integer doCall() throws Exception {
                db.createDomain(createRequest);
                return 0;
            }
        }.call();
    }

    public SelectResult select(final AmazonSimpleDBAsync db,
                               final SelectRequest request) {
        return new Caller<SelectResult>() {
            protected SelectResult doCall() throws Exception {
                return db.select(request);
            }
        }.call();
    }

    public void putAttributes(final AmazonSimpleDBAsync db,
                              final PutAttributesRequest request)
        throws DBConcurrentUpdateException {
        AmazonServiceException exception = new Caller<AmazonServiceException>() {
            protected AmazonServiceException doCall() throws Exception {
                try {
                    db.putAttributes(request);
                    
                } catch (AmazonServiceException e) {
                    if (e.getStatusCode() == SC_CONFLICT) {
                        return e;
                    } else {
                        throw e;
                    }
                }
                return null;
            }
        }.call();

        if (null != exception && exception.getStatusCode() == SC_CONFLICT) {
            throw new DBConcurrentUpdateException(exception);
        }
    }

    public void deleteDomainAsync(final AmazonSimpleDBAsync db,
                                  final DeleteDomainRequest request) {
        new Caller<Integer>() {
            protected Integer doCall() throws Exception {
                db.deleteDomainAsync(request);
                return 0;
            }
        }.call();
    }

    /**
     * This nested class spins on the abstract doCall() until the expected
     * result is returned or the maximum number of tries has been reached.
     *
     * @param <T> object type returned from abstract doCall()
     */
    private static abstract class Caller<T> {

        public T call() {
            T result = null;

            boolean callComplete = false;
            int maxTries = 5;
            int tries = 0;

            while (!callComplete && tries < maxTries) {
                try {
                    result = doCall();
                    callComplete = true;

                } catch (AmazonServiceException ase) {
                    if (ase.getStatusCode() == SC_INTERNAL_SERVER_ERROR ||
                        ase.getStatusCode() == SC_SERVICE_UNAVAILABLE) {
                        sleep(tries);
                        callComplete = false;
                    } else {
                        callComplete = true;
                    }

                    log.warn("Exception in SimpleDB call: " + ase.getMessage() +
                        ", retry: " + !callComplete);

                } catch (Exception e) {
                    log.warn("Unexpected exception: " + e.getMessage() +
                        ", retry: " + !callComplete);
                    callComplete = false;
                }
                tries++;
            }

            return result;
        }

        protected abstract T doCall() throws Exception;

    }

    private static void sleep(int tries) {
        try {
            Thread.sleep((long) (Math.random() * (Math.pow(3, tries) * 10L)));
        } catch (InterruptedException e) {
            // do nothing.
        }
    }

}
