package org.duracloud.common.model;

import java.util.Random;

/**
 * @author Andrew Woods
 *         Date: Mar 20, 2010
 */
public class SystemUserCredential extends Credential {
    private static String id;

    public SystemUserCredential() {
        super(generateId(), "not-needed");
    }

    private static String generateId() {
        if (null == id) {
            Random r = new Random();
            int prefix = r.nextInt(10000);
            id = prefix + "-" + System.currentTimeMillis();
        }
        return id;
    }

    @Override
    public String toString() {
        return "Credential [system: hash(" + id.hashCode() + ")]";
    }
}
