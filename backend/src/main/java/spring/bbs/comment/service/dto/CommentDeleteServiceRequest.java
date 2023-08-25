package spring.bbs.comment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentDeleteServiceRequest {
    private Long commentId;
    private String curMemberName;
}
