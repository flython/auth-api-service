package top.okfly.auth.infra.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

public abstract class MD5Util {

    public static String encrypt2ToMD5(String str) {
        String hexStr = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(str.getBytes("utf-8"));
            new String(digest);
            hexStr = bytesToHex(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexStr;
    }


    private static String bytesToHex(byte[] bytes) {
        return new BigInteger(1, bytes).toString(16);
    }


}