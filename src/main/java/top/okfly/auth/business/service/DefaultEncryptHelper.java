package top.okfly.auth.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.okfly.auth.business.domain.Auth;
import top.okfly.auth.business.domain.Token;
import top.okfly.auth.infra.exception.AuthException;
import top.okfly.auth.infra.utils.EncryptUtil;
import top.okfly.auth.infra.utils.MD5Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * actually md 5 is hash algorithm
 * some jwt / aes reference:
 * https://wenku.baidu.com/view/36f11f73bd23482fb4daa58da0116c175f0e1e02.html
 * https://blog.csdn.net/penriver/article/details/118408239
 */
@Slf4j
@Service
public class DefaultEncryptHelper implements PwdEncryptHandler, TokenEncryptHandler {


    public static final String BEARER = "Bearer ";
    private final String aesPwd;
    private final ObjectMapper objectMapper;
    private SecretKeySpec key;

    public DefaultEncryptHelper(@Value("${auth.aes.password:default}") String aesPwd, @Autowired ObjectMapper objectMapper) {
        this.aesPwd = aesPwd;
        this.objectMapper = objectMapper;
        init();
    }


    // MD5
    @Override
    public Auth doEncryptAuth(Auth auth) {
        //reentrant
        if (StringUtils.hasLength(auth.getAuth())) {
            return auth;
        }
        var password = auth.getPassword();
        if (!StringUtils.hasText(auth.getSalt())) {
            auth.setSalt(EncryptUtil.getRandomString(10));
        }
        var salt = auth.getSalt();

        StringBuilder sb = new StringBuilder(password + "c" + salt);
        sb = sb.insert(1, "G");
        sb = sb.insert(2, salt.substring(1));
        sb = sb.insert(4, "5");
        sb = sb.insert(5, salt.substring(2, 4));
        sb = sb.insert(7, "_");
        sb = sb.insert(8, salt.substring(0, 3));
        auth.setAuth(MD5Util.encrypt2ToMD5(sb.toString()));
        return auth;
    }

    //AES
    void init() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(256, new SecureRandom(aesPwd.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            key = new SecretKeySpec(enCodeFormat, "AES");
        } catch (Exception e) {
            log.error("FAILED TO INITIALIZE AES ALGORITHM" + e.getMessage(), e);
        }
    }

    /**
     * using a customize jwt-like algorithm
     * <p>
     * using rsa Encrypt “MD5.{json}.signature”
     * signature using MD5
     *
     * @param token
     */
    @Override
    public void encryptToken(Token token) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("MD5.");
            String json = objectMapper.writeValueAsString(token);
            sb.append(json);
            String sig = MD5Util.encrypt2ToMD5(sb.toString());
            sb.append('.');
            sb.append(sig);
            token.setTokenString(BEARER + bytesToHex(encrypt(sb.toString())));
        } catch (JsonProcessingException ignore) {
        }
    }

    @Override
    public Token decryptTokenStr(String token) {
        try {
            byte[] bytes = decrypt(hexStr2Bytes(token));
            String decrypted = new String(bytes);
            String[] split = decrypted.split("\\.", 3);
            return objectMapper.readValue(split[1], Token.class);
        } catch (Exception e) {
            throw AuthException.of(AuthException.Situation.TOKEN_PARSE_ERROR);
        }
    }

    /**
     * encryption
     *
     * @param content content that needs to be encrypted
     */
    private byte[] encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return result;
        } catch (Exception e) {
            log.error("AES ENCRYPTION FAILED" + e.getMessage(), e);
        }
        return null;
    }


    private byte[] decrypt(byte[] content) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            log.error("AES DECRYPTION FAILED" + e.getMessage(), e);
        }
        return null;
    }


    private byte[] checkByteLength(byte[] byteContent) {
        int length = byteContent.length;
        int remainder = length % 16;
        if (remainder == 0) {
            return byteContent;
        } else {
            return Arrays.copyOf(byteContent, length + (16 - remainder));
        }
    }

    /**
     * convert binary to hex
     *
     * @param buf
     * @return
     */
    private String bytesToHex(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private byte[] hexStr2Bytes(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
