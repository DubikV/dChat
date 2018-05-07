package com.gmail.vanyadubik.dchat.service;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class Encrypt {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static String encypt(String message, String key) {
        try {
            if (message == null || key == null) return null;

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char)(mesg[i] ^ keys[i % kl]);
            }

            return encyptBase64(new String(newmsg));
        } catch (Exception e) {
            return null;
        }
    }

    public static String dencypt(String text, String key) {

         String message = decryptBase64(text);
        try {
            if (message == null || key == null) return null;

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char)(mesg[i] ^ keys[i % kl]);
            }

            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }

    private static String encyptBase64(String text){
        try {
            byte[] encrpt = text.getBytes(DEFAULT_ENCODING);
            return Base64.encodeToString(encrpt, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String decryptBase64(String base64){
        try {
            byte[] decrypt= Base64.decode(base64, Base64.DEFAULT);
            return new String(decrypt, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
