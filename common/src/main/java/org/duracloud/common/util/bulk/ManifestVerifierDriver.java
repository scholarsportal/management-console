package org.duracloud.common.util.bulk;

import org.duracloud.common.util.error.ManifestVerifyException;

/**
 * @author Andrew Woods
 *         Date: Oct 24, 2009
 */
public class ManifestVerifierDriver {

    private static void verify(String file0, String file1) {
           ManifestVerifier verifier = new ManifestVerifier(file0, file1);
           try {
               verifier.verify();
               success();
           } catch (ManifestVerifyException e) {
               reportError(e);
           }
       }

       private static void reportError(ManifestVerifyException e) {
           System.out.println(e.getFormatedMessage());
       }

       private static void success() {
           System.out.println("valid");
       }


       private static void usage() {
           StringBuilder sb = new StringBuilder();
           sb.append("Usage: java ManifestVerifierDriver <manifest0> <manifest1>");
           sb.append("\n");
           sb.append("\n\twhere <manifest[0|1]> are files containing only pairs ");
           sb.append("of checksums and entry-names separated by whitespace");
           sb.append("\n");
           sb.append("\n\tExample:");
           sb.append("\n\t");
           sb.append("java ManifestVerifierDriver ");
           sb.append("/tmp/source-bag/manifest-md5.txt ");
           sb.append("/mnt/dura/duracloud-bag/manifest-md5.txt");
           sb.append("\n");
           System.out.println(sb.toString());
       }

    public static void main(String[] args) {
        if (args.length != 2) {
            usage();
            System.exit(1);
        }

        String file0 = args[0];
        String file1 = args[1];
        verify(file0, file1);
    }


}
