package spring.bbs.base.domain;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import spring.bbs.member.domain.Member;

@MappedSuperclass
@Getter
public abstract class Written extends BaseEntity {
    @ManyToOne
    protected Member author;
}
