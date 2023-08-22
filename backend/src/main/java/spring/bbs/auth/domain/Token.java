package spring.bbs.auth.domain;

public abstract class Token {
    protected String key;
    protected String token;

    public String getKey() {
        return key;
    }

    public String getToken() {
        return token;
    }
}
