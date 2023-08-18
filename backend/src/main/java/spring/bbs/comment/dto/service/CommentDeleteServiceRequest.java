package spring.bbs.comment.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentDeleteServiceRequest {
    private Long commentId;
    private String curMemberName;
}
