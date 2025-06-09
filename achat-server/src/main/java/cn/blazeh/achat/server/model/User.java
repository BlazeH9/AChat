package cn.blazeh.achat.server.model;

public class User {

    private final String userId;
    private final String password;
    private final String salt;

    public User(String userId, String password, String salt) {
        this.userId = userId;
        this.password = password;
        this.salt = salt;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

}
