package org.duracloud.common.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryption utilities.
 *
 * @author Bill Branan
 */
public class EncryptionUtil {

    private static final byte[] keyBytes = "7437018461906678".getBytes();
    private Cipher cipher;
    private Key key;

    /**
     * Initializes EncryptionUtil
     * @throws Exception
     */
    public EncryptionUtil() throws Exception {
        // Create cipher
        this.cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        // Create Key
        DESKeySpec deskey = new DESKeySpec(keyBytes);
        this.key = new SecretKeySpec(deskey.getKey(), "DES");
    }

    /**
     * Provides basic encryption on a String.
     */
    public String encrypt(String toEncrypt) throws Exception {
        byte[] input = toEncrypt.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input);
        return encodeBytes(cipherText);
    }

    /**
     * Provides decryption of a String encrypted using encrypt()
     */
    public String decrypt(String toDecrypt) throws Exception {
        byte[] input = decodeBytes(toDecrypt);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(input);
        return new String(plainText, "UTF-8");
    }

    /**
     * Encodes a byte array as a String without using a charset
     * to ensure that the exact bytes can be retrieved on decode.
     */
    private String encodeBytes(byte[] cipherText) {
        StringBuffer cipherStringBuffer = new StringBuffer();
        for(int i=0; i<cipherText.length; i++){
            byte b = cipherText[i];
            cipherStringBuffer.append(Byte.toString(b)+":");
        }
        return cipherStringBuffer.toString();
    }

    /**
     * Decodes a String back into a byte array.
     */
    private byte[] decodeBytes(String cipherString) {
        String[] cipherStringBytes = cipherString.split(":");
        byte[] cipherBytes = new byte[cipherStringBytes.length];
        for(int i=0; i<cipherStringBytes.length; i++){
            cipherBytes[i] = Byte.parseByte(cipherStringBytes[i]);
        }
        return cipherBytes;
    }

}
