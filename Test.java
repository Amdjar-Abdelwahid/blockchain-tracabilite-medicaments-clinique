import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Test {

    // --- COPIED FROM SignatureUtil ---
    public static class SignatureUtil {
        private static final String ALGORITHM = "RSA";
        private static final int KEY_SIZE = 2048;
        private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

        private SignatureUtil() {
        }

        public static Map<String, String> generateKeyPair() {
            try {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
                keyGen.initialize(KEY_SIZE);
                KeyPair pair = keyGen.generateKeyPair();

                String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
                String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

                Map<String, String> keys = new HashMap<>();
                keys.put("privateKey", privateKey);
                keys.put("publicKey", publicKey);
                return keys;
            } catch (Exception e) {
                throw new RuntimeException("Error generating key pair", e);
            }
        }

        public static String sign(String data, String privateKeyBase64) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
                PrivateKey privateKey = kf.generatePrivate(spec);

                Signature rsa = Signature.getInstance(SIGNATURE_ALGORITHM);
                rsa.initSign(privateKey);
                rsa.update(data.getBytes());

                return Base64.getEncoder().encodeToString(rsa.sign());
            } catch (Exception e) {
                throw new RuntimeException("Error signing data", e);
            }
        }

        public static boolean verify(String data, String signatureBase64, String publicKeyBase64) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
                PublicKey publicKey = kf.generatePublic(spec);

                Signature rsa = Signature.getInstance(SIGNATURE_ALGORITHM);
                rsa.initVerify(publicKey);
                rsa.update(data.getBytes());

                return rsa.verify(Base64.getDecoder().decode(signatureBase64));
            } catch (Exception e) {
                return false;
            }
        }
    }
    // --------------------------------

    public static void main(String[] args) {
        System.out.println("--- Starting Standalone Signature Test ---");

        // 1. Generate Keys
        System.out.println("1. Generating Keys...");
        Map<String, String> keys = SignatureUtil.generateKeyPair();
        String priv = keys.get("privateKey");
        String pub = keys.get("publicKey");
        /*
         * if (priv != null && pub != null) {
         * System.out.println("[OK] Keys generated.");
         * } else {
         * System.err.println("[FAIL] Keys missing.");
         * System.exit(1);
         * }
         */

        // 2. Sign Data
        String data = "HashDuBloc123456789";
        System.out.println("2. Signing data: " + data);
        String signature = SignatureUtil.sign(data, priv);
        System.out.println("Signature: " + signature.substring(0, 20) + "...");

        // 3. Verify Valid
        System.out.println("3. Verifying (Expect Valid)...");
        boolean isValid = SignatureUtil.verify(data, signature, pub);
        if (isValid) {
            System.out.println("[OK] Signature valid.");
        } else {
            System.err.println("[FAIL] Signature invalid (unexpected).");
        }

        // 4. Verify Invalid (Tampered Data)
        System.out.println("4. Verifying Tampered Data (Expect Invalid)...");
        boolean isTamperedValid = SignatureUtil.verify(data + "hacked", signature, pub);
        if (!isTamperedValid) {
            System.out.println("[OK] Tampered data rejected.");
        } else {
            System.err.println("[FAIL] Tampered data accepted (Security Breach).");
        }

        System.out.println("--- Test Complete ---");
    }
}
