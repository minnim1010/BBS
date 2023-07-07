package spring.bbs.member.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.bbs.member.domain.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @EntityGraph(attributePaths = "authority")
    Optional<Member> findWithAuthorityByName(String name);
    Optional<Member> findByName(String name);
    boolean existsByName(String name);
}
