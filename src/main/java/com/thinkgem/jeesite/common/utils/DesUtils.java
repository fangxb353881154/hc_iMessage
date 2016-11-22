package com.thinkgem.jeesite.common.utils;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.imessage.EmojiUtils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.Key;

/**
 * @author Fangxb
 * @version 2016-07-23 17:52
 */
public class DesUtils {
    // 指定DES加密解密所用的密钥
    private static Key key;

    /**
     * 加密key为空
     */
    public DesUtils() {
        setkey(Global.getConfig("des.key"));
    }

    /**
     * 设置加密key
     *
     * @param keyStr
     *            加密key值
     */
    public DesUtils(String keyStr) {
        setkey(keyStr);
    }

    /**
     * 设置加密的校验码
     *
     * @Author:dot
     * @Date:2012-10-16 下午04:25:13
     * @ClassDescription:
     */
    private void setkey(String keyStr) {
        try {
            DESKeySpec objDesKeySpec = new DESKeySpec(keyStr.getBytes("UTF-8"));
            SecretKeyFactory objKeyFactory = SecretKeyFactory.getInstance("DES");
            key = objKeyFactory.generateSecret(objDesKeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 对字符串进行DES加密，返回BASE64编码的加密字符串
    public final String encryptString(String str) {

        byte[] bytes = str.getBytes();
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptStrBytes = cipher.doFinal(bytes);
            return Base64.encodeBase64String(encryptStrBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 对BASE64编码的加密字符串进行解密，返回解密后的字符串
    public final String decryptString(String str) {
        try {
            byte[] bytes = Base64.decodeBase64(str);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            bytes = cipher.doFinal(bytes);
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    // 提供main函数，方便转换加密字符
    public static void main(String[] args) {
        DesUtils des = new DesUtils();

        String s = "车上[[%F0%9F%8C%B9]]很阿豪3d[[%F0%9F%90%AF]]不是[[%F0%9F%90%85]]";
        String ss = des.encryptString(s);
        System.out.println(ss);

        System.out.println(des.decryptString(ss));

        System.out.println(EmojiUtils.emojiRecovery(des.decryptString(ss)));
    }
}
