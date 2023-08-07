package spring.bbs.jwt.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private Long memberId;
    @Column(nullable = false)
    private String token;

    public RefreshToken(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
    }

    public RefreshToken update(String newRefreshToken){
        this.token = newRefreshToken;
        return this;
    }
}
