package uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeysHelper {

    public static class Private {
        public static PrivateKey fromBase64(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
        }
    }

    public static class Public {
        public static PublicKey fromBase64(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
        }
    }
}
