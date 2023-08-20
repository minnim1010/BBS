package spring.bbs.auth.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
            "name='" + name + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
