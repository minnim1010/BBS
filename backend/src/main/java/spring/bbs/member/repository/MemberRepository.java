package spring.bbs.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.bbs.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}
