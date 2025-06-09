package cn.blazeh.achat.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * 提供字符串加密相关功能，采用SHA-512算法
 */
public final class DigestUtils {

    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-512";

    /**
     * 随机生成一个16字节的盐值
     * @return 16字节的盐值
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /**
     * 给指定字符串加盐加密，具体方法为sha(sha(password)+salt)
     * @param password 要加密的明文
     * @param salt 盐值
     * @return 加密后的密文
     */
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
