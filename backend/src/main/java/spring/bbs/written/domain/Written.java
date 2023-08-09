package spring.bbs.written.domain;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import spring.bbs.member.domain.Member;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class Written {
    protected LocalDateTime createdTime;
    protected LocalDateTime lastModifiedTime;
    @ManyToOne
    protected Member author;
}
