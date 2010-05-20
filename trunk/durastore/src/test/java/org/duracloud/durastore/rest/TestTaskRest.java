package org.duracloud.durastore.rest;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.duracloud.common.web.RestHttpHelper;
import org.apache.commons.httpclient.HttpStatus;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class TestTaskRest extends BaseRestTester {

    @Test
    public void testPerformTask() throws Exception {
        String taskId = "unsupported-task";
        String url = baseUrl + "/task/" + taskId;
        RestHttpHelper.HttpResponse response = restHelper.post(url, null, null);
        String responseText = checkResponse(response, HttpStatus.SC_BAD_REQUEST);
        assertTrue(responseText.contains(taskId));
    }

    @Test
    public void testGetTask() throws Exception {
        String taskId = "unsupported-task";
        String url = baseUrl + "/task/" + taskId;
        RestHttpHelper.HttpResponse response = restHelper.get(url);
        String responseText = checkResponse(response, HttpStatus.SC_BAD_REQUEST);
        assertTrue(responseText.contains(taskId));
    }
}
