package com.myorg.tracemed.util;

import java.util.Map;

public class SignatureTest {
    public static void main(String[] args) {
        System.out.println("--- Starting Signature Test ---");

        // 1. Generate Keys
        System.out.println("1. Generating Keys...");
        Map<String, String> keys = SignatureUtil.generateKeyPair();
        String priv = keys.get("privateKey");
        String pub = keys.get("publicKey");

        if (priv != null && pub != null) {
            System.out.println("[OK] Keys generated.");
        } else {
            System.err.println("[FAIL] Keys missing.");
            System.exit(1);
        }

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

        // 5. Verify Invalid (Wrong Key)
        System.out.println("5. Verifying Wrong Key (Expect Invalid)...");
        Map<String, String> otherKeys = SignatureUtil.generateKeyPair();
        boolean isWrongKeyValid = SignatureUtil.verify(data, signature, otherKeys.get("publicKey"));
        if (!isWrongKeyValid) {
            System.out.println("[OK] Wrong key rejected.");
        } else {
            System.err.println("[FAIL] Wrong key accepted (Security Breach).");
        }

        System.out.println("--- Test Complete ---");
    }
}
