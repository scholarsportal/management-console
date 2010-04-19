package org.duracloud.common.model;

import java.util.Calendar;
import java.util.Random;

/**
 * @author Andrew Woods
 *         Date: Mar 20, 2010
 */
public class SystemUserCredential extends Credential {
    private static final String id = generateId();

    private static String generateId() {
        Random r = new Random();
        int prefix = r.nextInt();
        return prefix + ":" + Calendar.getInstance().toString();
    }

    public SystemUserCredential() {
        super(id, "not-needed");
    }

    @Override
    public String toString() {
        return "Credential [system: hash(" + id.hashCode() + ")]";
    }
}
