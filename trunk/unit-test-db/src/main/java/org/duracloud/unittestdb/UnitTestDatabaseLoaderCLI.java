package org.duracloud.unittestdb;

import org.apache.log4j.Logger;
import org.duracloud.common.model.Credential;
import org.duracloud.storage.domain.StorageProviderType;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UnitTestDatabaseLoaderCLI {

    protected static final Logger log =
            Logger.getLogger(UnitTestDatabaseUtil.class);

    private final UnitTestDatabaseUtil dbUtil;

    public UnitTestDatabaseLoaderCLI()
            throws Exception {
        dbUtil = new UnitTestDatabaseUtil();
    }

    private void begin() throws Exception {
        if (inputCreateNewDB()) {
            dbUtil.createNewDB();
        } else {
            dbUtil.connectToExistingDB();
        }
        inputPasswords();
    }

    private boolean inputCreateNewDB() {
        System.out.println("[A]ppend to existing db or [C]reate new db? a|c:");
        String cmd = readLine().trim();
        while (!cmd.equalsIgnoreCase("a") && !cmd.equalsIgnoreCase("c")) {
            System.out.println("Invalid entry: '" + cmd
                    + "', try again: 'a' or 'c'");
            cmd = readLine().trim();
        }
        return cmd.equals("c");
    }

    private void inputPasswords() {
        while (true) {
            StorageProviderType type = inputResourceType();
            String username = inputUsername();
            String password = inputPassword();
            if (isValid(username, password)) {
                Credential cred = new Credential(username, password);
                dbUtil.insertCredentialForResource(type, cred);
            }
            if (inputFinished()) {
                break;
            }
        }
    }

    private StorageProviderType inputResourceType() {
        StringBuilder sb =
                new StringBuilder("Enter resource type from [");

        for (StorageProviderType type : StorageProviderType.values()) {
            sb.append(type + ", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "]");
        System.out.println(sb.toString());
        String type = readLine().trim();
        while (StorageProviderType.fromString(type)
                .equals(StorageProviderType.UNKNOWN)) {
            System.out.println("Invalid entry: '" + type + "', try again.");
            type = readLine().trim();
        }
        return StorageProviderType.fromString(type);
    }

    private String inputUsername() {
        System.out.println("Enter username: ");
        return readLine().trim();
    }

    private String inputPassword() {
        System.out.println("Enter password: ");
        return readLine().trim();
    }

    private boolean inputFinished() {
        System.out.println("Continue? y|n: ");
        String value = readLine().trim();
        return (!isValid(value) || value.equalsIgnoreCase("n"));
    }

    private String readLine() {
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException("Error: Unable to read from STDIN");
        }
    }

    private static boolean isValid(String... texts) {
        return UnitTestDatabaseUtil.isValid(texts);
    }

    public static void main(String[] args) {
        try {
            UnitTestDatabaseLoaderCLI cli = new UnitTestDatabaseLoaderCLI();
            cli.begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
