/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import org.apache.commons.httpclient.HttpStatus;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Oct 10, 2010
 */
public class AmazonSimpleDBCallerTest {

    private AmazonSimpleDBCaller caller;

    private AmazonSimpleDB db;

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

    private AmazonSimpleDB createMockListDomainsDB() {
        AmazonSimpleDB db = EasyMock.createMock(AmazonSimpleDB.class);

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

    private AmazonSimpleDB createMockCreateDomainDB() {
        AmazonSimpleDB db = EasyMock.createMock(AmazonSimpleDB.class);

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

    private AmazonSimpleDB createMockSelectDB() {
        AmazonSimpleDB db = EasyMock.createMock(AmazonSimpleDB.class);

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

    private AmazonSimpleDB createMockPutAttributesDB() {
        AmazonSimpleDB db = EasyMock.createMock(AmazonSimpleDB.class);

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
    public void testDeleteAttributes() throws Exception {
        db = createMockDeleteAttributesDB();

        caller.deleteAttributes(db, null);
        caller.deleteAttributes(db, null);

        EasyMock.verify(db);
    }

    private AmazonSimpleDB createMockDeleteAttributesDB() {
        AmazonSimpleDB db = EasyMock.createMock(AmazonSimpleDB.class);

        AmazonServiceException e = new AmazonServiceException("test-0");
        e.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
        db.deleteAttributes(null);
        EasyMock.expectLastCall().andThrow(e);

        AmazonClientException ex = new AmazonClientException("test-1");
        db.deleteAttributes(null);
        EasyMock.expectLastCall().andThrow(ex);

        e = new AmazonServiceException("test-2");
        e.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        db.deleteAttributes(null);
        EasyMock.expectLastCall().andThrow(e);

        db.deleteAttributes(null);
        EasyMock.expectLastCall();

        EasyMock.replay(db);
        return db;
    }

    @Test
    public void testDeleteDomain() throws Exception {
        db = createMockDeleteDomainDB();

        caller.deleteDomainAsync(db, null);
        caller.deleteDomainAsync(db, null);

        EasyMock.verify(db);
    }

    private AmazonSimpleDB createMockDeleteDomainDB() {
        AmazonSimpleDB db = EasyMock.createMock(AmazonSimpleDB.class);

        AmazonServiceException e = new AmazonServiceException("test-0");
        e.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
        db.deleteDomain(null);
        EasyMock.expectLastCall().andThrow(e);

        AmazonClientException ex = new AmazonClientException("test-1");
        db.deleteDomain(null);
        EasyMock.expectLastCall().andThrow(ex);

        e = new AmazonServiceException("test-2");
        e.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        db.deleteDomain(null);
        EasyMock.expectLastCall().andThrow(e);

        db.deleteDomain(null);
        EasyMock.expectLastCall();

        EasyMock.replay(db);
        return db;
    }
}
