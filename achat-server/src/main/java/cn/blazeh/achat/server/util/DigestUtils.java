package cn.blazeh.achat.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class DigestUtils {

    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-512";

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    public static String hashWithSalt(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] passwordHash = digest.digest(password.getBytes());
            byte[] saltBytes = HexFormat.of().parseHex(salt);

            byte[] combined = new byte[passwordHash.length + saltBytes.length];
            System.arraycopy(passwordHash, 0, combined, 0, passwordHash.length);
            System.arraycopy(saltBytes, 0, combined, passwordHash.length, saltBytes.length);

            return HexFormat.of().formatHex(digest.digest(combined));
        } catch (NoSuchAlgorithmException ignored) {
            return "";
        }
    }

}
