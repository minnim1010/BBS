package spring.bbs.common.entity;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import spring.bbs.member.domain.Member;

@MappedSuperclass
@Getter
public abstract class Written extends BaseTime {
    @ManyToOne
    protected Member author;
}
