package spring.bbs.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class JoinRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    @NotBlank
    @Size(min = 8, max=16)
    private String password;
    @NotBlank
    @Size(min = 8, max=16)
    private String checkPassword;
    @Email
    private String email;

    public JoinRequest(String name, String password, String checkPassword, String email) {
        this.name = name;
        this.password = password;
        this.checkPassword = checkPassword;
        this.email = email;
    }
}
