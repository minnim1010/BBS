package spring.bbs.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
public class Member implements UserDetails {
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

    public Member() {
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

    public Member updateOAuthName(String oauthName){
        this.oauthName = oauthName;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.authority.toString()));
    }

    @Override
    public String getUsername() {return email;}

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
