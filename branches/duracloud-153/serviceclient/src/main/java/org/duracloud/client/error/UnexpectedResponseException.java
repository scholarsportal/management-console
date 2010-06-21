package org.duracloud.client.error;

/**
 * Exception thrown when an unexpected response code is returned from
 * a services call.
 *
 * @author Bill Branan
 */
public class UnexpectedResponseException extends ServicesException {

    private static final long serialVersionUID = 1L;

    private static final String messageKey =
        "duracloud.error.serviceclient.unexpectedresponse";

    public UnexpectedResponseException(String defaultMessage,
                                       int actualCode,
                                       int expectedCode,
                                       String msg) {
        super(defaultMessage);
        setArgs(new Integer(actualCode).toString(),
                new Integer(expectedCode).toString(),
                msg);
    }

}