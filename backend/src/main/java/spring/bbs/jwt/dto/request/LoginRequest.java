package spring.bbs.jwt.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String name;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
