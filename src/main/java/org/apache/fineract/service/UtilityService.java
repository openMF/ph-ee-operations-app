package org.apache.fineract.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.springframework.stereotype.Service;

@Service
public interface UtilityService {

    String getSignature(String toBeHashed, String privateKeyString) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
}
