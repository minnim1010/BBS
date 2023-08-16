package spring.bbs.comment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import spring.bbs.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

//    @Query(value = "UPDATE Comment c SET c.groupOrder = c.groupOrder+1" +
//        "WHERE c.post = :post AND c.groupNum = :groupNum AND c.groupOrder >= :updateStartOrder")
//    @Transactional
//    @Modifying
//    void updateOrder(@Param("post") Post post, @Param("groupNum") Long groupNum, @Param("updateStartOrder") int updateStartOrder);

    int countByParentComment(Comment parentComment);
}
