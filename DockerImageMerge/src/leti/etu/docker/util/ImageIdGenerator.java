package leti.etu.docker.util;

import java.util.Date;
import java.util.Random;

/**
 * Created by lightwave on 17.12.15.
 */
public class ImageIdGenerator {

    public static String generateId() {
        Random generator = new Random((new Date()).getTime());
        byte[] rawName = new byte[32];
        generator.nextBytes(rawName);
        String newId = String.valueOf(bytesToHex(rawName));
        return newId;
    }

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
