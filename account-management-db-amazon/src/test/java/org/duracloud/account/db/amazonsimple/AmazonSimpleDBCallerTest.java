/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple;

import org.apache.commons.httpclient.HttpStatus;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;

/**
 * @author Andrew Woods
 *         Date: Oct 10, 2010
 */
public class AmazonSimpleDBCallerTest {

    private AmazonSimpleDBCaller caller;

    private AmazonSimpleDBAsync db;

    @Before
    public void setUp() throws Exception {
        caller = new AmazonSimpleDBCaller();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testListDomains() throws Exception {
        db = createMockListDomainsDB();

        caller.listDomains(db, null);
        caller.listDomains(db, null);

        EasyMock.verify(db);
    }

    private AmazonSimpleDBAsync createMockListDomainsDB() {
        AmazonSimpleDBAsync db = EasyMock.createMock(AmazonSimpleDBAsync.class);

        AmazonServiceException e = new AmazonServiceException("test-0");
        e.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
        EasyMock.expect(db.listDomains(null)).andThrow(e);

        AmazonClientException ex = new AmazonClientException("test-1");
        EasyMock.expect(db.listDomains(null)).andThrow(ex);

        e = new AmazonServiceException("test-2");
        e.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        EasyMock.expect(db.listDomains(null)).andThrow(e);

        EasyMock.expect(db.listDomains(null)).andReturn(null);

        EasyMock.replay(db);
        return db;
    }

    @Test
    public void testCreateDomain() throws Exception {
        db = createMockCreateDomainDB();

        caller.createDomain(db, null);
        caller.createDomain(db, null);

        EasyMock.verify(db);
    }

    private AmazonSimpleDBAsync createMockCreateDomainDB() {
        AmazonSimpleDBAsync db = EasyMock.createMock(AmazonSimpleDBAsync.class);

        AmazonServiceException e = new AmazonServiceException("test-0");
        e.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
        db.createDomain(null);
        EasyMock.expectLastCall().andThrow(e);

        AmazonClientException ex = new AmazonClientException("test-1");
        db.createDomain(null);
        EasyMock.expectLastCall().andThrow(ex);

        e = new AmazonServiceException("test-2");
        e.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        db.createDomain(null);
        EasyMock.expectLastCall().andThrow(e);

        db.createDomain(null);
        EasyMock.expectLastCall();

        EasyMock.replay(db);
        return db;
    }

    @Test
    public void testSelect() throws Exception {
        db = createMockSelectDB();

        caller.select(db, null);
        caller.select(db, null);

        EasyMock.verify(db);
    }

    private AmazonSimpleDBAsync createMockSelectDB() {
        AmazonSimpleDBAsync db = EasyMock.createMock(AmazonSimpleDBAsync.class);

        AmazonServiceException e = new AmazonServiceException("test-0");
        e.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
        EasyMock.expect(db.select(null)).andThrow(e);

        AmazonClientException ex = new AmazonClientException("test-1");
        EasyMock.expect(db.select(null)).andThrow(ex);

        e = new AmazonServiceException("test-2");
        e.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        EasyMock.expect(db.select(null)).andThrow(e);

        EasyMock.expect(db.select(null)).andReturn(null);

        EasyMock.replay(db);
        return db;
    }

    @Test
    public void testPutAttributes() throws Exception {
        db = createMockPutAttributesDB();

        caller.putAttributes(db, null);
        caller.putAttributes(db, null);

        EasyMock.verify(db);
    }

    private AmazonSimpleDBAsync createMockPutAttributesDB() {
        AmazonSimpleDBAsync db = EasyMock.createMock(AmazonSimpleDBAsync.class);

        AmazonServiceException e = new AmazonServiceException("test-0");
        e.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
        db.putAttributes(null);
        EasyMock.expectLastCall().andThrow(e);

        AmazonClientException ex = new AmazonClientException("test-1");
        db.putAttributes(null);
        EasyMock.expectLastCall().andThrow(ex);

        e = new AmazonServiceException("test-2");
        e.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        db.putAttributes(null);
        EasyMock.expectLastCall().andThrow(e);

        db.putAttributes(null);
        EasyMock.expectLastCall();

        EasyMock.replay(db);
        return db;
    }

    @Test
    public void testDeleteDomainAsync() throws Exception {
        db = createMockDeleteDomainAsyncDB();

        caller.deleteDomainAsync(db, null);
        caller.deleteDomainAsync(db, null);

        EasyMock.verify(db);
    }

    private AmazonSimpleDBAsync createMockDeleteDomainAsyncDB() {
        AmazonSimpleDBAsync db = EasyMock.createMock(AmazonSimpleDBAsync.class);

        AmazonServiceException e = new AmazonServiceException("test-0");
        e.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
        db.deleteDomainAsync(null);
        EasyMock.expectLastCall().andThrow(e);

        AmazonClientException ex = new AmazonClientException("test-1");
        db.deleteDomainAsync(null);
        EasyMock.expectLastCall().andThrow(ex);

        e = new AmazonServiceException("test-2");
        e.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        db.deleteDomainAsync(null);
        EasyMock.expectLastCall().andThrow(e);

        EasyMock.expect(db.deleteDomainAsync(null)).andReturn(null);

        EasyMock.replay(db);
        return db;
    }
}
