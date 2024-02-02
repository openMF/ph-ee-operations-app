package org.apache.fineract.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Service
public class UtilityServiceImpl implements UtilityService{
    @Override
    public String getSignature(String tobeHashed, String privateKeyString)throws       NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String hashData = (new DigestUtils("SHA-256")).digestAsHex(tobeHashed);

        Cipher cipher = Cipher.getInstance("RSA");
        PrivateKey publicKey = getPrivateKeyFromString(privateKeyString);
        cipher.init(1, publicKey);
        byte[] cipherText = cipher.doFinal(hashData.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64String(cipherText);
    }

    public static PrivateKey getPrivateKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.decodeBase64(key);
        EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
