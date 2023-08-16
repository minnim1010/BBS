package spring.bbs.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spring.bbs.base.domain.BaseEntity;
import spring.bbs.member.dto.request.JoinRequest;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String oauthName;
    private String password;
    private String email;
    private boolean isEnabled;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    private Member(Long id, String name, String oauthName, String password, String email, boolean isEnabled, Authority authority) {
        this.id = id;
        this.name = name;
        this.oauthName = oauthName;
        this.password = password;
        this.email = email;
        this.isEnabled = isEnabled;
        this.authority = authority;
    }

    public Member(String name, String password, String email, boolean isEnabled, Authority role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.isEnabled = isEnabled;
        this.authority = role;
    }

    public Member(String oauthName, String email, boolean isEnabled, Authority role) {
        this.oauthName = oauthName;
        this.email = email;
        this.isEnabled = isEnabled;
        this.authority = role;
    }

    public static Member of(JoinRequest req, String roleUser, String encodedPassword, boolean isEnabled) {
        return Member.builder()
            .name(req.getName())
            .email(req.getEmail())
            .password(encodedPassword)
            .authority(Enum.valueOf(Authority.class, roleUser))
            .isEnabled(isEnabled)
            .build();
    }

    public Member updateOAuthName(String oauthName){
        this.oauthName = oauthName;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.authority.toString()));
    }

    @Override
    public String getUsername() {return name;}

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", oauthName='" + oauthName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", isEnabled=" + isEnabled +
                ", authority=" + authority.toString() +
                '}';
    }
}
